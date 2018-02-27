% CONFIG BEGIN
zones_num = 3;
sectors_per_zone = [ 1, 1, 1 ];

period    = 10; % seconds

vasca_sw     = uint8(1);
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

zone_read_elements = [
      struct('Name', 'v_vasca_%d', 'Type', 'single', 'Port', 1),
      struct('Name', 'i_vasca_%d', 'Type', 'single', 'Port', 1),
      struct('Name', 'i_vasca_%d', 'Type', 'single', 'Port', 2),
      struct('Name', 'Vasca%d/vasca_r_in_st', 'Type', 'single', 'Port', 1),
      struct('Name', 's_vasca_%d', 'Type', 'uint8', 'Port', 1),
];

zone_write_elements = [
      struct('Name', 'Vasca%d/vasca_r_in', 'Type', 'single'),
      struct('Name', 'Vasca%d/trigger/vasca_sw', 'Type', 'uint8'),
];

sector_read_elements = [
      struct('Name', 'flowRateTotSector%d_%d', 'Type', 'single', 'Port', 1),
      struct('Name', 'flowRate%d_%d', 'Type', 'single', 'Port', 1),
      struct('Name', 'triggerStatusSector%d_%d', 'Type', 'uint8', 'Port', 1),
];

classes = {'double','single','int8','uint8','int16','uint16','int32','uint32','int64','uint64'};
sizes = [8,4,1,1,2,2,4,4,8,8];
handles2b = {0, @single2b, 0, @boolean2b, 0, 0, 0, 0, 0, 0};
handles2v = {0, @b2single, 0, @b2boolean, 0, 0, 0, 0, 0, 0};
% CONFIG END



addpath RealTime_Pacer/

% STARTING MQTT (THE NEXT COMMAND WILL CRASH IF DOCKER (OR THE BROKER) IS
% NOT RUNNING
try
    myMQTT = mqtt('tcp://localhost:1883');
catch
    disp("Unable to create MQTT Connection. Is broker reacheable and available?");
end

% OPEN MODEL AND START SIMULATION

open_system(model_name);
set_param(model_name,'SimulationCommand','start');

% RESET THE MODEL (SIMULATION MUST BE RUNNING)
values = string([ vasca_r_in, vasca_sw ]);
for i=1:zones_num
    for k=1:(length(zone_write_elements))
        tmp_index = find(strcmp(classes, zone_write_elements(k).Type));
        tmp = values(k);
        f = cell2mat(handles2v(tmp_index));
        element = sprintf('%s/%s', model_name, sprintf(zone_write_elements(k).Name, i));
        tmp=f(tmp);
        set_param(element, 'value', tmp);
    end
end

% CREATING SUBSCRIPTIONS AND PUBLISH_URLS

subscriptions = containers.Map('KeyType', 'int32', 'ValueType', 'any');
publish_urls = containers.Map('KeyType', 'int32', 'ValueType', 'any');

for i = 1:zones_num
  tmp = struct('Sub', subscribe(myMQTT, sprintf('/actuators/zones/%d/', i)));
  subscriptions(i) = tmp;

  tmp = containers.Map('KeyType', 'int32', 'ValueType', 'any');
  for s = 1:sectors_per_zone(i)
    tmp(s) = sprintf('/sensors/zones/%d/sectors/%d/', i)
  end
  publish_urls(i) = struct('Zone', sprintf('/sensors/zones/%d/', i), 'Sectors', tmp);
end

% INFINITE LOOP TO HANDLE MQTT COMMUNICATION

while 1
    for i=1:zones_num

      % PUBLISH Zone infos
      readings = "";
      for k=1:length(zone_read_elements)
            element = sprintf('%s/%s', model_name, sprintf(zone_read_elements(k).Name, i));
            tmp = get_param(element, 'RuntimeObject');
            tmp = tmp.InputPort(zone_read_elements(k).Port).Data;
            readings = sprintf('%s%s|', readings, num2str(tmp));
      end
      publish(myMQTT, publish_urls(i).Zone, readings);

      % PUBLISH Sectors infos
      readings = "";
      for k=1:length(zone_read_elements)
            element = sprintf('%s/%s', model_name, sprintf(zone_read_elements(k).Name, i));
            tmp = get_param(element, 'RuntimeObject');
            tmp = tmp.InputPort(zone_read_elements(k).Port).Data;
            readings = sprintf('%s%s|', readings, num2str(tmp));
      end
      publish(myMQTT, publish_urls(i).Zone, readings);

      % PUBLISH end

      % READ
      try
        values = strsplit(read(subscriptions(i).Sub), '|');
        %%%display(values);
        for k=1:(length(zone_write_elements))
            % Get type index
            tmp_index = find(strcmp(classes, zone_write_elements(k).Type));
            tmp = values(k);
            % Retrieve deserilization function
            f = cell2mat(handles2v(tmp_index));
            % Convert to the proper value
            tmp = f(tmp);
            element = sprintf('%s/%s', model_name, sprintf(zone_write_elements(k).Name, i));
            %%%display(tmp);
            set_param(element, 'value', tmp);
        end
      catch
      end
      %READ END
    end

    pause(period);
end

% HELPERS

function y = concatb(varargin)
    y = fi(cell2mat(varargin{1,:}));
end

function y = single2b(x)
    y = typecast(single(x), 'uint16');
end

function y = boolean2b(x)
    y = typecast(single(x), 'uint8');
end

function y = b2single(x)
    y = sprintf('single(%s)',x);
end

function y = b2boolean(x)
    y = sprintf('uint8(%s)',x);
end
