package hu.kiss.seeder.config;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import hu.kiss.seeder.client.utils.HTTPUtils;


public class ConfigStore {

    private static Logger logger = LogManager.getLogger();
    private static final String CONFIG_URL="http://config-server:8888/auto-downloader/default/auto-downloader.yml";
    private HTTPUtils httpUtils;
    private List<String> episodeDeleteCategories;
    private List<String> episodeDeleteExtensions;
    private String episodeDeleteFolderEmby;
    private String episodeDeleteFolderTorrent;
    

    public ConfigStore(HTTPUtils httpUtils){
	this.httpUtils=httpUtils;
	loadConfig();
    }

    private void loadConfig(){
	var response = this.httpUtils.doGet(CONFIG_URL, null);
	try{
	    var responseStr = EntityUtils.toString(response.getEntity());
	    logger.debug("Response str: " + responseStr);
	    JSONParser parser = new JSONParser();

	    JSONObject obj = (JSONObject) parser.parse(responseStr);

	    JSONArray sources = (JSONArray) obj.get("propertySources");
	    JSONObject config = (JSONObject) ((JSONObject) sources.get(0)).get("source");

	    episodeDeleteCategories = parseList(config, "episode.delete.categories");
	    episodeDeleteExtensions = parseList(config, "episode.delete.extensions");
	    episodeDeleteFolderEmby = (String) config.get("episode.delete.folder.emby");
	    episodeDeleteFolderTorrent = (String) config.get("episode.delete.folder.torrent");
	}
	catch(ParseException | IOException e){
	   logger.error("Parse exception:",e);
	}
    }

    private List<String> parseList(JSONObject object, String key){
	List<String> result = new ArrayList<>();
	for(int i=0;object.containsKey(key+"["+i+"]");i++){
	    result.add((String) object.get(key+"["+i+"]"));
	}

	return result;
    }

	public List<String> getEpisodeDeleteCategories() {
		return episodeDeleteCategories;
	}

	public List<String> getEpisodeDeleteExtensions() {
		return episodeDeleteExtensions;
	}

	public String getEpisodeDeleteFolderEmby() {
		return episodeDeleteFolderEmby;
	}

	public String getEpisodeDeleteFolderTorrent() {
		return episodeDeleteFolderTorrent;
	}

}
