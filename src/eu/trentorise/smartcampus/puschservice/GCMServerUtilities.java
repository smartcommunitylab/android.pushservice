package eu.trentorise.smartcampus.puschservice;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.content.Context;
import android.os.StrictMode;
import android.util.Log;

import com.google.android.gcm.GCMRegistrar;


import eu.trentorise.smartcampus.communicator.CommunicatorConnector;
import eu.trentorise.smartcampus.communicator.CommunicatorConnectorException;
import eu.trentorise.smartcampus.communicator.model.UserSignature;
import eu.trentorise.smartcampus.puschservice.util.PushServiceCostant;
import eu.trentorise.smartcampus.pushservice.R;

/**
 * Helper class used to communicate with the server.
 */
public final class GCMServerUtilities {

	private static final String TAG = "GCMServerUtilities";
	private static final int MAX_ATTEMPTS = 5;
	private static final int BACKOFF_MILLI_SECONDS = 2000;
	private static final Random random = new Random();

	
	private static String token;
	
	private static CommunicatorConnector communicatorConnector;



	public GCMServerUtilities() throws CommunicatorConnectorException {
		communicatorConnector = new CommunicatorConnector(
				PushServiceCostant.SERVER_URL, PushServiceCostant.APP_NAME);
		Map<String, Object> initMap=communicatorConnector.requestAppConfigurationToPush(PushServiceCostant.APP_NAME, PushServiceCostant.CLIENT_AUTH_TOKEN);
		
		PushServiceCostant.setConfigurationMap(initMap);
	}

	/**
	 * Register this account/device pair within the server.
	 * 
	 * @return whether the registration succeeded or not.
	 */
	static boolean register(final Context context, final String regId) {
		Log.i(TAG, "registering device (regId = " + regId + ")");
		// accessProvider = new AMSCAccessProvider();
		// setToken(accessProvider.readToken(context, null));
	
		
		Map<String, String> params = new HashMap<String, String>();
		params.put("regId", regId);
		long backoff = BACKOFF_MILLI_SECONDS + random.nextInt(1000);
		// Once GCM returns a registration id, we need to register it in the
		// demo server. As the server might be down, we will retry it a couple
		// times.
		for (int i = 1; i <= MAX_ATTEMPTS; i++) {
			Log.d(TAG, "Attempt #" + i + " to register");
			try {
				Log.i(TAG, context.getString(R.string.server_registering, i,
						MAX_ATTEMPTS));
				
				UserSignature userSignature=new UserSignature();
				userSignature.setAppName(PushServiceCostant.APP_NAME);
				userSignature.setRegistrationId(regId);
				
				communicatorConnector.registerUserToPush(PushServiceCostant.APP_NAME, userSignature,PushServiceCostant.USER_AUTH_TOKEN );
				//post(serverUrl, params, regId);
				GCMRegistrar.setRegisteredOnServer(context, true);
				String message = context.getString(R.string.server_registered);
				Log.i(TAG, message);
				return true;
			} catch (Exception e) {
				// Here we are simplifying and retrying on any error; in a real
				// application, it should retry only on unrecoverable errors
				// (like HTTP error code 503).
				Log.e(TAG, "Failed to register on attempt " + i, e);
				if (i == MAX_ATTEMPTS) {
					break;
				}
				try {
					Log.d(TAG, "Sleeping for " + backoff + " ms before retry");
					Thread.sleep(backoff);
				} catch (InterruptedException e1) {
					// Activity finished before we complete - exit.
					Log.d(TAG, "Thread interrupted: abort remaining retries!");
					Thread.currentThread().interrupt();
					return false;
				}
				// increase backoff exponentially
				backoff *= 2;
			}
		}
		String message = context.getString(R.string.server_register_error,
				MAX_ATTEMPTS);
		Log.i(TAG, message);
		return false;
	}

