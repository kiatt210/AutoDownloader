package hu.kiss.seeder.client;

import hu.kiss.seeder.client.utils.HTTPUtils;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

public class IMDBClient {
    private static Logger logger = LogManager.getLogger();
    private final String listId = "ls004643148";
    protected HTTPUtils httpUtils;
    private String list;

    public IMDBClient(){
        this.httpUtils = new HTTPUtils();
        ClassicHttpResponse response = httpUtils.doGet("https://imdb.com/list/"+listId+"/export",null);
        logger.debug(response.getCode());
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
