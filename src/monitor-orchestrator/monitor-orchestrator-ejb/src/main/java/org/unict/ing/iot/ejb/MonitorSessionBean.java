/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
    private DBConnectionSingletonSessionBeanLocal db;

    
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
        return db.getStorage().findByClassName("Zone");
    }
    
    
    /**
     * TODO Methods for RestAPIs
     */
}