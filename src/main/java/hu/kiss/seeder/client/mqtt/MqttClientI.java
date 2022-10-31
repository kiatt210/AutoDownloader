package hu.kiss.seeder.client.mqtt;

public interface MqttClientI {

    public void send(String topic,String message);

}
