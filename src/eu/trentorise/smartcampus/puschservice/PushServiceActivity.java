package eu.trentorise.smartcampus.puschservice;

import java.util.Map;

import junit.framework.Assert;
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
import eu.trentorise.smartcampus.communicator.model.AppSignature;
import eu.trentorise.smartcampus.communicator.model.UserSignature;
import eu.trentorise.smartcampus.puschservice.util.PushServiceCostant;
import eu.trentorise.smartcampus.pushservice.R;

/**
 * Main UI for the demo app.
 */
public class PushServiceActivity {

	private static final String TAG = "PushServiceActivity";

	AsyncTask<Void, Void, Void> mRegisterTask;

	private String regId;

	private GoogleCloudMessaging cloudMessaging;
	private CommunicatorConnector mConnector;
	private Context context;

	private static String APP_ID;
	private static String SERVER_URL;

	public void init(Context cnt) throws CommunicatorConnectorException {
		context = cnt;
		ApplicationInfo ai;
		try {
			ai = context.getPackageManager().getApplicationInfo(
					context.getPackageName(), PackageManager.GET_META_DATA);
			Bundle bundle = ai.metaData;
			APP_ID = "clientname";//bundle.getString("APP_ID");
			SERVER_URL = "https://vas-dev.smartcampuslab.it/core.communicator";//bundle.getString("SERVER_URL");
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

		checkNotNull(PushServiceCostant.SERVER_URL, "SERVER_URL");
		
		
		
	
		

		context.registerReceiver(mHandleMessageReceiver, new IntentFilter(
				DISPLAY_MESSAGE_ACTION));

		new AsyncTask<Void, Void, Void>() {

			@Override
			protected Void doInBackground(Void... params) {
				// TODO Auto-generated method stub
				try {
					
					Map<String, Object> mapKey = null;
					//solo per test
					AppSignature signature = new AppSignature();
					signature.setApiKey("AIzaSyCW1Vr4LKs22qrFWBwXX0DC_ckEB20YgEY");
					signature.setAppId(APP_ID);
					signature.setSenderId("499940284623");
					mConnector.registerApp(signature,
							APP_ID, PushServiceCostant.CLIENT_AUTH_TOKEN);
					
					//solo per test
					
					try {
						mapKey = mConnector.requestAppConfigurationToPush(
								PushServiceCostant.CLIENT_AUTH_TOKEN, APP_ID);
					} catch (Exception e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					
					// set senderid
					//PushServiceCostant.setConfigurationMap(mapKey);
					
					regId = cloudMessaging
							.register(PushServiceCostant.SENDER_ID);
					if (regId != null) {
						UserSignature signUserSignature = new UserSignature();
						signUserSignature.setAppName(APP_ID);
						signUserSignature.setRegistrationId(regId);
						mConnector.registerUserToPush(APP_ID,
								signUserSignature,
								PushServiceCostant.USER_AUTH_TOKEN);
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