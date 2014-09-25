package eu.trentorise.smartcampus.pushservice;

import android.content.Context;
import android.content.Intent;

public interface PushNotificationBuilder {

	public PushNotification buildNotification(Context ctx, Intent i);
}
