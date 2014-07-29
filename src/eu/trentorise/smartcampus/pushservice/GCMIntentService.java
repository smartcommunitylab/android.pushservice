package eu.trentorise.smartcampus.pushservice;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.android.gcm.GCMBaseIntentService;

import eu.trentorise.smartcampus.communicator.model.UserSignature;

public class GCMIntentService extends GCMBaseIntentService {

    
	private static final String TAG = "GCMIntentService";
	
	public GCMIntentService()
	{
		super("220741898329");
		Log.d(TAG, "GCMIntentService init");
	}
	

	@Override
	protected void onError(Context ctx, String sError) {
		Log.d(TAG, "Error: " + sError);
		
	}

	@Override
	protected void onMessage(Context ctx, Intent intent) {
		
		Log.d(TAG, "Message Received");
		
		String message = intent.getStringExtra("message");
		
		sendGCMIntent(ctx, message);
		
	}

	
	private void sendGCMIntent(Context ctx, String message) {
		
		Intent broadcastIntent = new Intent();
		broadcastIntent.setAction("GCM_RECEIVED_ACTION");
		broadcastIntent.putExtra("gcm", message);
		
		NotificationDBHelper db = new NotificationDBHelper(ctx);
		db.insertNotification(message);
		
		ctx.sendBroadcast(broadcastIntent);
	}
	
	
	@Override
	protected void onRegistered(Context ctx, String regId) {
		// send regId to your server
		
	}

	@Override
	protected void onUnregistered(Context ctx, String regId) {
		// send notification to your server to remove that regId
	}

}
