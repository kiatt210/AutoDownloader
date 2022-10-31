package hu.kiss.seeder.test;

import hu.kiss.seeder.auth.Secret;
import hu.kiss.seeder.client.NCoreClient;
import hu.kiss.seeder.client.utils.HTTPUtils;
import hu.kiss.seeder.data.Torrent;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class HTTPCUtilsTest {

    static NCoreClient client;

    @BeforeAll
    public static void init(){
        client = new NCoreClient();
        client.login(Secret.all().get(0));
    }

    @Test
    public void testConnectionRelease(){
        Torrent t = new Torrent("Test");
        t.setId(2319938);
        for(int i=0;i<20;i++){
            String str = client.download(t);
            Assertions.assertNotNull(str);
        }
    }

}
