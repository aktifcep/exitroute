package com.agsa.client;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.agsa.client.error.ApplicationNotFoundException;
import com.agsa.client.error.InvalidAPIKeyException;

public class AGSA {

	private static Map<String, AGSAClient> instances = new HashMap<String, AGSAClient>();

	public static AGSAClient getClient(String apiKey) {
		if (!instances.containsKey(apiKey)) {
			instances.put(apiKey, new AGSAClientImpl(apiKey));
		}

		return instances.get(apiKey);
	}

	public static AGSAClient getClient(String apiKey, String appName) throws InvalidAPIKeyException, ApplicationNotFoundException, IOException {
		AGSAClient client = getClient(apiKey);
		
		client.useApplication(appName);

		return client;
	}

	private static boolean secured = false;

	public static void setSecured(boolean secured) {
		AGSA.secured = secured;
	}

	public static boolean isSecured() {
		return secured;
	}
	
}
