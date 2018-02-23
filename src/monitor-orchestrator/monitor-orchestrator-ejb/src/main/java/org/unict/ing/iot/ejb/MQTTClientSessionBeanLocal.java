/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.unict.ing.iot.ejb;

import javax.ejb.Local;

/**
 *
 * @author zartyuk
 */
@Local
public interface MQTTClientSessionBeanLocal {
    
    public void publish(String topic, float val);
    
    public void createConnection();
    
}
