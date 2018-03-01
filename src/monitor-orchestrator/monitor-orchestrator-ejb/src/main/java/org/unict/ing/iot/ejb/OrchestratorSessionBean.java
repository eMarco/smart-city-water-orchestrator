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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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
    private static final int PERIOD      = 3; //seconds
    private static final int ZONE_MULT   = 2;
    private static final int SECTOR_MULT = 2;
    private static final int TANK_CAP_MIN = 600;
    private static final int TANK_CAP_MAX = 670;
    private static final float VMAX          = 700;
    private static final float DECREMENT_STEP  = (float)0.5;
    private static final float INCREMENT_STEP  = (float)0.1;
    private static int counter           = 0;
    
    private static float F_THRESHOLD     = 0.05f;
    private static float HYSTERESIS_ON   = 0.15f;
    private static float HYSTERESIS_OFF  = 0.25f;
    private static final Map<String, Boolean> mailer = new HashMap<>();

    private static final Logger LOG = Logger.getLogger(OrchestratorSessionBean.class.getName());

    @EJB
    private MonitorSessionBeanRemote monitorSessionBean;

    @Resource
    private SessionContext context;

    @PostConstruct
    private void init() {
        System.err.println("INITINIT" + this.getClass().getClassLoader().toString());

    }

    @Schedule(dayOfMonth = "*", hour = "*", second = "*/2", minute = "*")
    private void tankActuation() {
        List<GenericValue> tanks = monitorSessionBean.getTanks();
        System.err.println(JsonHelper.writeList(tanks));
        
        float _Vtot = 0, _IOtot = 0, _Ptot = 0;
        for (int i = 0; i < tanks.size(); i++) {
            Tank t = ((Tank)(tanks.get(i)));

            _Vtot += (VMAX - t.getCapacity());
            _IOtot += t.getOutputFlowRate();
            _Ptot += (VMAX - t.getCapacity()) *  t.getOutputFlowRate();
        }

        final float Vtot = _Vtot;
        final float IOtot = _IOtot;
        final float Ptot = _Ptot;

                /*
                // c * tank.getOutputFlowRate() is probably too small
                } else if (tank.getOutputFlowRate() < F_THRESHOLD && c > F_THRESHOLD) {         // 2: Tank NOT FULL    w\      NO OUTPUT
                    // Increase RES to yeld on other tanks                                      // RES SMALL increment
                    tank.getValve().increment(1-(c/Vtot));
                } else if (tank.getOutputFlowRate() > F_THRESHOLD && c < F_THRESHOLD) {         // 3: FULL Tank        w\      OUTPUT
                    // Increase RES to yeld on other tanks                                      // RES MEDIUM increment
                    tank.getValve().increment(1-(tank.getOutputFlowRate()/IOtot));
                } else /* (tank.getOutputFlowRate() < F_THRESHOLD && c < F_THRESHOLD) */ //{      // 4: FULL Tank        w\      NO OUTPUT
                    // Increase RES to yeld on other tanks                                      // RES HUGE increment
                /*    tank.getValve().increment(1-((c * tank.getOutputFlowRate())/Ptot));
                }*/
       
        tanks.forEach(tankk -> {

            if (tankk instanceof Tank) {
                Tank tank = (Tank) tankk;
                String log = tank.toString();

                float rj = 0;
                float c = (VMAX - tank.getCapacity());
                //Decide actuation strategy based on Tank capacity and Output
                if (tank.getCapacity() > TANK_CAP_MIN && 
                        tank.getCapacity() < TANK_CAP_MAX && 
                        tank.getOutputFlowRate() > F_THRESHOLD) {
                    rj = Ptot / (c * tank.getOutputFlowRate());
                    tank.getValve().setFlowRateResistance(rj*100);
                    
                } else  if (tank.getCapacity() < TANK_CAP_MIN) {
                    
                    log += "Tank capacity under setPoint_min: DECREMENTING";
                    tank.getValve().decrement(DECREMENT_STEP);
                    
                } else if (tank.getCapacity() > TANK_CAP_MAX) {
                    
                    log += "Tank capacity over setPoint_max: INCREMENTING";
                    tank.getValve().increment(INCREMENT_STEP);
                    
                }

                log += " - Setting input resistance to " + tank.getValve().getFlowRateResistance() + " - ";

                // OUTPUT SWITCH
                if (tank.getCapacity() <= capacityError(true)) {                                // ALMOST EMPTY TANK ==>    FORCE NO OUTPUT
                    
                    log += " CLOSING TRIGGER";
                    tank.getTrigger().close();
                    
                } else if (tank.getCapacity() > capacityError(false)) {                         // ALMOST EMPTY TANK ==>    RE-ENABLE OUTPUT
                    
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
                if (sector.getTankId() == sector.getTankId()) { // TODO: WTF?
                    log += "Checking for Sector "  + sector.getSectorId() + " in zone " + sector.getTankId();
                    float diff = (sector.getFlowRate() - sector.getFlowRateCounted());
                    log += ": diff = outputRate - sectorRate = " + diff;
                    
                    //Problem of water loss?
                    if (diff < flowRateError()) {
                        log += " - Closing trigger - Sending alert";
                        sector.getTrigger().close();
                        String el = String.valueOf(sector.getTankId()) + String.valueOf(sector.getSectorId());
                        System.err.println("WATER LOSS: " + el + mailer);
                        if(!Objects.equals(mailer.get(el), Boolean.TRUE)) {
                            alertSessionBean.SendMail("alessandro+iot@madfarm.it", "Alert on " +sector.getSectorId() + " - Zone: " + sector.getTankId() , "Water LOSS!!");
                            System.err.println("SENDING MAIL FOR " + el);
                            mailer.put(el, Boolean.TRUE);
                        }
                        
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

    @Schedule(dayOfMonth = "*", hour = "*", second = "0", minute = "0")
    private void resetMailer() {
        mailer.clear();
    }
    
    private float flowRateError() {
        return 1;
    }

    private float capacityError(boolean stak) {
        if (stak == false)
            return (float)(HYSTERESIS_ON * VMAX);
        else
            return (float)(HYSTERESIS_OFF * VMAX);
    }

}
