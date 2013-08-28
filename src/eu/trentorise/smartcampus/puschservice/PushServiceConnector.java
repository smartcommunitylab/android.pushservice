package eu.trentorise.smartcampus.puschservice;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import eu.trentorise.smartcampus.communicator.CommunicatorConnector;
import eu.trentorise.smartcampus.communicator.CommunicatorConnectorException;
import eu.trentorise.smartcampus.communicator.model.Notification;
import eu.trentorise.smartcampus.communicator.model.NotificationAuthor;
import eu.trentorise.smartcampus.communicator.model.UserSignature;
import eu.trentorise.smartcampus.profileservice.model.BasicProfile;
import eu.trentorise.smartcampus.puschservice.util.PushServiceCostant;
import eu.trentorise.smartcampus.pushservice.R;

/**
 * Main UI for the demo app.
 */
public class PushServiceConnector {

	private static final String TAG = "PushServiceConnector";

	AsyncTask<Void, Void, Void> mRegisterTask;

	private String regId;

	private GoogleCloudMessaging cloudMessaging;
	private CommunicatorConnector mConnector;
	private Context context;
	private String userAuthToken;
	private BasicProfile bp;
	private static String APP_ID;
	private static String SERVER_URL;

	public void init(Context cnt, String tkn, BasicProfile basicP)
			throws CommunicatorConnectorException {
		context = cnt;
		userAuthToken = tkn;
		bp = basicP;
		ApplicationInfo ai;
		try {
			ai = context.getPackageManager().getApplicationInfo(
					context.getPackageName(), PackageManager.GET_META_DATA);
			Bundle bundle = ai.metaData;
			APP_ID = bundle.getString("APP_ID");
			SERVER_URL = bundle.getString("SERVER_URL");
			PushServiceCostant.setSERVER_URL(SERVER_URL);

			try {
				mConnector = new CommunicatorConnector(SERVER_URL, APP_ID);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} catch (NameNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		cloudMessaging = GoogleCloudMessaging.getInstance(context);

		checkNotNull(PushServiceCostant.getSERVER_URL(), "SERVER_URL");

		context.registerReceiver(mHandleMessageReceiver, new IntentFilter(
				DISPLAY_MESSAGE_ACTION));

		new AsyncTask<Void, Void, Void>() {

			@Override
			protected Void doInBackground(Void... params) {
				// TODO Auto-generated method stub
				try {
					// // // solo per test
					// AppSignature signature = new AppSignature();
					// Map<String, Object> publiclist = new HashMap<String,
					// Object>();
					// publiclist.put(Constants.GCM_SENDER_ID, "557126495282");
					// Map<String, Object> privatelist = new HashMap<String,
					// Object>();
					// privatelist.put(Constants.GCM_SENDER_API_KEY,
					// "AIzaSyBA0dQYoF2YQKwm6h5dH4q7h5DTt7LmJrw");
					// signature.setPrivateKey(privatelist);
					// signature.setPublicKey(publiclist);
					//
					//
					// mConnector.registerApp(signature, APP_ID,
					// "043b44c9-7277-4888-8b81-890a9607c678");
					//
					// // // solo per test

					System.out
							.println("Token in pushservice: " + userAuthToken);
					System.out.println(APP_ID);

					Map<String, Object> mapKey = null;

					mapKey = mConnector.requestPublicConfigurationToPush(
							APP_ID, userAuthToken);
					System.out.println(mapKey);
					// set senderid
					PushServiceCostant.setConfigurationMap(mapKey);

					regId = cloudMessaging
							.register(PushServiceCostant.SENDER_ID);
					if (regId != null) {
						UserSignature signUserSignature = new UserSignature();
						signUserSignature.setAppName(APP_ID);
						signUserSignature.setRegistrationId(regId);
						mConnector.registerUserToPush(APP_ID,
								signUserSignature, userAuthToken);

						// send notification
						List<String> users = new ArrayList<String>();
						users.add(bp.getUserId());

						
						Notification n = new Notification();
						n.setId(null);
						n.setUser(bp.getName());
						n.setTitle("Titolo prima notifica");
						n.setDescription("Testo prima notifica");
						n.setType(APP_ID);
						
						NotificationAuthor author = new NotificationAuthor();
						author.setUserId(bp.getUserId());
						
						n.setAuthor(author);
						mConnector.sendUserNotification(users, n, userAuthToken);

						//mConnector.sendAppNotification(n, "55712645282", users,
//								userAuthToken);
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return null;
			}

			@Override
			protected void onPostExecute(Void result) {
				// TODO Auto-generated method stub
				super.onPostExecute(result);
				Log.v(TAG, "\n RegId:" + regId + "\n\n ProjectId:"
						+ PushServiceCostant.SENDER_ID + "\n\n");
			}

		}.execute();

	}

	public void destroy() {
		if (mRegisterTask != null) {
			mRegisterTask.cancel(true);
		}
		context.unregisterReceiver(mHandleMessageReceiver);
	}

	private void checkNotNull(Object reference, String name) {
		if (reference == null) {
			throw new NullPointerException(context.getString(
					R.string.error_config, name));
		}
	}

	private final BroadcastReceiver mHandleMessageReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			Log.i("Notification", "Arrived");
		}
	};

	static final String DISPLAY_MESSAGE_ACTION = "com.google.android.gcm.demo.app.DISPLAY_MESSAGE";

}