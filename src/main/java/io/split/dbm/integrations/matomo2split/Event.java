package io.split.dbm.integrations.matomo2split;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

// Dead code
public class Event {
    @SuppressWarnings("unused")
	private static final SimpleDateFormat SERVER_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
    @SuppressWarnings("unused")
	private static final SimpleDateFormat SERVER_FORMAT_2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public final String key;
    public final String eventTypeId;
    public final String trafficTypeName;
    public final String environmentName;
    public final Map<String, Object> properties;
    public final long timestamp;
    public final Double value;

    public Event(JsonObject matomoEvent, Configuration config) {
        this.key = userId(matomoEvent, config).orElseThrow(() -> new IllegalStateException("User ID is required."));
        this.timestamp = timestamp(matomoEvent).orElseThrow(() -> new IllegalStateException("Event time is required."));
        this.value = value(matomoEvent, config);
        this.eventTypeId = eventTypeId(matomoEvent, config);
        this.properties = properties(matomoEvent, config);
        this.trafficTypeName = config.splitTrafficType;
        this.environmentName = config.splitEnvironment;
    }

    public static Optional<Event> fromJson(String json, Configuration config) {
        try {
            JsonObject matomoEvent = new Gson().fromJson(json, JsonObject.class);
            return Optional.of(new Event(matomoEvent, config));
        } catch (IllegalStateException exception) {
            System.err.printf("WARN - Error parsing event: error=%s %n", exception.getMessage());
            return Optional.empty();
        }
    }

    private static Optional<String> userId(JsonObject matomoEvent, Configuration config) {
        String userIdField = config.userIdField;
        if(!matomoEvent.has(userIdField)) {
            System.err.printf("WARN - User ID field not found for event: field=%s event=%s %n", userIdField, matomoEvent.toString());
            return Optional.empty();
        }
        return Optional.of(matomoEvent.get(userIdField).toString().replace("\"", ""));
    }

    public static Double value(JsonObject matomoEvent, Configuration config) {
        // Only get value if field is set
        String valueField = config.valueField;
        if(valueField != null && !valueField.isEmpty()) {
            try {
                return matomoEvent.get(valueField).getAsDouble();
            } catch (Exception exception) {
                System.err.printf("WARN - Event did not have a valid Value: key=%s %n", valueField);
            }
        }
        return null;
    }

    public static String eventTypeId(JsonObject matomoEvent, Configuration config) {
        if(matomoEvent.has("event_type")) {
            return config.eventTypePrefix + matomoEvent.get("type").getAsString();
        }
        return config.eventTypePrefix + "null";
    }

    public static Optional<Long> timestamp(JsonObject matomoEvent) {
//        String serverUploadTime = matomoEvent.get("timestamp").getAsString();
//        Date parsedServerTime;
//        try {
//            parsedServerTime = SERVER_FORMAT.parse(serverUploadTime);
//        } catch (ParseException pe) {
//            try {
//                parsedServerTime = SERVER_FORMAT_2.parse(serverUploadTime);
//            } catch (ParseException e) {
//                System.err.println("ERROR - event_time could not be parsed");
//                return Optional.empty();
//            }
//        }
//
//        return Optional.of(parsedServerTime.getTime());
    	return Optional.of(matomoEvent.get("timestamp").getAsLong() * 1000);
    }

    public static Map<String, Object> properties(JsonObject matomoEvent, Configuration config) {
//        JsonObject userPropsObj = matomoEvent.getAsJsonObject("user_properties");

        HashMap<String, Object> properties = new HashMap<>();
//        for(String propertyKey : config.propertyFields) {
//            // Check Base Event
//            if(matomoEvent.has(propertyKey) && !matomoEvent.get(propertyKey).isJsonNull()) {
//                properties.put(propertyKey, matomoEvent.get(propertyKey).getAsString());
//            }
//            // Check User Properties
//            if(userPropsObj != null && userPropsObj.has(propertyKey) && !userPropsObj.get(propertyKey).isJsonNull()) {
//                properties.put(propertyKey, userPropsObj.get(propertyKey).getAsString());
//            }
//        }
//
        return properties;
    }
}
