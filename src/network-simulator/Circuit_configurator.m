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

model_name = 'Rete';
zone_name = 'Vasca%d';
read_elements = [
      struct('Name', 'v_vasca_%d', 'Type', 'single', 'Port', 1),
      struct('Name', 'i_vasca_%d', 'Type', 'single', 'Port', 1),
      struct('Name', 'i_vasca_%d', 'Type', 'single', 'Port', 2),
      struct('Name', 'Vasca%d/vasca_r_in_st', 'Type', 'single', 'Port', 1),
      struct('Name', 's_vasca_%d', 'Type', 'uint8', 'Port', 1),
];

write_elements = [
      struct('Name', 'Vasca%d/vasca_r_in', 'Type', 'single'),
      struct('Name', 'Vasca%d/trigger/vasca_sw', 'Type', 'uint8'),
];

% CONFIG END

classes = {'double','single','int8','uint8','int16','uint16','int32','uint32','int64','uint64'};
sizes = [8,4,1,1,2,2,4,4,8,8];
handles2b = {0, @single2b, 0, @boolean2b, 0, 0, 0, 0, 0, 0};
handles2v = {0, @b2single, 0, @b2boolean, 0, 0, 0, 0, 0, 0};

addpath RealTime_Pacer/

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
      % READ

      % try
        bytes = uint8(read(subscriptions(i).Sub));
      % catch
      % end

      start_byte = 0;
      for k=length(write_elements):-1:1
        % Get type index
        tmp_index = find(strcmp(classes, read_elements(k).Type));

        % Calculate end bit on size basis
        end_byte = start_byte + sizes(tmp_index) - 1;
        tmp = bytes(start_byte:end_byte);

        % Retrieve deserilization function
        f = handles2v(tmp_index);
        f = cell2mat(f);

        % Convert to value
        tmp = f(tmp);

        element = sprintf('%s/%s', model_name, sprintf(write_elements(k).Name, i));
        set_param(element, 'value', tmp);

        start_byte = end_bit + 1;
      end

      %READ END
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
  y = single(x);
end

function y = b2boolean(x)
  y = uint8(x);
end
