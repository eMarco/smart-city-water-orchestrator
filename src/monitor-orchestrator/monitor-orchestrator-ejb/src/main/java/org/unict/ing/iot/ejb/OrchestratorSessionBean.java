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
import javax.ejb.Schedule;
import javax.ejb.SessionContext;
import javax.ejb.Singleton;
import javax.ejb.Startup;
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
    private AlertSessionBeanLocal alertSessionBean;

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
    private static float F_THRESHOLD     = (float)0.05;
    
    private static final Logger LOG = Logger.getLogger(OrchestratorSessionBean.class.getName());
    
    @EJB
    private MonitorSessionBeanRemote monitorSessionBean;

    @Resource
    private SessionContext context;
    
    @PostConstruct
    private void init() {
        System.err.println("INITINIT" + this.getClass().getClassLoader().toString());
        
    }
    
    @Schedule(dayOfMonth = "*", hour = "*", second = "*/" + PERIOD * ZONE_MULT, minute = "*")
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
                    float rj;
                    float c = (VMAX - tank.getCapacity());
                    if (c > F_THRESHOLD && tank.getOutputFlowRate() > F_THRESHOLD) {
                        rj = Vtot2 / (c * tank.getOutputFlowRate());
                        tank.getValve().setFlowRateResistance(rj);
                        log += " - Setting input resistance to " + rj + " - ";
                    } else if (tank.getOutputFlowRate() < F_THRESHOLD && c > F_THRESHOLD) { // Full tank
                        tank.getValve().increment();
                    } else if (tank.getOutputFlowRate() > F_THRESHOLD && c < F_THRESHOLD) { 
                        
                    } else {
                        
                    }
                    
                    
                    counter = 1;
                    
                } else {
                    float diff = tank.getInputFlowRate() - tank.getOutputFlowRate();
                    log += " " + diff;
                   
                    if (diff > flowRateError()) {
                        log += " DECREMENTING (RUBINETT)";
                        tank.getValve().increment();
                    } else {
                        log += " INCREMENTING (RUBINETT)";
                        tank.getValve().decrement();
                    }
                    
                    counter++;
                }
                         
                if (tank.getCapacity() < capacityError(true)) {
                    log += " CLOSING TRIGGER";
                    tank.getTrigger().close();
                } else if (tank.getCapacity() > capacityError(false)) {
                    log += " OPENING TRIGGER";
                    tank.getTrigger().open();
                }
                
                mQTTClientSessionBean.publish(tank.getTankId() + "/", tank);
                LOG.warning(log);
            }
        });
    }
    
    @Schedule(dayOfMonth = "*", hour = "*", second = "*/" + PERIOD * SECTOR_MULT, minute = "*")
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
                        log += " - Closing trigger - Sending alert";
                        sector.getTrigger().close();
                        alertSessionBean.SendMail("alessandro+iot@madfarm.it", "Alert on " +sector.getSectorId() + " - Zone: " + sector.getTankId() , "Water LOSS!!");
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
    
    private float capacityError(boolean stak) {
        if (stak == false)
            return (float)(0.15 * VMAX);
        else
            return (float)(0.25 * VMAX);
    }
    
}
