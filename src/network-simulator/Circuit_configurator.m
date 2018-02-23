% CONFIG BEGIN

vasca_1_c    = 10;
vasca_1_r    = 100;
vasca_1_r_in = 100;
vasca_1_sw   = 1;

vasca_2_c    = 10;
vasca_2_r    = 100;
vasca_2_r_in = 100;
vasca_2_sw   = 1;

vasca_3_c    = 10;
vasca_3_r    = 100;
vasca_3_r_in = 100;
vasca_3_sw   = 1;

v_pompa      = 1600;
r_pompa      = 100;

% CONFIG END



% addpath RealTime_Pacer/


i = 0;

% set_param('elementary/v1_c_switch', 'value', 'i');



% 
% % function a = test
% a = timer('Execution Mode', 'Fixed rate', 'Period', 1, 'TimerFcn', @myFun, 'TaskToSchedule', 10);

myMQTT = mqtt('tcp://localhost:1883');


mySub = subscribe(myMQTT,'myMQTT')


while 1
    
%     vasca_1_c    = 10;
%     vasca_1_r    = 100;
%     vasca_1_r_in = 100;
%     vasca_1_sw   = 1;
% 
%     vasca_2_c    = 10;
%     vasca_2_r    = 100;
%     vasca_2_r_in = 100;
%     vasca_2_sw   = 1;
% 
%     vasca_3_c    = 10;
%     vasca_3_r    = 100;
%     vasca_3_r_in = 100;
%     vasca_3_sw   = 1;
    
    
    
end



function myFig = createFigure() 
    figure
    subplot(3,2,1)
    title('Vasca (Tensione)')
    plot(vasca1.time, vasca1.data(:,1))
    ylabel('V')
    
    subplot(3,2,2)
    title('Vasca (Correnti)')
    ylabel('A')
    plot(vasca1.time, vasca1.data(:,2:3))
    subplot(3,2,3)
    
    title(string('Pozzo (Tensione)'))
    ylabel('V')
    plot(pozzo.time, pozzo.data(:,1))
    
    subplot(3,2,4)
    title(string('Pozzo (Correnti)'))
    ylabel('A')
    plot(pozzo.time, pozzo.data(:,2:3))
    
    subplot(3,2,5)
    xlabel('s')
    plot(utilizator.time, (utilizator.Data/(10^9)))
    
    subplot(3,2,6)
    title(string('Corrente utilizzatore'))
    plot(utilizatorFlowRate)
    
end