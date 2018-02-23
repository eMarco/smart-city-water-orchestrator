/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.unict.ing.iot.ejb;

import java.util.List;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.SessionContext;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ejb.Timeout;
import javax.ejb.Timer;
import javax.ejb.TimerConfig;
import javax.ejb.TimerService;
import org.unict.ing.iot.utils.model.GenericValue;
import org.unict.ing.iot.utils.model.Tank;
import org.unict.ing.iot.utils.model.Zone;

/**
 *
 * @author aleskandro - eMarco - cursedLondor
 */
@Singleton
@Startup
public class OrchestratorSessionBean implements OrchestratorSessionBeanLocal {

    @EJB
    private MQTTClientSessionBeanLocal mQTTClientSessionBean;
    /***
     * CONFIGS for the timers of periodically called methods
     */
    private static final int PERIOD    = 15; //seconds
    private static final int ZONE_MULT = 2;
    private static final Logger LOG = Logger.getLogger(OrchestratorSessionBean.class.getName());
    
    @EJB
    private MonitorSessionBeanRemote monitorSessionBean;

    @Resource
    private SessionContext context;
    
    @PostConstruct
    private void init() {
        TimerService timerService = context.getTimerService();
        timerService.getTimers().forEach((Timer t) -> t.cancel());
        timerService.createIntervalTimer(2020, ZONE_MULT * PERIOD * 1000, new TimerConfig("ZONE", true));
        mQTTClientSessionBean.createConnection();
        //timerService.createIntervalTimer(4000, FIXFINGER_MULT * PERIOD * 1000, new TimerConfig("FIXFINGERS", true));
    }

    @Timeout
    public void timeout(Timer timer) {
        if (timer.getInfo().equals("ZONE")) {
            tankActuation();
        }
        /*if (timer.getInfo().equals("FIXFINGERS")) {
            fixFingers();
        }*/
    }
    
    private void tankActuation() {
        List<GenericValue> zones = monitorSessionBean.getZones();
        zones.forEach(zone -> {
            if (zone instanceof Zone) {
                String log = zone.toString();
                Tank tank = ((Zone) zone).getTank();
                float diff = tank.getOutputFlowRate() - tank.getInputFlowRate();
                log += " " + diff;
                if (diff < flowRateError()) {
                    log += " DECREMENTING";
                    tank.getValve().decrement();
                } else {
                    log += " INCREMENTING";
                    tank.getValve().increment();
                }
                if (tank.getCapacity() < capacityError()) {
                    log += " CLOSING TRIGGER";
                    tank.getTrigger().close();
                } else {
                    log += " OPENING TRIGGER";
                    tank.getTrigger().open();
                }
                LOG.warning(log);
            }
        });
    }
    
    
    private float flowRateError() {
        // TODO
        return 1;
    }
    
    private float capacityError() {
        // TODO
        return 1;
    }
    
}
