package eu.trentorise.smartcampus.puschservice;


import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gcm.GCMRegistrar;

import eu.trentorise.smartcampus.puschservice.util.PushServiceCostant;
import eu.trentorise.smartcampus.pushservice.R;

/**
 * Main UI for the demo app.
 */
public class PushServiceActivity extends Activity {

	private static final String TAG = "PushServiceActivity";
	
	AsyncTask<Void, Void, Void> mRegisterTask;
	
	Context context ;
	
	public void init(String serverurl,String sendeid,String appname){
		new PushServiceCostant(sendeid,serverurl,appname);
	}
	
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        checkNotNull(PushServiceCostant.SERVER_URL, "SERVER_URL");
        checkNotNull(PushServiceCostant.SENDER_ID, "SENDER_ID");
        // Make sure the device has the proper dependencies.
        GCMRegistrar.checkDevice(this);
        // Make sure the manifest was properly set - comment out this line
        // while developing the app, then uncomment it when it's ready.
        GCMRegistrar.checkManifest(this);
       context=getApplicationContext();
       
        registerReceiver(mHandleMessageReceiver, new IntentFilter(DISPLAY_MESSAGE_ACTION));
        final String regId = GCMRegistrar.getRegistrationId(this);
        if (regId.equals("")) {
            // Automatically registers application on startup.
            GCMRegistrar.register(this, PushServiceCostant.SENDER_ID);
        } else {
            // Device is already registered on GCM, check server.
            if (GCMRegistrar.isRegisteredOnServer(this)) {
                // Skips registration.
            	
            	Log.i(TAG,getString(R.string.already_registered) + "\n");
                
            } else {
                // Try to register again, but not in the UI thread.
                // It's also necessary to cancel the thread onDestroy(),
                // hence the use of AsyncTask instead of a raw thread.
            	
                mRegisterTask = new AsyncTask<Void, Void, Void>() {

                    @Override
                    protected Void doInBackground(Void... params) {
                   
                        boolean registered =
                                GCMServerUtilities.register(context, regId);
                        // At this point all attempts to register with the app
                        // server failed, so we need to unregister the device
                        // from GCM - the app will try to register again when
                        // it is restarted. Note that GCM will send an
                        // unregistered callback upon completion, but
                        // GCMIntentService.onUnregistered() will ignore it.
                        if (!registered) {
                        	GCMServerUtilities.unregister(context, regId);
                            GCMRegistrar.unregister(context);
                        }
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void result) {
                        mRegisterTask = null;
                    }

                };
                mRegisterTask.execute(null, null, null);
            }
        
        }
        Log.v(TAG,"\n RegId:"+regId+ "\n\n ProjectId:"+PushServiceCostant.SENDER_ID+"\n\n" );
       

    }
	
	

	@Override
	protected void onDestroy() {
		if (mRegisterTask != null) {
			mRegisterTask.cancel(true);
		}
		unregisterReceiver(mHandleMessageReceiver);
		GCMRegistrar.onDestroy(context);
		super.onDestroy();
	}

	private void checkNotNull(Object reference, String name) {
		if (reference == null) {
			throw new NullPointerException(getString(R.string.error_config,
					name));
		}
	}

	private final BroadcastReceiver mHandleMessageReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			
			
		}
	};
	
	 static final String DISPLAY_MESSAGE_ACTION =
	            "com.google.android.gcm.demo.app.DISPLAY_MESSAGE";

	    /**
	     * Intent's extra that contains the message to be displayed.
	     */
	    static final String EXTRA_MESSAGE = "message";

	    /**
	     * Notifies UI to display a message.
	     * <p>
	     * This method is defined in the common helper because it's used both by
	     * the UI and the background service.
	     *
	     * @param context application's context.
	     * @param message message to be displayed.
	     */
	    static void displayMessage(Context context, String message) {
	        Intent intent = new Intent(DISPLAY_MESSAGE_ACTION);
	        intent.putExtra(EXTRA_MESSAGE, message);
	        context.sendBroadcast(intent);
	    }

}