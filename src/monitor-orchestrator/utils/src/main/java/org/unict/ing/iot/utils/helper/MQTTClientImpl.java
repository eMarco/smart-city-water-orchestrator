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
package org.unict.ing.iot.utils.helper;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.unict.ing.iot.ejb.MonitorSessionBeanRemote;
import org.unict.ing.iot.utils.model.GenericValue;
import org.unict.ing.iot.utils.model.Sector;
import org.unict.ing.iot.utils.model.Tank;

/**
 *
 * @author aleskandro - eMarco - cursedLondor
 */
public class MQTTClientImpl implements MqttCallbackExtended {
    
    private MqttClient client;
    private static final int QOS = 0;
    private static final String BROKER   = "tcp://iot_broker_1:1883";
    private static final String CLIENTID = "MQTTClient10";
    private MonitorSessionBeanRemote subscribedBean;

    public MQTTClientImpl(MonitorSessionBeanRemote subscribedBean) {
        creator(subscribedBean);
    }
    
    public final void creator(MonitorSessionBeanRemote subscribedBean) {
        this.subscribedBean = subscribedBean;
        try {
            client = new MqttClient(BROKER, CLIENTID);
            client.setCallback(this);
            MqttConnectOptions connOpts = new MqttConnectOptions();
            if(!client.isConnected()) {
                System.out.println("Client on");
                client.connect(connOpts);
            }
        } catch (MqttException ex) {
            Logger.getLogger(MQTTClientImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public final void destructor() {
        if (client != null) {
            try {
                client.disconnect();
                client.close();
                client = null;
            } catch (MqttException ex) {
                Logger.getLogger(MQTTClientImpl.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    public void publish(String topic, GenericValue value) {
        try {
            if(client != null && !client.isConnected()) {
                destructor();
                creator(subscribedBean);
            }
            String payload = "";
            if (value instanceof Tank) {
                Tank tank = (Tank)value;

                payload = Float.toString(tank.getValve().getFlowRateResistance());

                if(tank.getTrigger().isOpened() == true)
                    payload = payload + "|1|";
                else 
                    payload = payload + "|0|";
            }
            if (value instanceof Sector) {
                Sector sector = (Sector)value;
                
                if(sector.getTrigger().isOpened() == true)
                    payload = payload + "1|";
                else 
                    payload = payload + "0|";
            }
            System.err.println(payload);
            MqttMessage message = new MqttMessage(payload.getBytes());
            message.setQos(QOS);
            client.publish("/actuators/zones/" + topic, message);
            
        } catch(MqttException me) {
            System.err.println("reason "+me.getReasonCode());
            System.err.println("msg "+me.getMessage());
            System.err.println("loc "+me.getLocalizedMessage());
            System.err.println("excep "+me);
        }
    }
    @Override
    public void connectionLost(Throwable thrwbl) {
        System.err.println("[MQTT] CONNECTION LOST CALLBACK RAN...");
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) {
        System.out.println("Entered in arrived");
        String[] split2 = topic.split("[/]");
        String payload = new String(message.getPayload(), 0);
        System.err.println(topic);
        System.out.println(payload);
        String[] split = payload.split("[|]");
        try {
            if(topic.contains("/sectors")) {
                Sector s = new Sector(Float.parseFloat(split[0]), Float.parseFloat(split[1]), Integer.parseInt(split2[3]), Integer.parseInt(split2[5]));
                
                if(Integer.parseInt(split[2]) == 0) s.getTrigger().setOpened(false);
                else s.getTrigger().setOpened(true);
                System.out.println("[MQTT] Received a message. Topic: " + topic + " Value: " + s.toString());
                try {
                    subscribedBean.put(s);
                } catch(Exception e) {
                    System.err.println("Unable to call subscribedBean; dereferencing client to destroy (maybe an old deploy?)" + e.getMessage());
                    destructor();
                }
            }
            else {
                Tank t = new Tank(Float.parseFloat(split[0]), Float.parseFloat(split[1]), Float.parseFloat(split[2]), Integer.parseInt(split2[3]));
                t.getValve().setFlowRateResistance(Float.parseFloat(split[3]));
                if(Integer.parseInt(split[4]) == 0) t.getTrigger().setOpened(false);
                else t.getTrigger().setOpened(true);
                System.out.println("[MQTT] Received a message. Topic: " + topic + " Value: " + t.toString());
                try {
                    subscribedBean.put(t);
                } catch(Exception e) {
                    System.err.println("Unable to call subscribedBean; dereferencing client to destroy (maybe an old deploy?)" + e.getMessage());
                    destructor();
                }
            }
            System.err.println("[MQTT] All receives ended");
        } catch (NumberFormatException e) {
            System.err.println("Error on Arrived" + e.getMessage());
        }
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken imdt) {
        System.err.println("[MQTT] DELIVERY COMPLETED...");
    }

    @Override
    public void connectComplete(boolean bln, String string) {
        try {
            System.err.println("Subscribing....");
            client.subscribe("/sensors/zones/+/", QOS);
            client.subscribe("/sensors/zones/+/sectors/+/", QOS);
            System.err.println("Subscribed?");
        } catch (MqttException ex) {
            System.err.println(ex.getMessage());
        }
    }
}
