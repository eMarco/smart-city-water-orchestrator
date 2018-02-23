/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.unict.ing.iot.ejb;

import java.util.List;
import javax.ejb.Remote;
import org.unict.ing.iot.utils.model.GenericValue;

/**
 *
 * @author aleskandro - eMarco - cursedLondor
 */
@Remote
public interface MonitorSessionBeanRemote {

    void put(final GenericValue elem);

    List<GenericValue> getZones();
    
    float obtainValue();
    
    void modify(float num);
}
