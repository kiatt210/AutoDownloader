package hu.kiss.seeder.client.mqtt;

import org.junit.jupiter.api.Test;

public class PahoClientTest {

    @Test
    public void testSendStart(){
        PahoClient client = new PahoClient();
        client.send("home/state/auto-downloader","on");
    }

    @Test
    public void testSendStop(){
        PahoClient client = new PahoClient();
        client.send("home/state/auto-downloader","off");
    }

}
