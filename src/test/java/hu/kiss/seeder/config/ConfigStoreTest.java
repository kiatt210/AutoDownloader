package hu.kiss.seeder.config;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;

import hu.kiss.seeder.client.utils.HTTPUtils;

public class ConfigStoreTest {

    private static String sampleConfig = """
	{
	"name":"auto-downloader",
	"profiles":["default"],
	"label":"auto-downloader.yml",
	"version":null,
	"state":null,
	"propertySources":[
	    {"name":"classpath:/auto-downloader.yml",
	    "source":{
		"episode.delete.categories[0]":"category1",
		"episode.delete.extensions[0]":"mkv",
		"episode.delete.extensions[1]":"wmv",
		"episode.delete.folder.emby":"mnt/share1",
		"episode.delete.folder.torrent":"downloads"
		}
	     }
	 ]
        }
	""";
    private static HTTPUtils httpUtils = mock(HTTPUtils.class);
    private static ConfigStore configStore;

    @BeforeAll
    public static void init() throws UnsupportedOperationException, IOException{
	HttpResponse response = mock(HttpResponse.class);
	HttpEntity entity = mock(HttpEntity.class);
	when(entity.getContent()).thenReturn(new ByteArrayInputStream(sampleConfig.getBytes()));
	when(httpUtils.doGet(any(), any())).thenReturn(response);
	when(httpUtils.doGet(any(), any())).thenReturn(response);
	when(response.getEntity()).thenReturn(entity);
	configStore = new ConfigStore(httpUtils);
	
    }

    @Test
    public void testEpisodeDeleteCategories()  {
	var categories = configStore.getEpisodeDeleteCategories();
	assertEquals(1, categories.size());
	assertEquals("category1", categories.get(0));
    }

    @Test
    public void testEpisodeDeteleExtensions(){
	var extensions = configStore.getEpisodeDeleteExtensions();
	assertEquals(2, extensions.size());
	assertEquals("mkv", extensions.get(0));
	assertEquals("wmv", extensions.get(1));
    }

    @Test
    public void testGetEpisodeDeleteFolderEmby(){
	assertEquals("mnt/share1", configStore.getEpisodeDeleteFolderEmby());
    }

    @Test
    public void testGetEpisodeDeleteFolderTorrent(){
	assertEquals("downloads", configStore.getEpisodeDeleteFolderTorrent());
    }

}
