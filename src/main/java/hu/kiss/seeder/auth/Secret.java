package hu.kiss.seeder.auth;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class Secret {

    private String username;
    private String password;
    private String key;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public static List<Secret> all(){
        ObjectMapper objectMapper = new ObjectMapper();

        ClassLoader classLoader = Secret.class.getClassLoader();
        URL resource = classLoader.getResource("secrets.json");

        try {

            return objectMapper.readValue(new File(resource.toURI()), new TypeReference<List<Secret>>() {});

        } catch (IOException | URISyntaxException e) {
            throw new RuntimeException(e);
        }

    }

    public static Secret get(String userName){
        return all().stream().filter( f-> f.username.equals(userName)).findFirst().orElse(new Secret());
    }
}
