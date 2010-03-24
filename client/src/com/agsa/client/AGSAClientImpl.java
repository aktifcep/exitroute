package com.agsa.client;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;

import com.agsa.client.error.ApplicationNotFoundException;
import com.agsa.client.error.InvalidAPIKeyException;
import com.agsa.client.error.NoAPIKeyException;
import com.agsa.client.model.AGSAMessage;

class AGSAClientImpl implements AGSAClient {

	private String apiKey;
	private String appName;

	public AGSAClientImpl(String apiKey) {
		this.apiKey = apiKey;
	}

	public String getApiKey() {
		return apiKey;
	}

	public String getAppName() {
		return appName;
	}

	private String get(String path, Map<String, String> params) throws URISyntaxException, ClientProtocolException, IOException, InvalidAPIKeyException, ApplicationNotFoundException,
			NoAPIKeyException {
		String query = Constants.KEY + "=" + URLEncoder.encode(apiKey, Constants.CHARSET);
		for (Map.Entry<String, String> entry : params.entrySet()) {
			query += "&" + URLEncoder.encode(entry.getKey(), Constants.CHARSET) + "=" + URLEncoder.encode(entry.getValue(), Constants.CHARSET);
		}

		HttpClient client = new DefaultHttpClient();
		URI uri = new URI(AGSA.isSecured() ? "https" : "http", Constants.HOST, path, query, (String) null);
		HttpUriRequest request = new HttpGet(uri);
		HttpResponse response = client.execute(request);

		switch (response.getStatusLine().getStatusCode()) {
			case 200:
				return response.getLastHeader(Constants.X_JSON_DATA).getValue();
			case 500: {
				Header errorHeader = response.getLastHeader(Constants.X_JSON_DATA);
				Header messageHeader = response.getLastHeader(Constants.X_JSON_ERROR);
				String error = errorHeader == null ? null : errorHeader.getValue();
				String message = messageHeader == null ? null : messageHeader.getValue();
				if (error != null && error.equals(Constants.ERR_INVALID_API_KEY)) {
					throw new InvalidAPIKeyException(message);
				} else if (error != null && error.equals(Constants.ERR_NO_API_KEY)) {
					throw new NoAPIKeyException(message);
				} else if (error != null && error.equals(Constants.ERR_APP_NOT_FOUND)) {
					throw new ApplicationNotFoundException(message);
				} else {
					throw new IOException(message);
				}
			}
			default:
				throw new IOException("Unexpected response code");
		}
	}

	private JSONArray getArray(String path, Map<String, String> params) throws ClientProtocolException, JSONException, URISyntaxException, IOException, InvalidAPIKeyException,
			ApplicationNotFoundException, NoAPIKeyException {
		return new JSONArray(get(path, params));
	}

	private JSONArray getArray(String path) throws ClientProtocolException, JSONException, URISyntaxException, IOException, InvalidAPIKeyException, ApplicationNotFoundException, NoAPIKeyException {
		return getArray(path, new HashMap<String, String>());
	}

	private String[] cachedApplications;

	@Override
	public String[] getApplications() throws IOException, InvalidAPIKeyException {
		if (cachedApplications == null) {
			JSONArray array;
			try {
				array = getArray(Constants.PATH_APPLICATIONS);
			} catch (ClientProtocolException e) {
				throw new IOException(e.getMessage());
			} catch (JSONException e) {
				throw new IOException(e.getMessage());
			} catch (URISyntaxException e) {
				throw new IOException(e.getMessage());
			} catch (ApplicationNotFoundException e) {
				throw new IOException(e.getMessage());
			} catch (NoAPIKeyException e) {
				throw new InvalidAPIKeyException(e.getMessage());
			}

			cachedApplications = new String[array.length()];
			for (int i = 0; i < array.length(); i++) {
				try {
					cachedApplications[i] = array.getString(i);
				} catch (JSONException e) {
					cachedApplications = null;
					throw new IOException(e.getMessage());
				}
			}
		}

		return cachedApplications;
	}

	@Override
	public void getApplications(final AGSAResponseHandler<String[]> responseHandler) {
		(new Thread() {
			@Override
			public void run() {
				try {
					String[] result = AGSAClientImpl.this.getApplications();
					responseHandler.onSuccess(result);
				} catch (Throwable e) {
					responseHandler.onFailure(e);
				}
			}
		}).start();
	}

	@Override
	public void useApplication(String appName) throws IOException, ApplicationNotFoundException, InvalidAPIKeyException {
		String[] applications = getApplications();

		for (String application : applications) {
			if (application.equals(appName)) {
				this.appName = appName;
				return;
			}
		}

		throw new ApplicationNotFoundException();
	}

	@Override
	public void useApplication(final String appName, final AGSAResponseHandler<Void> responseHandler) {
		(new Thread() {
			@Override
			public void run() {
				try {
					AGSAClientImpl.this.useApplication(appName);
					responseHandler.onSuccess((Void) null);
				} catch (Throwable e) {
					responseHandler.onFailure(e);
				}
			}
		}).start();
	}

	@Override
	public AGSAMessage createMessage(String type, String title, String content) throws IOException, InvalidAPIKeyException, ApplicationNotFoundException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void createMessage(String type, String title, String content, AGSAResponseHandler<AGSAMessage> responseHandler) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getCurrentApplication() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AGSAMessage[] getMessages(String type, int start, int count) throws IOException, InvalidAPIKeyException, ApplicationNotFoundException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void getMessages(String type, int start, int count, AGSAResponseHandler<AGSAMessage[]> responseHandler) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getNbMessages(String type) throws IOException, InvalidAPIKeyException, ApplicationNotFoundException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void getNbMessages(String type, AGSAResponseHandler<Integer> responseHandler) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public AGSAMessage[] getRecentMessages(String type, Date publishedAfter) throws IOException, InvalidAPIKeyException, ApplicationNotFoundException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void getRecentMessages(String type, Date publishedAfter, AGSAResponseHandler<AGSAMessage[]> responseHandler) {
		// TODO Auto-generated method stub
		
	}

}
