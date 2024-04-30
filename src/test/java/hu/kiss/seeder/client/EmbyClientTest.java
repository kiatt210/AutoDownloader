package hu.kiss.seeder.client;

import java.net.URL;

import org.junit.Assert;
import org.junit.jupiter.api.Test;

public class EmbyClientTest {

    @Test
    public void testSelectPlayed(){
	URL resource = EmbyClientTest.class.getClassLoader().getResource("emby_test.db");
	System.out.println("resource:"+resource.getPath());
	EmbyClient client = new EmbyClient(resource.getPath());
	Boolean result = client.isWatched("Az.alommelo.S02E25.HUN.WEB-DL.720p.H264-LEGION.mkv");
	Assert.assertTrue(result);
    }

}
