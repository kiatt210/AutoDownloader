package hu.kiss.seeder.auth;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Collection;

public class SecretTest {

    @Test
    public void testAll(){
        Collection secrets =  Secret.all();
        Assertions.assertFalse(secrets.isEmpty());
        Assertions.assertEquals(2,secrets.size());

        Secret kiatt = Secret.get("kiatt07");
        Assertions.assertFalse(kiatt.getUsername().isEmpty());
        Assertions.assertFalse(kiatt.getPassword().isEmpty());
        Assertions.assertFalse(kiatt.getKey().isEmpty());
    }

}
