package io.split.dbm.integrations.matomo2split;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import com.google.gson.Gson;

public class Configuration {

	public String splitServerSideApiKey;
	public String matomoAuthKey;
	public String siteId;
	public int batchSize;   
	public int retries;
	public String splitTrafficType;
	public String splitEnvironment;
	public String userIdField;
	public String valueField;
	public String eventTypePrefix;

	public static Configuration fromFile(String configFilePath) throws IOException {
		String configContents = Files.readString(Paths.get(configFilePath));
		return new Gson().fromJson(configContents, Configuration.class);
	}
}