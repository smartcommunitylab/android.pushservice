package eu.trentorise.smartcampus.pushservice;

import java.util.Map;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import com.google.android.gcm.GCMRegistrar;

import eu.trentorise.smartcampus.communicator.CommunicatorConnector;
import eu.trentorise.smartcampus.communicator.CommunicatorConnectorException;
import eu.trentorise.smartcampus.communicator.model.UserSignature;

public class PushServiceConnector {
	public static final Object SENDER_ID_KEY = "GCM_SENDER_ID";
	public static final Object APP_ID_KEY = "APP_ID";
	private static final String TAG = "PushServiceConnector";

	private Context mContext;
	private String mUserAuthToken;;
	private static String mAppId;
	private static String mServerUrl;

	public void init(Context cnt, String tkn, String appid, String ServerUrl)
			throws CommunicatorConnectorException {
		mContext = cnt;
		mUserAuthToken = tkn;

		mAppId = appid;
		mServerUrl = ServerUrl;

		CommunicatorConnector mConnector = null;
		try {
			mConnector = new CommunicatorConnector(mServerUrl, mAppId);
			IntentFilter gcmFilter = new IntentFilter();
			gcmFilter.addAction("GCM_RECEIVED_ACTION");

			String regId = "";
			// This registerClient() method checks the current device,
			// checks the
			// manifest for the appropriate rights, and then retrieves a
			// registration id
			// from the GCM cloud. If there is no registration id,
			// GCMRegistrar will
			// register this device for the specified project, which will
			// return a
			// registration id.
			// Check that the device supports GCM (should be in a try /
			// catch)
			GCMRegistrar.checkDevice(mContext);

			// Check the manifest to be sure this app has all the
			// required
			// permissions.
			GCMRegistrar.checkManifest(mContext);

			// Get the existing registration id, if it exists.
			regId = GCMRegistrar.getRegistrationId(mContext);

			if (regId.equals("")) {
				if (mConnector != null) {
					Map<String, Object> mapKey = mConnector
							.requestPublicConfigurationToPush("core.mobility",
									mUserAuthToken);

					// find senderid
					String senderid = String.valueOf(mapKey
							.get("GCM_SENDER_ID"));
					// register this device for this project
					GCMRegistrar.register(mContext, senderid);
					regId = GCMRegistrar.getRegistrationId(mContext);
					if (regId != null && regId.length() > 0) {
						UserSignature signUserSignature = new UserSignature();
						signUserSignature.setAppName("core.mobility");
						signUserSignature.setRegistrationId(regId);
						mConnector.registerUserToPush(signUserSignature,
								"core.mobility", mUserAuthToken);
					}
				}
			} else {
				// Already registered;
			}
			
			Log.i("GCM_REGID:", regId);
		} catch (Exception e) {
			Log.e(TAG, e.toString());
		}

	}

}