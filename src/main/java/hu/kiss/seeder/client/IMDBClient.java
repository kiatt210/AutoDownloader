package hu.kiss.seeder.client;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.apache.http.HttpResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import hu.kiss.seeder.client.utils.HTTPUtils;

public class IMDBClient {
    private static Logger logger = LogManager.getLogger();
    private final String listId = "ls004643148";
    protected HTTPUtils httpUtils;
    private String list;

    public IMDBClient(){
        this.httpUtils = new HTTPUtils();
        HttpResponse response = httpUtils.doGet("https://imdb.com/list/"+listId+"/export",Map.of());
        logger.debug(response.getStatusLine().getStatusCode());
        list = httpUtils.getContent(response,"\r\n");
    }

    public String getList(){
        return list;
    }

    /**
     * Return random imdb ID-s
     * @param count
     * @return
     */
    public Collection<String> getRandom(int count) {
        Random random = new Random();
        Set<String> result = new HashSet<String>();
        String[] lines = list.split("\r\n");
        while (result.size() < count){
            String line = lines[1+random.nextInt(lines.length-1)];
            result.add(getId(line));
        }
        return result;
    }

    public String getId(String line){
        //"320,tt2404435,2016-10-01,2016-10-01,,The Magnificent Seven,https://www.imdb.com/title/tt2404435/,movie,6.8,132,2016,\"Action, Adventure, Western\",209376,2016-09-08,Antoine Fuqua"
        String[] data = line.split(",");
        String id = data[1];
        return id;
    }
}