	/**
	 * Unregister this account/device pair within the server.
	 */
	static void unregister(final Context context, final String regId) {

		
		Map<String, String> params = new HashMap<String, String>();
		params.put("regId", regId);
		try {
			
			communicatorConnector.unregisterUserToPush(PushServiceCostant.APP_NAME, PushServiceCostant.USER_AUTH_TOKEN);
			//post(serverUrl, params, regId);
			GCMRegistrar.setRegisteredOnServer(context, false);
			String message = context.getString(R.string.server_unregistered);
			Log.i(TAG, message);
		} catch (Exception e) {
			// At this point the device is unregistered from GCM, but still
			// registered in the server.
			// We could try to unregister again, but it is not necessary:
			// if the server tries to send a message to the device, it will get
			// a "NotRegistered" error message and should unregister the device.
			String message = context.getString(
					R.string.server_unregister_error, e.getMessage());

			Log.i(TAG, message);
		}
	}

//	/**
//	 * Issue a POST request to the server.
//	 * 
//	 * @param endpoint
//	 *            POST address.
//	 * @param params
//	 *            request parameters.
//	 * 
//	 * @throws IOException
//	 *             propagated from POST.
//	 */
//	private static void get(String endpoint, Map<String, String> params,
//			String regId) throws IOException {
//
//		@SuppressWarnings("unused")
//		URL url;
//		try {
//			url = new URL(endpoint);
//		} catch (MalformedURLException e) {
//			throw new IllegalArgumentException("invalid url: " + endpoint);
//		}
//		HttpClient client = new DefaultHttpClient();
//		HttpGet request = new HttpGet();
//		try {
//			request.setURI(new URI(endpoint));
//			request.setHeader("AUTH_TOKEN".toString(),
//					token);
//			request.setHeader("REGISTRATIONID", regId);
//		} catch (URISyntaxException e) {
//			Log.e(TAG, e.getMessage());
//		}
//		HttpResponse response = client.execute(request);
//
//	}

//	public static void posts(String endpoint, Map<String, String> params,
//			String regId) throws IOException {
//		URL url;
//		try {
//			url = new URL(endpoint);
//		} catch (MalformedURLException e) {
//			throw new IllegalArgumentException("invalid url: " + endpoint);
//		}
//		StringBuilder bodyBuilder = new StringBuilder();
//		Iterator<Entry<String, String>> iterator = params.entrySet().iterator();
//		// constructs the POST body using the parameters
//		while (iterator.hasNext()) {
//			Entry<String, String> param = iterator.next();
//			bodyBuilder.append(param.getKey()).append('=')
//					.append(param.getValue());
//			if (iterator.hasNext()) {
//				bodyBuilder.append('&');
//			}
//		}
//		String body = bodyBuilder.toString();
//		Log.v(TAG, "Posting '" + body + "' to " + url);
//		byte[] bytes = body.getBytes();
//		HttpURLConnection conn = null;
//		try {
//			conn = (HttpURLConnection) url.openConnection();
//			conn.setDoOutput(true);
//			conn.setUseCaches(false);
//			conn.setFixedLengthStreamingMode(bytes.length);
//			conn.setRequestMethod("POST");
//			conn.setRequestProperty("AUTH_TOKEN", PushServiceCostant.mToken);
//			conn.setRequestProperty("REGISTRATIONID", regId);
//
//			StrictMode.ThreadPolicy policy = new
//					StrictMode.ThreadPolicy.Builder()
//					.permitAll().build();
//					StrictMode.setThreadPolicy(policy);
//
//			conn.connect();
//			// post the request
//			OutputStream out = conn.getOutputStream();
//			out.write(bytes);
//			out.close();
//			// handle the response
//			int status = conn.getResponseCode();
//			if (status != 200) {
//				throw new IOException("Post failed with error code " + status);
//			}
//		} catch (Exception e) {
//			Log.v(TAG,e.getMessage());
//		} finally {
//			if (conn != null) {
//				conn.disconnect();
//			}
//		}
//	}

	public String getToken() {
		return token;
	}

	public static void setToken(String token) {
		GCMServerUtilities.token = token;
	}

}
