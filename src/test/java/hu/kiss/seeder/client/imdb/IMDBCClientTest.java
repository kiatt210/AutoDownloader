package hu.kiss.seeder.client.imdb;

import hu.kiss.seeder.client.IMDBClient;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.List;

public class IMDBCClientTest {

    private static IMDBClient client;

    @BeforeAll
    public static void init(){
        client = new IMDBClient();
    }

    @Test
    public void downloadCsv(){
        String data = client.getList();
        Assert.assertFalse(data.isEmpty());
    }

    @Test
    public void getRandom(){
        Collection<String> movies = client.getRandom(4);
        Assert.assertEquals(4,movies.size());
    }

    @Test
    public void createQuery(){
        String query = client.getId("320,tt2404435,2016-10-01,2016-10-01,,The Magnificent Seven,https://www.imdb.com/title/tt2404435/,movie,6.8,132,2016,\"Action, Adventure, Western\",209376,2016-09-08,Antoine Fuqua");
        Assert.assertEquals("tt2404435",query);
    }

}
