package eu.trentorise.smartcampus.pushservice;

import java.util.List;

import android.content.Context;

public class NotificationCenter {
	
	private NotificationDBHelper mDB;
	private Context mContext;
	
	public NotificationCenter(Context ctx) {
		mContext = ctx.getApplicationContext();
		mDB = new NotificationDBHelper(mContext);
	}

	public List<PushNotification> getNotifications() {
		return mDB.getNotifications();
	}

	public void insertNotification(PushNotification notif) {
		mDB.insertNotification(notif);
	}

	public void insertNotification(String msg) {
		mDB.insertNotification(msg);
	}

	public void deleteNotification(int id) {
		mDB.deleteNotification(id);
	}

	public void setNotificationRead(int id){
		mDB.setNotificationRead(id);
	}
}
