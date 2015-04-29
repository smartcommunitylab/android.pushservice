package eu.trentorise.smartcampus.pushservice;

import java.io.IOException;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import eu.trentorise.smartcampus.communicator.CommunicatorConnector;
import eu.trentorise.smartcampus.communicator.CommunicatorConnectorException;
import eu.trentorise.smartcampus.communicator.model.UserSignature;

public class PushServiceConnector {
	private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

	public static final Object SENDER_ID_KEY = "GCM_SENDER_ID";
	public static final Object APP_ID_KEY = "APP_ID";
	private static final String TAG = "PushServiceConnector";
	private static final String PREFS = "pushServicePrefs";
	private static final int ACCESS = Context.MODE_PRIVATE | Context.CONTEXT_RESTRICTED;
	private static final String REG_ID = "regId";

	private Context mContext;
	private String mUserAuthToken;;
	private static String mAppId;
	private static String mServerUrl;

	private static GoogleCloudMessaging gcm;

	public void init(Context cnt, String tkn, String appid, String ServerUrl) throws CommunicatorConnectorException,
			IOException {
		mContext = cnt.getApplicationContext();
		mUserAuthToken = tkn;

		mAppId = appid;
		mServerUrl = ServerUrl;

		CommunicatorConnector mConnector = null;
		try {
			mConnector = new CommunicatorConnector(mServerUrl, mAppId);

			String regId = "";

			if (!checkPlayServices()) {
				return;
			}

			gcm = GoogleCloudMessaging.getInstance(mContext);

			// Get the existing registration id, if it exists.
			regId = readRegId(mContext);// GCMRegistrar.getRegistrationId(mContext);

			if (regId == null || regId.equals("")) {
				if (mConnector != null) {
					Map<String, Object> mapKey = mConnector.requestPublicConfigurationToPush(mAppId, mUserAuthToken);

					// find senderid
					String senderid = String.valueOf(mapKey.get("GCM_SENDER_ID"));
					// register this device for this project
					// senderid= "220741898329";
					regId = gcm.register(senderid);
					if (regId != null && regId.length() > 0) {
						UserSignature signUserSignature = new UserSignature();
						signUserSignature.setAppName(mAppId);
						signUserSignature.setRegistrationId(regId);
						mConnector.registerUserToPush(signUserSignature, mAppId, mUserAuthToken);
						writeRegId(mContext, regId);
					}
				}
			} else {
				// Already registered;
			}

			Log.i("GCM_REGID:", regId);
		} catch (Exception e) {
			gcm.unregister();
			Log.e(TAG, e.toString());
		}
	}

	public static void reset(Context mContext) throws IOException {
		writeRegId(mContext, "");
		gcm.unregister();
	}

	private static String readRegId(Context ctx) {
		return ctx.getSharedPreferences(PREFS, ACCESS).getString(REG_ID, "");
	}

	private static void writeRegId(Context ctx, String regId) {
		ctx.getSharedPreferences(PREFS, ACCESS).edit().putString(REG_ID, regId).commit();
	}

	/**
	 * Check the device to make sure it has the Google Play Services APK. If it
	 * doesn't, display a dialog that allows users to download the APK from the
	 * Google Play Store or enable it in the device's system settings.
	 */
	private boolean checkPlayServices() {
		int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(mContext);
		if (resultCode != ConnectionResult.SUCCESS) {
			if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
				GooglePlayServicesUtil.getErrorDialog(resultCode, (Activity) mContext, PLAY_SERVICES_RESOLUTION_REQUEST).show();
			} else {
				Log.i(TAG, "This device is not supported.");
			}
			return false;
		}
		return true;
	}
}