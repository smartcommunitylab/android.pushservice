package eu.trentorise.smartcampus.pushservice;

import java.util.Map;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import eu.trentorise.smartcampus.communicator.CommunicatorConnector;
import eu.trentorise.smartcampus.communicator.CommunicatorConnectorException;
import eu.trentorise.smartcampus.communicator.model.UserSignature;



public class PushServiceConnector {
	public static final Object SENDER_ID_KEY = "GCM_SENDER_ID";
	public static final Object APP_ID_KEY = "APP_ID";
	private static final String TAG = "PushServiceConnector";

	AsyncTask<Void, Void, Void> mRegisterTask;

	private String regId;

	private GoogleCloudMessaging cloudMessaging;
	private CommunicatorConnector mConnector;
	private Context context;
	private String userAuthToken;;
	private static String APP_ID;
	private static String SERVER_URL;
	private static String senderid;

	public void init(Context cnt, String tkn,String appid,String ServerUrl)
			throws CommunicatorConnectorException {
		context = cnt;
		userAuthToken = tkn;
	
				
			APP_ID = appid;
			SERVER_URL = ServerUrl;

			try {
				mConnector = new CommunicatorConnector(SERVER_URL, APP_ID);
			} catch (Exception e) {
				e.printStackTrace();
			}

		
		cloudMessaging = GoogleCloudMessaging.getInstance(context);

		context.registerReceiver(mHandleMessageReceiver, new IntentFilter(
				DISPLAY_MESSAGE_ACTION));

		new AsyncTask<Void, Void, Void>() {

			@Override
			protected Void doInBackground(Void... params) {
				// TODO Auto-generated method stub
				try {

					System.out
							.println("Token in pushservice: " + userAuthToken);

					Map<String, Object> mapKey = null;

					// get parameters from communicator service
					mapKey = mConnector.requestPublicConfigurationToPush(
							APP_ID, userAuthToken);

					// find senderid
					senderid = String.valueOf(mapKey
							.get(SENDER_ID_KEY));

					if (senderid == null)
						throw new CommunicatorConnectorException(
								"No app registered with this app id");

					// register app to gcm
					regId = cloudMessaging.register(senderid);

					// check regid not null
					if (regId != null) {
						UserSignature signUserSignature = new UserSignature();
						signUserSignature.setAppName(APP_ID);
						signUserSignature.setRegistrationId(regId);
						mConnector.registerUserToPush(
								signUserSignature,APP_ID, userAuthToken);

					}

				} catch (Exception e) {
					e.printStackTrace();
				}
				return null;
			}

			@Override
			protected void onPostExecute(Void result) {
				super.onPostExecute(result);
				Log.v(TAG, "\n RegId:" + regId + "\n\n ProjectId:" + senderid
						+ "\n\n");
			}

		}.execute();

	}

	public void destroy() {
		if (mRegisterTask != null) {
			mRegisterTask.cancel(true);
		}
		context.unregisterReceiver(mHandleMessageReceiver);
	}

	private final BroadcastReceiver mHandleMessageReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			Log.i("Notification", "Arrived");
		}
	};

	static final String DISPLAY_MESSAGE_ACTION = "com.google.android.gcm.demo.app.DISPLAY_MESSAGE";

}