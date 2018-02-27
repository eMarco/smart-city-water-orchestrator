/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.unict.ing.iot.ejb;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.ConcurrencyManagement;
import javax.ejb.ConcurrencyManagementType;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import org.unict.ing.iot.utils.helper.MQTTClientImpl;
import org.unict.ing.iot.utils.model.GenericValue;
import org.unict.ing.iot.utils.model.Tank;

/**
 *
 * @author zartyuk
 */
@Singleton
@Startup
@ConcurrencyManagement(ConcurrencyManagementType.CONTAINER)
public class MQTTClientSessionBean implements MQTTClientSessionBeanLocal{

    @EJB
    private MonitorSessionBeanRemote monitorSessionBean;
    
    private MQTTClientImpl myClient;
   
    
    @PostConstruct
    private void init() {
        System.out.println("[MQTT] Creating connection...");
        myClient = new MQTTClientImpl(monitorSessionBean);
        System.out.println("[MQTT] Created connection...");
    }
    
    @PreDestroy
    public void destroyed() {
        myClient = null;
    }
    
    @Override
    public void publish(String topic, GenericValue value) {
        myClient.publish(topic, value);
    }
}
