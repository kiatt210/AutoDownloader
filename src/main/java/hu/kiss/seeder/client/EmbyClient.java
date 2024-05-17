package hu.kiss.seeder.client;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import hu.kiss.seeder.client.utils.HTTPUtils;

public class EmbyClient {

	private static Logger logger = LogManager.getLogger();
	private final String REQUEST_PATH="http://192.168.0.20:8096/emby/Items";
	private HTTPUtils httpUtils;

	public EmbyClient(HTTPUtils httpUtils) {
	    this.httpUtils = httpUtils;	    
	}

	@SuppressWarnings("unchecked")
	public Boolean isWatched(String fileName) {
	    Map<String,String> headers = new HashMap<>();
	    try{
		URIBuilder builder = new URIBuilder(REQUEST_PATH);
		
		builder.addParameter("api_key", System.getenv("API_KEY"));
		builder.addParameter("Recursive", "true");
		builder.addParameter("IncludeItemTypes", "episode");
		builder.addParameter("EnableUserData", "true");
		builder.addParameter("UserId",System.getenv("USER_ID"));
		builder.addParameter("Path",fileName);

		HttpResponse response = httpUtils.doGet(builder.build().toString(), headers);
		logger.debug("Response code:"+response.getStatusLine().getStatusCode());
		if (response.getStatusLine().getStatusCode() != 200)
			return false;
		var responseStr = EntityUtils.toString(response.getEntity());
		logger.debug("Response str: " + responseStr);
		JSONParser parser = new JSONParser();
		JSONObject obj = (JSONObject) parser.parse(responseStr);

		JSONArray items = (JSONArray)obj.get("Items");
		if (items.isEmpty())
			return false;
		JSONObject item = (JSONObject) items.get(0);
		JSONObject userData = (JSONObject) item.get("UserData");
		Boolean played = (Boolean) userData.get("Played");
		logger.debug(fileName+" is watched: " + played);
		return played;
	    }
	    catch(IOException | URISyntaxException | ParseException e){
		logger.error("Response read error:",e);
	    }

	    return false;
	}
}
