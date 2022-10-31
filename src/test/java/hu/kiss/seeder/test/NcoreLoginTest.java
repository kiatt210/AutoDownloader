package hu.kiss.seeder.test;

import hu.kiss.seeder.auth.Secret;
import hu.kiss.seeder.client.NCoreClient;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertTimeout;


public class NcoreLoginTest {

    @Test
//    @Timeout(value = 2000, unit = TimeUnit.MILLISECONDS)
    public void login(){
        Configurator.setRootLevel(Level.DEBUG);
        NCoreClient ncClient = new NCoreClient();
        assertTimeout(Duration.ofSeconds(4), () -> ncClient.login(Secret.all().get(0)));

    }

}
