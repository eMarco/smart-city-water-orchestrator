% CONFIG BEGIN
zones_num = 3;
period = 10; % seconds

vasca_1_c    = 10;
vasca_1_r    = 100;
vasca_1_r_in = single(100);
vasca_1_sw   = uint8(1);

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

% CONFIG END

addpath RealTime_Pacer/

% float capacity;
% float inputFlowRate;
% float outputFlowRate;
% float valve;
% bool schmidtTrigger;

model_name = 'Rete'
zone_name = 'Vasca%d'
read_elements = ['Voltimeter/v_vasca', 'iFlowRate', 'flowRate', 'Variable Resistor', 'Switch/sw_vasca']

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

  publish_urls(i) = sprintf('/sensors/zones/${i}/', i);
end




while 1
    try
      % publish(myMQTT, 'myMQTT', 'testMessage');
      % pause(1);
      % test = read(mySub);
      readings = {}

      for k=1:length(subscriptions)
        subscription=subscriptions{k};
        readings{k} = read(mySub);
      end

    catch
    end

    pause(period);
end



function y = actuate(bits)
  y = fi(varargin);
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
