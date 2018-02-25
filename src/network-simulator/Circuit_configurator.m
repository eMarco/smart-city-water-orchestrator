% CONFIG BEGIN
zones_num = 3;
period = 10; % seconds

vasca_1_c    = 10;
vasca_1_r    = 100;
vasca_r_in = single(100);
vasca_sw   = uint8(1);

vasca_2_c    = 10;
vasca_2_r    = 100;
vasca_2_r_in = single(100);
vasca_2_sw   = uint8(1);

vasca_3_c    = 10;
vasca_3_r    = 100;
vasca_3_r_in = single(100);
vasca_3_sw   = uint8(1);

v_pompa      = 1600;
r_pompa      = 100;

% float capacity;
% float inputFlowRate;
% float outputFlowRate;
% float valve;
% bool schmidtTrigger;

model_name = 'Rete'
zone_name = 'Vasca%d'
read_elements = [
      struct('Name', 'Voltimeter/v_vasca', 'Type', 'single'),
      struct('Name', 'iFlowRate', 'Type', 'single'),
      struct('Name', 'flowRate', 'Type', 'single'),
      struct('Name', 'Variable Resistor', 'Type', 'single'),
      struct('Name', 'Switch/sw_vasca', 'Type', 'uint8'),
]

write_elements = [
      struct('Name', 'Variable Resistor', 'Type', 'single'),
      struct('Name', 'Switch/sw_vasca', 'Type', 'uint8'),
]

% CONFIG END

classes = {'double','single','int8','uint8','int16','uint16','int32','uint32','int64','uint64'};
sizes = [8,4,1,1,2,2,4,4,8,8];
handles2b = {0, @single2b, 0, @boolean2b, 0, 0, 0, 0, 0, 0};
handles2v = {0, @b2single, 0, @b2boolean2b, 0, 0, 0, 0, 0, 0};

addpath RealTime_Pacer/

% zone = sprintf(zone_name, i);
% sprintf('%s/%s/%s', model_name, zone, param_name);

% i = 0;
% set_param('elementary/v1_c_switch', 'value', 'i');

% % function a = test
% a = timer('Execution Mode', 'Fixed rate', 'Period', 1, 'TimerFcn', @myFun, 'TaskToSchedule', 10);

myMQTT = mqtt('tcp://localhost:1883');

subscriptions = containers.Map('KeyType', 'int32', 'ValueType', 'any');
publish_urls = containers.Map('KeyType', 'int32', 'ValueType', 'any');

for i = 1:zones_num
  tmp = struct('Sub', subscribe(myMQTT, sprintf('/actuators/zones/%d/', i)));
  subscriptions(i) = tmp;

  publish_urls(i) = sprintf('/sensors/zones/%d/', i);
end


while 1
    % PUBLISH
    for i=1:zones_num
      zone = sprintf(zone_name, i);

      readings = {}
      for k=1:length(read_elements)
        element = sprintf('%s/%s/%s', model_name, zone, read_elements(k).Name);

        % TODO : FIX ME!
        tmp = get_param(element, 'SignalObject');

        % Get type index
        tmp_index = find(strcmp(sizes, write_elements(i).Type));

        % Retrieve serialization function
        f = handles2b(tmp_index);

        % Append the binary data
        readings{k} = f(tmp);
      end

      payload = concatb({readings});

      try
        publish(myMQTT, publish_urls(i), payload);
      catch
      end

      % PUBLISH end
      % READ

      % try
        % TODO : does the read return bits?
        bits = read(subscriptions{k}.Sub);
      % catch
      % end

      start_bit = 0;
      for k=length(write_elements):-1:1

        % Get type index
        tmp_index = find(strcmp(sizes, write_elements(i).Type));

        % Calculate end bit on size basis
        end_bit = start_bit + sizes(tmp_index) * 8 - 1;
        tmp = bitsliceget(bits, start_bit, end_bit);

        % Retrieve deserilization function
        f = handles2v(tmp_index);
        % Convert to value
        tmp = f(tmp);

        set_param(sprintf('%s/%s/%s', model_name, zone, write_elements(k).Name), 'value', tmp);

        start_bit = end_bit + 1;
      end

      %READ END
    end

    pause(period);
end


function y = concatb(varargin)
  y = fi(varargin);
end

function y = single2b(x)
   y = de2bi(typecast(single(x), 'uint32'), 32);
end

function y = boolean2b(x)
   y = de2bi(typecast(single(x), 'uint8'), 8);
end

function y = b2single(x)
  y = typecast(bi2de(tmp), 'single')
end

function y = b2boolean(x)
  y = typecast(bi2de(tmp), 'uint8')
end
