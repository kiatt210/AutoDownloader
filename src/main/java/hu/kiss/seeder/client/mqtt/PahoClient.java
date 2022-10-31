package hu.kiss.seeder.client.mqtt;

import org.eclipse.paho.client.mqttv3.*;

import java.util.UUID;

public class PahoClient implements MqttClientI {

    private static final String SERVER_HOST = "192.168.0.20";
    private IMqttClient publisher;

    public PahoClient(){
        String publisherId = UUID.randomUUID().toString();
        try {
            publisher = new MqttClient("tcp://"+SERVER_HOST+":1883",publisherId);
            connect();
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    private void connect() throws MqttException {
        MqttConnectOptions options = new MqttConnectOptions();
        options.setAutomaticReconnect(true);
        options.setCleanSession(true);
        options.setConnectionTimeout(10);
        options.setUserName("svc_auto_downloader");
        options.setPassword("*wCd`nYntvnKY#xJ.%vt".toCharArray());
        publisher.connect(options);
    }

    @Override
    public void send(String topic, String message) {
        if ( !publisher.isConnected()) {
            return;
        }
        MqttMessage msg = new MqttMessage();
        msg.setQos(0);
        msg.setRetained(true);
        msg.setPayload(message.getBytes());
        try {
            publisher.publish(topic,msg);
        } catch (MqttException e) {
            e.printStackTrace();
        }

    }
}
