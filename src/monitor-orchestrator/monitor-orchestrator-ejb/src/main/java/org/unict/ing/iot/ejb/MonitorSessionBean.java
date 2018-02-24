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
import javax.ejb.EJB;
import javax.ejb.Stateless;
import org.unict.ing.iot.utils.model.GenericValue;
import org.unict.ing.iot.utils.mongodriver.DBConnectionSingletonSessionBeanLocal;

/**
 *
 * @author aleskandro - eMarco - cursedLondor
 */
@Stateless
public class MonitorSessionBean implements MonitorSessionBeanRemote {

    @EJB
    private MQTTClientSessionBeanLocal mqttClientSessionBean;

    @EJB
    private DBConnectionSingletonSessionBeanLocal db;
    
    private static float number = 0;
    
    /**
     * Used by the MqttSessionBean to put elements in the database
     * @param elem 
     */
    @Override
    public void put(final GenericValue elem) {
        db.getStorage().insert(elem);
    }

    /**
     * Used by the OrchestratorSessionBean to get the zones
     * @return 
     */
    
    @Override
    public List<GenericValue> getZones() {
        // TODO only the last one record for each zone
        return db.getStorage().findByClassName("Zone");
    }
    
    @Override
    public List<GenericValue> getTanks() {
        return db.getStorage().findLastTanks();
    }
    
    @Override
    public void modify(float num) {
        number = num;
    }
    
    @Override
    public float obtainValue() {
        return number;
    }
    /**
     * TODO Methods for RestAPIs
     */
}
