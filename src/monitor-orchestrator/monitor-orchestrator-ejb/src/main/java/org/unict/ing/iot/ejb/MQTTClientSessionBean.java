/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.unict.ing.iot.ejb;

import java.nio.ByteBuffer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.ConcurrencyManagement;
import javax.ejb.ConcurrencyManagementType;
import javax.ejb.EJB;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.unict.ing.iot.utils.model.Electrovalve;
import org.unict.ing.iot.utils.model.SchmidtTrigger;
import org.unict.ing.iot.utils.model.Tank;

/**
 *
 * @author zartyuk
 */
@Singleton
@Startup
@ConcurrencyManagement(ConcurrencyManagementType.CONTAINER)
public class MQTTClientSessionBean implements MQTTClientSessionBeanLocal, MqttCallback {

    @EJB
    private MonitorSessionBeanRemote monitorSessionBean;
    
    private final String broker   = "tcp://iot_broker_1:1883";
    private final int qos         = 0;
    private final String clientId = "MQTTClient";
    private MqttClient client;
    
    @PostConstruct
    private void init() {
        System.out.println("[MQTT] Creating connection...");
        createConnection();
    }
    
    @Override
    public void publish(String topic, Tank tank) {
        try {
                ByteBuffer bbuf = ByteBuffer.allocate(17);
                bbuf.putFloat(tank.getCapacity());
                bbuf.putFloat(tank.getInputFlowRate());
                bbuf.putFloat(tank.getOutputFlowRate());
                bbuf.putFloat(tank.getValve().getFlowRateResistance());
                if(tank.getTrigger().isOpened()== true) bbuf.put((byte)1);
                else bbuf.put((byte)0);
                //MqttMessage message = new MqttMessage(ByteBuffer.allocate(4).putFloat(val).array());
                MqttMessage message = new MqttMessage(bbuf.array());
                message.setQos(qos);
                client.setCallback(this);
                client.subscribe("/sensors/zones/", qos);
                client.publish("/sensors/zones/" + topic, message);
            
            } catch(MqttException me) {
                System.out.println("reason "+me.getReasonCode());
                System.out.println("msg "+me.getMessage());
                System.out.println("loc "+me.getLocalizedMessage());
                System.out.println("excep "+me);
            }
    }
    
    @Override
    public void createConnection() {
        try {
            client = new MqttClient(broker, clientId);
            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setCleanSession(true);
            if(!client.isConnected()) {
                client.connect(connOpts);
                //client.subscribe("/actuators/zones/example/", qos);
                publish("1/", new Tank(4, 5, 6, 1, new Electrovalve(5), new SchmidtTrigger(true)));
            }
        } catch (MqttException ex) {
            Logger.getLogger(MQTTClientSessionBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    @Lock(LockType.READ)
    public void connectionLost(Throwable thrwbl) {
        System.err.println("[MQTT] CONNECTION LOST CALLBACK RAN...");
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    @Lock(LockType.READ)
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        System.out.println("Entered in arrived");
        String[] split = topic.split("[/]");
        ByteBuffer bbuf = ByteBuffer.wrap(message.getPayload());
        Tank t = new Tank(bbuf.getFloat(), bbuf.getFloat(), bbuf.getFloat(), Integer.parseInt(split[3]));
        t.getValve().setFlowRateResistance(bbuf.getFloat());
        if(bbuf.get() == (byte)0) t.getTrigger().setOpened(false);
        else t.getTrigger().setOpened(true);
        //monitorSessionBean.put(t);
        System.out.println("[MQTT] Received a message. Topic: " + topic + " Value: " + t.toString());
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken imdt) {
        System.err.println("[MQTT] DELIVERY COMPLETED...");
    }
}
