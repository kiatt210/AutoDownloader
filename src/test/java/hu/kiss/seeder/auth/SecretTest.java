package hu.kiss.seeder.auth;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.Collection;

public class SecretTest {

    @Test
    public void testAll(){
	var file = this.getClass().getClassLoader().getResource("secrets.json").getFile();
        Collection secrets =  Secret.all(file);
        Assertions.assertFalse(secrets.isEmpty());
        Assertions.assertEquals(1,secrets.size());
        Secret test = Secret.get("test");
        Assertions.assertFalse(test.getUsername().isEmpty());
        Assertions.assertFalse(test.getPassword().isEmpty());
        Assertions.assertFalse(test.getKey().isEmpty());
    }

}
