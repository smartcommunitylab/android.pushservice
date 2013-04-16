/**
 * 
 */

package eu.trentorise.smartcampus.puschservice;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.android.gcm.GCMBaseIntentService;
import com.google.android.gcm.GCMRegistrar;

import eu.trentorise.smartcampus.puschservice.util.PushServiceCostant;

/**
 * IntentService responsible for handling GCM messages.
 */
public abstract class GCMIntentServiceExtended extends GCMBaseIntentService {

	private static final String TAG = "GCMIntentService";
	
	public GCMIntentServiceExtended() {
		super(PushServiceCostant.SENDER_ID);
	}

	@Override
	protected void onRegistered(Context context, String registrationId) {
		Log.i(TAG, "Device registered: regId = " + registrationId);
		GCMServerUtilities.register(context, registrationId);
	}

	@Override
	protected void onUnregistered(Context context, String registrationId) {
		Log.i(TAG, "Device unregistered");
		if (GCMRegistrar.isRegisteredOnServer(context)) {
			GCMServerUtilities.unregister(context, registrationId);
		} else {
			// This callback results from the call to unregister made on
			// ServerUtilities when the registration to the server failed.
			Log.i(TAG, "Ignoring unregister callback");
		}
	}

	@Override
	protected boolean onRecoverableError(Context context, String errorId) {
		Log.i(TAG, "Received recoverable error: " + errorId);
		return super.onRecoverableError(context, errorId);
	}

	@Override
	protected abstract void onMessage(Context context, Intent intent);

	@Override
	protected abstract void onDeletedMessages(Context context, int total);

	@Override
	public abstract void onError(Context context, String errorId);

}
