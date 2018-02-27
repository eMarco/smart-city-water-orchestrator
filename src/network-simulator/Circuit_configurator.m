% CONFIG BEGIN
speedup      = num2str(50);

zones_num    = 3;
sectors_per_zone = [ 1, 1, 1 ];

period       = 5; % seconds

vasca_sw     = uint8(1);
vasca_r_in   = single(zones_num);

vasca_1_c    = 10;
vasca_1_r    = 100;

vasca_2_c    = 10;
vasca_2_r    = 100;

vasca_3_c    = 10;
vasca_3_r    = 100;

v_pompa      = 700;
r_pompa      = 100;



% Initial conditions
vasca_1_initial_voltage = 700;
vasca_2_initial_voltage = 700;
vasca_3_initial_voltage = 700;

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

sector_write_elements = [
      struct('Name', 'Vasca%d/trigger/vasca_sw', 'Type', 'uint8'),
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
set_param('Rete/pacer', 'simTimePerRealTime', speedup);
set_param(model_name,'SimulationCommand','start');
%input('Start simulation and press enter > ', 's');

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
publish_topics = containers.Map('KeyType', 'int32', 'ValueType', 'any');

for i = 1:zones_num
  % Subscribe
  tmp = containers.Map('KeyType', 'int32', 'ValueType', 'any');
  for s = 1:sectors_per_zone(i)
    tmp(s) = subscribe(myMQTT, sprintf('/actuators/zones/%d/sectors/%d/', i, s));
  end
  subscriptions(i) = struct('Sub', subscribe(myMQTT, sprintf('/actuators/zones/%d/', i)), 'Sectors', tmp);

  % Create publish topics
  tmp = containers.Map('KeyType', 'int32', 'ValueType', 'any');
  for s = 1:sectors_per_zone(i)
    tmp(s) = sprintf('/sensors/zones/%d/sectors/%d/', i, s);
  end
  publish_topics(i) = struct('Zone', sprintf('/sensors/zones/%d/', i), 'Sectors', tmp);
end

% INFINITE LOOP TO HANDLE MQTT COMMUNICATION

while 1
    pause(period);
    for i=1:zones_num

      % PUBLISH
      % PUBLISH Zone
      readings = "";
      for k=1:length(zone_read_elements)
            element = sprintf('%s/%s', model_name, sprintf(zone_read_elements(k).Name, i));
            tmp = get_param(element, 'RuntimeObject');
            tmp = tmp.InputPort(zone_read_elements(k).Port).Data;
            readings = sprintf('%s%s|', readings, num2str(tmp));
      end
      publish(myMQTT, publish_topics(i).Zone, readings);

      % PUBLISH Sectors
      sectors_publish_url = publish_topics(i).Sectors;

      for s = 1:sectors_per_zone(i)
        readings = "";
        for k=1:length(sector_read_elements)
              element = sprintf('%s/%s', model_name, sprintf(sector_read_elements(k).Name, i, s));
              tmp = get_param(element, 'RuntimeObject');
              tmp = tmp.InputPort(sector_read_elements(k).Port).Data;
              readings = sprintf('%s%s|', readings, num2str(tmp));
        end
        publish(myMQTT, sectors_publish_url(i), readings);
      end

      % PUBLISH end

      % READ
      try

        % READ Zone
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
            display(tmp);
            set_param(element, 'value', tmp);
        end

        % READ Sectors
        for s = 1:sectors_per_zone(i)
          for k=1:(length(sector_write_elements))
              % Get type index
              tmp_index = find(strcmp(classes, sector_write_elements(k).Type));
              tmp = values(k);
              % Retrieve deserilization function
              f = cell2mat(handles2v(tmp_index));
              % Convert to the proper value
              tmp = f(tmp);
              element = sprintf('%s/%s', model_name, sprintf(sector_write_elements(k).Name, i, s));
              %%%display(tmp);
              set_param(element, 'value', tmp);
          end
        end

      catch
      end
      %READ END
    end


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
