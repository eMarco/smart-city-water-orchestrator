/* 
 * Copyright (C) 2018 aleskandro - eMarco - cursedLondor
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.unict.ing.iot.ejb;

import java.util.List;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.EJBTransactionRolledbackException;
import javax.ejb.SessionContext;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ejb.Timeout;
import javax.ejb.Timer;
import javax.ejb.TimerConfig;
import javax.ejb.TimerService;
import org.unict.ing.iot.utils.helper.JsonHelper;
import org.unict.ing.iot.utils.model.GenericValue;
import org.unict.ing.iot.utils.model.Tank;
import org.unict.ing.iot.utils.model.Sector;

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
    private static final int PERIOD    = 3; //seconds
    private static final int ZONE_MULT = 2;
    private static final int SECTOR_MULT = 2;
    private static int counter         = 0;
    private static float VMAX          = 700;
    
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
        timerService.createIntervalTimer(2025, SECTOR_MULT * PERIOD * 1000, new TimerConfig("SECTOR", true));
    }

    @Timeout
    public void timeout(Timer timer) {
        if (timer.getInfo().equals("ZONE")) {
            try {
                tankActuation();
            }
            catch (EJBTransactionRolledbackException e)  {
               timer.cancel();
            }
        }
       if (timer.getInfo().equals("SECTOR")) {
            try {
                sectorActuation();
            }
            catch (EJBTransactionRolledbackException e)  {
               timer.cancel();
            }
        }
    }
    
    private void tankActuation() {
        List<GenericValue> tanks = monitorSessionBean.getTanks(); 
        System.err.println(JsonHelper.writeList(tanks));
        float Vtot = 0;
        for (int i = 0; i < tanks.size(); i++) {
            Tank t = ((Tank)(tanks.get(i)));
            Vtot += (VMAX - t.getCapacity()) *  t.getOutputFlowRate();
        }
        final float Vtot2 = Vtot;
        tanks.forEach(tankk -> { 
            
            if (tankk instanceof Tank) {
                Tank tank = (Tank) tankk;
                String log = tank.toString();
                if (counter % 5 == 0) {
                    float rj = Vtot2 / ((VMAX - tank.getCapacity()) * tank.getOutputFlowRate());
                    tank.getValve().setFlowRateResistance(rj);
                    //counter = 1;
                } else {
                    /*float diff = tank.getInputFlowRate() - tank.getOutputFlowRate();
                    log += " " + diff;
                    if (diff > flowRateError()) {
                        log += " DECREMENTING (RUBINETT)";
                        tank.getValve().increment();
                    } else {
                        log += " INCREMENTING (RUBINETT)";
                        tank.getValve().decrement();
                    }*/
                    //counter++;
                }
                        
                if (tank.getCapacity() < capacityError()) {
                    log += " CLOSING TRIGGER";
                    tank.getTrigger().close();
                } else {
                    log += " OPENING TRIGGER";
                    tank.getTrigger().open();
                }
                
                mQTTClientSessionBean.publish(tank.getTankId() + "/", tank);
                LOG.warning(log);
            }
        });
    }
    
    private void sectorActuation() { 
        List<GenericValue> sectors = monitorSessionBean.getSectors(); 
        
        sectors.forEach((s) -> {
            String log = "";
            if (s instanceof Sector) {
                Sector sector = (Sector)s;
                if (sector.getTankId() == sector.getTankId()) {
                    log += "Checking for Sector "  + sector.getSectorId() + " in zone " + sector.getTankId();
                    float diff = (sector.getFlowRate() - sector.getFlowRateCounted());
                    log += ": diff = outputRate - sectorRate = " + diff;
                    if (diff < flowRateError()) {
                        log += " - Closing trigger";
                        sector.getTrigger().close();
                    } else {
                        log += " - Opening trigger";
                        sector.getTrigger().open();
                    }
                    mQTTClientSessionBean.publish(sector.getTankId() + "/sectors/" + sector.getSectorId() + "/", sector);
                    LOG.warning(log);
                }
            }
        });
    }
    private float flowRateError() {
        // TODO
        return 1;
    }
    
    private float capacityError() {
        return (float)(0.15 * VMAX);
    }
    
}
