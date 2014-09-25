package eu.trentorise.smartcampus.pushservice;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

public class NotificationCenter {

	private Context mContext;

	public NotificationCenter(Context ctx) {
		mContext = ctx.getApplicationContext();
	}

	public void publishNotification(Context ctx, Intent i, PushNotificationBuilder builder, int pushId, Class<? extends Activity> resultActivity) {
		PushNotification notif = builder.buildNotification(ctx, i);
		showSystemNotification(pushId, notif, resultActivity);
	}


	public void showSystemNotification(int pushId, PushNotification notif,
			Class<? extends Activity> resultActivity) {
		NotificationCompat.Builder ncb = new NotificationCompat.Builder(
				mContext);
		ncb.setSmallIcon(R.drawable.ic_launcher);
		ncb.setContentText(notif.getDescription());
		ncb.setContentTitle(notif.getTitle());

		Intent resultIntent = new Intent(mContext, resultActivity);

		TaskStackBuilder stackBuilder = TaskStackBuilder.create(mContext);
		stackBuilder.addParentStack(resultActivity);
		stackBuilder.addNextIntent(resultIntent);
		PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0,
				PendingIntent.FLAG_UPDATE_CURRENT);
		ncb.setContentIntent(resultPendingIntent);
		ncb.setAutoCancel(true);
		NotificationManager nm = (NotificationManager) mContext
				.getSystemService(Context.NOTIFICATION_SERVICE);
		Notification n = ncb.build();
		n.defaults = Notification.DEFAULT_ALL;
		nm.notify(pushId, n);
	}
}
