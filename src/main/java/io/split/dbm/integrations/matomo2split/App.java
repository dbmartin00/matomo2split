package io.split.dbm.integrations.matomo2split;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Date;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import org.json.JSONArray;
import org.json.JSONObject;

public class App 
{
	
	public static final String MATOMO_API_URL = "https://dbm.matomo.cloud/?module=API" 
			+ "&method=Live.getLastVisitsDetails&idSite=%s&period=day" 
			+ "&date=today&format=json&token_auth=%s"
			+ "&filter_limit=100";
	
    public static void main( String[] args ) throws Exception, IOException
    {
    	Configuration config = Configuration.fromFile("matomo2split.json");
    	
        URI uri = URI.create(String.format(MATOMO_API_URL, config.siteId, config.matomoAuthKey));
        HttpRequest request =  HttpRequest.newBuilder(uri).GET()
                .build();
        System.out.println("INFO - Requesting Matomo events: GET " + uri);

        // Process response
        HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
        if(response.statusCode() >= 300) {
            System.out.println("ERROR - events request failed: status=" + response.statusCode());
            System.out.println("Exiting...");
            System.exit(1);
        } else {
        	System.out.println("INFO - Matomo events query succeeded: " + response.statusCode());
        }
        JSONArray visits = new JSONArray(response.body());
        
//        JSONArray visits = new JSONArray(readFile("response.json"));
		for(int i = 0; i < visits.length(); i++) {
			JSONObject v = visits.getJSONObject(i);
			JSONArray splitEventsArray = new JSONArray();
			if(!v.isNull("userId")) {
				JSONObject splitEvent = new JSONObject();
				splitEvent.put("key", v.getString("userId"));
				splitEvent.put("trafficTypeName", "user");
				splitEvent.put("environmentName", "Prod-Default");
				JSONArray actionsArray = v.getJSONArray("actionDetails");
				
				for(int j = 0; j < actionsArray.length(); j++) {
					JSONObject action = actionsArray.getJSONObject(j);
					long timestamp = action.getLong("timestamp");
					String eventTypeId = action.getString("type");
					
					JSONObject clone = new JSONObject(splitEvent.toString());
					clone.put("timestamp", timestamp * 1000);
					clone.put("eventTypeId", eventTypeId);
					clone.put("properties", action);
					
					splitEventsArray.put(clone);
				}	
			}
//			System.err.println(splitEventsArray.toString(2));
			CreateEvents create = new CreateEvents(config);
			System.out.println("INFO - sending " + splitEventsArray.length() + " events to Split");
//			LOGGER.log(Level.INFO, "sending " + splitEventsArray.length() + " events to split");
//			LOGGER.log(Level.INFO, "" + i);
			create.doPost(splitEventsArray);
		}
    }

	public static String readFile(String path)
			throws IOException
	{
		byte[] encoded = Files.readAllBytes(Paths.get(path));
		return new String(encoded, Charset.defaultCharset());
	}
	
}
