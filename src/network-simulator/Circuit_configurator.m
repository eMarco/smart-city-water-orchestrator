% CONFIG BEGIN
zones_num = 3;
period = 10; % seconds

vasca_sw   = uint8(1);
vasca_r_in   = single(100);

vasca_1_c    = 10;
vasca_1_r    = 100;

vasca_2_c    = 10;
vasca_2_r    = 100;

vasca_3_c    = 10;
vasca_3_r    = 100;

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
      struct('Name', 'v_vasca_%d', 'Type', 'single', 'Port', 1),
      struct('Name', 'i_vasca_%d', 'Type', 'single', 'Port', 1),
      struct('Name', 'i_vasca_%d', 'Type', 'single', 'Port', 2),
      struct('Name', 'Vasca%d/vasca_r_in_st', 'Type', 'single', 'Port', 1),
      struct('Name', 's_vasca_%d', 'Type', 'uint8', 'Port', 1),
]

write_elements = [
      struct('Name', 'Vasca%d/vasca_r_in', 'Type', 'single'),
      struct('Name', 'Vasca%d/trigger/vasca_sw', 'Type', 'uint8'),
]

% CONFIG END

classes = {'double','single','int8','uint8','int16','uint16','int32','uint32','int64','uint64'};
sizes = [8,4,1,1,2,2,4,4,8,8];
handles2b = {0, @single2b, 0, @boolean2b, 0, 0, 0, 0, 0, 0};
handles2v = {0, @b2single, 0, @b2boolean, 0, 0, 0, 0, 0, 0};

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
    for i=1:1
        display("START CONVERSION")
      readings = {};
      for k=1:length(read_elements)
        
        element = sprintf('%s/%s', model_name, sprintf(read_elements(k).Name, i));
        
        tmp = get_param(element, 'RuntimeObject');
        tmp = tmp.InputPort(read_elements(k).Port).Data;
        % Get type index
        tmp_index = find(strcmp(classes, read_elements(k).Type));

        % Retrieve serialization function
        f = handles2b(tmp_index);
        f = cell2mat(f);
        % Append the binary data
        readings{k} = f(tmp);
      end
      %try
        publish(myMQTT, publish_urls(i), char(cell2mat(readings)));
      %catch
        
      %end

      % PUBLISH end
%       % READ
% 
%       % try
%         % TODO : does the read return bits?
%         bits = read(subscriptions{k}.Sub);
%       % catch
%       % end
% 
%       start_bit = 0;
%       for k=length(write_elements):-1:1
% 
%         % Get type index
%         tmp_index = find(strcmp(sizes, write_elements(i).Type));
% 
%         % Calculate end bit on size basis
%         end_bit = start_bit + sizes(tmp_index) * 8 - 1;
%         tmp = bitsliceget(bits, start_bit, end_bit);
% 
%         % Retrieve deserilization function
%         f = handles2v(tmp_index);
%         % Convert to value
%         tmp = f(tmp);
% 
%         set_param(sprintf('%s/%s/%s', model_name, zone, write_elements(k).Name), 'value', tmp);
% 
%         start_bit = end_bit + 1;
%       end
% 
%       %READ END
    end

    pause(period);
end

function y = concatb(varargin)
  y = fi(cell2mat(varargin{1,:}));
end

function y = single2b(x)
   y = typecast(single(x), 'uint8');
end

function y = boolean2b(x)
   y = typecast(uint8(x), 'uint8');
end

function y = b2single(x)
  y = typecast(tmp, 'single')
end

function y = b2boolean(x)
  y = typecast(tmp, 'uint8')
end
