package hu.kiss.seeder.client;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.JsonObject;

import org.apache.http.HttpResponse;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.json.simple.JSONArray;

import hu.kiss.seeder.client.utils.HTTPUtils;

public class EmbyClient {
	//curl -X 'GET'   'http://192.168.0.20:8096/emby/Items?api_key=e6b02179ed994015a2c564f963c60230&Recursive=true&IncludeItemTypes=episode&Path=%2Fmnt%2Fshare1%2FCapak.Kozott%2FSeason.7%2FCapak.kozott.S07E10.720p.RTLP.WEB-DL.AAC2.0.H.264.HUN-FULCRUM%2Ffulcrum-capak.kozott.s07e10.720p.web.mkv&EnableUserData=true&UserId=5242342ffa0548a58a991108687069ef' -H 'accept: application/json'| jq

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
