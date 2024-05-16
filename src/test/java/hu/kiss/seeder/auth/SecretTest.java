package hu.kiss.seeder.auth;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.Collection;

public class SecretTest {

    @Test
    public void testAll(){

        Collection secrets =  Secret.all();
        Assertions.assertFalse(secrets.isEmpty());
        Assertions.assertEquals(1,secrets.size());
        Secret test = Secret.get("test");
        Assertions.assertFalse(test.getUsername().isEmpty());
        Assertions.assertFalse(test.getPassword().isEmpty());
        Assertions.assertFalse(test.getKey().isEmpty());
    }

}
