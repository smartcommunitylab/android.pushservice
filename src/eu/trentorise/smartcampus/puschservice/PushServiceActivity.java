package eu.trentorise.smartcampus.puschservice;

import java.io.IOException;
import java.util.Map;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import android.app.Activity;
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

import eu.trentorise.smartcampus.communicator.CommunicatorConnector;
import eu.trentorise.smartcampus.communicator.CommunicatorConnectorException;
import eu.trentorise.smartcampus.puschservice.util.PushServiceCostant;
import eu.trentorise.smartcampus.pushservice.R;

/**
 * Main UI for the demo app.
 */
public class PushServiceActivity extends Activity {

	private static final String TAG = "PushServiceActivity";

	AsyncTask<Void, Void, Void> mRegisterTask;

	private String regId;

	private GoogleCloudMessaging cloudMessaging;
	private CommunicatorConnector mConnector;

	private static String APP_ID;
	private static String SERVER_URL;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		ApplicationInfo ai;
		try {
			ai = getPackageManager().getApplicationInfo(
					getPackageName(), PackageManager.GET_META_DATA);
			Bundle bundle = ai.metaData;
			APP_ID = bundle.getString("APP_ID");
			SERVER_URL = bundle.getString("SERVER_URL");
			mConnector = new CommunicatorConnector(SERVER_URL, APP_ID);
			init();
		} catch (NameNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (CommunicatorConnectorException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void init() throws CommunicatorConnectorException, IOException {
		cloudMessaging = GoogleCloudMessaging
				.getInstance(getApplicationContext());
		Map<String, Object> a = mConnector.requestAppConfigurationToPush(
				PushServiceCostant.CLIENT_AUTH_TOKEN, APP_ID);

		// new GCMServerUtilities();

		checkNotNull(PushServiceCostant.SERVER_URL, "SERVER_URL");
		checkNotNull(PushServiceCostant.SENDER_ID, "SENDER_ID");
		// Make sure the device has the proper dependencies.
		// GCMRegistrar.checkDevice(context);
		// // Make sure the manifest was properly set - comment out this line
		// // while developing the app, then uncomment it when it's ready.
		// GCMRegistrar.checkManifest(context);

		registerReceiver(mHandleMessageReceiver, new IntentFilter(
				DISPLAY_MESSAGE_ACTION));

		new AsyncTask<Void, Void, Void>() {

			@Override
			protected Void doInBackground(Void... params) {
				// TODO Auto-generated method stub
				try {
					regId = cloudMessaging
							.register(PushServiceCostant.SENDER_ID);
				} catch (IOException e) {
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
		// if (regId.equals("")) {
		// // Automatically registers application on startup.
		// GCMRegistrar.register(context, PushServiceCostant.SENDER_ID);
		// } else {
		// // Device is already registered on GCM, check server.
		// if (GCMRegistrar.isRegisteredOnServer(context)) {
		// // Skips registration.
		//
		// Log.i(TAG, context.getString(R.string.already_registered)
		// + "\n");
		//
		// } else {
		// // Try to register again, but not in the UI thread.
		// // It's also necessary to cancel the thread onDestroy(),
		// // hence the use of AsyncTask instead of a raw thread.
		//
		// mRegisterTask = new AsyncTask<Void, Void, Void>() {
		//
		// @Override
		// protected Void doInBackground(Void... params) {
		//
		// boolean registered = GCMServerUtilities.register(
		// context, regId);
		// // At this point all attempts to register with the app
		// // server failed, so we need to unregister the device
		// // from GCM - the app will try to register again when
		// // it is restarted. Note that GCM will send an
		// // unregistered callback upon completion, but
		// // GCMIntentService.onUnregistered() will ignore it.
		// if (!registered) {
		// GCMServerUtilities.unregister(context, regId);
		// GCMRegistrar.unregister(context);
		// }
		// return null;
		// }
		//
		// @Override
		// protected void onPostExecute(Void result) {
		// mRegisterTask = null;
		// }
		//
		// };
		// mRegisterTask.execute(null, null, null);
		// }
		//
		// }
	}

	// @Deprecated
	// protected void unRegisterPush() {
	// // only for test
	// GCMServerUtilities.unregister(context, regId);
	// }

	public void destroy() {
		if (mRegisterTask != null) {
			mRegisterTask.cancel(true);
		}
		unregisterReceiver(mHandleMessageReceiver);
	}

	private void checkNotNull(Object reference, String name) {
		if (reference == null) {
			throw new NullPointerException(getApplicationContext().getString(
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