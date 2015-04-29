In order to use this library:

Retrieve di User Auth token and then:

	new AsyncTask<Void, Void, Void>() {

			@Override
			protected Void doInBackground(Void... params) {
				PushServiceConnector connector = new PushServiceConnector();
				try {
					connector.init(mContext, userAuthToken,
							GCM_APP_ID, GCM_SERVER_URL);

				} catch (CommunicatorConnectorException e) {
					Log.e(TAG, e.toString());
				} catch (AACException e) {
					Log.e(TAG, e.toString());
				}
				return null;
			}

	}.execute();

where the APP_ID is the one registered on the smartcampus console.

Add the permissions to the manifest:

	<permission
        android:name="eu.trentorise.smartcampus.viaggiatrento.permission.C2D_MESSAGE"
        android:protectionLevel="signature" />

    <uses-permission android:name="YOURPACKAGE.C2D_MESSAGE" />
    
    <!-- receives GCM messages -->
    <uses-permission android:name="com.google.android.c2dm.permission.RECEIVE" />
    <!-- GCM connects to Google services -->
    <uses-permission android:name="android.permission.INTERNET" />
    
    <!-- wake the processor if a GCM message is received -->
	<uses-permission android:name="android.permission.WAKE_LOCK" />

Declare the service:

	<receiver android:name="com.google.android.gcm.GCMBroadcastReceiver" android:permission="com.google.android.c2dm.permission.SEND" >\

            <intent-filter>
                <action android:name="com.google.android.c2dm.intent.RECEIVE" />
                <action android:name="com.google.android.c2dm.intent.REGISTRATION" />
                <category android:name="YOURPACKAGE.gcmclient" />  
            </intent-filter>   
        </receiver>
        <service
            android:name="eu.trentorise.smartcampus.pushservice.GCMIntentService">
        </service>

sobstituting YOURPACKAGE with the package of your app.
