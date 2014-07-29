package eu.trentorise.smartcampus.pushservice;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

public class NotificationCenter {

	private NotificationDBHelper mDB;
	private Context mContext;

	public NotificationCenter(Context ctx) {
		mContext = ctx.getApplicationContext();
		mDB = new NotificationDBHelper(mContext);
	}

	public List<PushNotification> getNotifications() {
		SQLiteDatabase db = mDB.getReadableDatabase();
		String sql = " select * from "
				+ NotificationDBHelper.DB_TABLE_NOTIFICATION
				+ " order by date DESC";
		ArrayList<PushNotification> notifs = new ArrayList<PushNotification>();
		try {
			db.beginTransaction();
			Cursor cursor = db.rawQuery(sql, null);
			db.setTransactionSuccessful();

			while (cursor.moveToNext()) {
				PushNotification notif = new PushNotification(
						cursor.getInt(cursor
								.getColumnIndex(NotificationDBHelper.ID_KEY)),
						cursor.getString(cursor
								.getColumnIndex(NotificationDBHelper.TEXT_KEY)),
						new Date(cursor.getLong(cursor
								.getColumnIndex(NotificationDBHelper.DATE_KEY))),
						cursor.getInt(cursor
								.getColumnIndex(NotificationDBHelper.READ_KEY)) > 0 ? true
								: false);
				notifs.add(notif);
			}
		} catch (Exception ex) {
			Log.e(this.getClass().getName(), ex.toString());
		} finally {
			db.endTransaction();
		}

		return notifs;
	}

	public void publishNotification(Intent i,int pushId) {
		String message = i.getStringExtra("message");
		insertNotification(message);
		showNotification(pushId, message);
	}

	public void insertNotification(PushNotification notif) {
		SQLiteDatabase db = mDB.getWritableDatabase();
		try {
			db.beginTransaction();
			db.insert(NotificationDBHelper.DB_TABLE_NOTIFICATION, null,
					notif.toContentValues());
			db.setTransactionSuccessful();
		} catch (Exception ex) {
			Log.e(this.getClass().getName(), ex.toString());
		} finally {
			db.endTransaction();
		}
		db.close();
	}

	private void insertNotification(String msg) {
		SQLiteDatabase db = mDB.getWritableDatabase();
		PushNotification out = new PushNotification(msg);
		try {
			db.beginTransaction();
			db.insert(NotificationDBHelper.DB_TABLE_NOTIFICATION, null,
					out.toContentValues());
			db.setTransactionSuccessful();
		} catch (Exception ex) {
			Log.e(this.getClass().getName(), ex.toString());
		} finally {
			db.endTransaction();
		}
	}

	public void deleteNotification(int id) {
		SQLiteDatabase db = mDB.getWritableDatabase();
		try {
			db.beginTransaction();
			db.delete(NotificationDBHelper.DB_TABLE_NOTIFICATION,
					NotificationDBHelper.ID_KEY + "=?",
					new String[] { id + "" });
			db.setTransactionSuccessful();
		} catch (Exception ex) {
			Log.e(this.getClass().getName(), ex.toString());
		} finally {
			db.endTransaction();
		}
		db.close();
	}
	public void deleteNotification(PushNotification notif) {
		SQLiteDatabase db = mDB.getWritableDatabase();
		try {
			db.beginTransaction();
			db.delete(NotificationDBHelper.DB_TABLE_NOTIFICATION,
					NotificationDBHelper.ID_KEY + "=?",
					new String[] { notif.getId() + "" });
			db.setTransactionSuccessful();
		} catch (Exception ex) {
			Log.e(this.getClass().getName(), ex.toString());
		} finally {
			db.endTransaction();
		}
		db.close();
	}

	public void setNotificationRead(int id) {
		SQLiteDatabase db = mDB.getWritableDatabase();
		try {
			db.beginTransaction();
			db.update(NotificationDBHelper.DB_TABLE_NOTIFICATION,
					new PushNotification(id).toContentValues(),
					NotificationDBHelper.ID_KEY + "=?",
					new String[] { id + "" });
			db.setTransactionSuccessful();
		} catch (Exception ex) {
			Log.e(this.getClass().getName(), ex.toString());
		} finally {
			db.endTransaction();
		}
		db.close();
	}
	
	public void setNotificationRead(PushNotification notif) {
		SQLiteDatabase db = mDB.getWritableDatabase();
		try {
			db.beginTransaction();
			db.update(NotificationDBHelper.DB_TABLE_NOTIFICATION,
					notif.toContentValues(),
					NotificationDBHelper.ID_KEY + "=?",
					new String[] { notif.getId() + "" });
			db.setTransactionSuccessful();
		} catch (Exception ex) {
			Log.e(this.getClass().getName(), ex.toString());
		} finally {
			db.endTransaction();
		}
		db.close();
	}
	
	public void setAllNotificationsRead() {
		SQLiteDatabase db = mDB.getWritableDatabase();
		db.execSQL("update from "+NotificationDBHelper.DB_TABLE_NOTIFICATION+" set "+NotificationDBHelper.READ_KEY+"=0 where true");
		db.close();
	}

	public void showNotification(int pushId, String msg) {
		NotificationCompat.Builder ncb = new NotificationCompat.Builder(
				mContext);
		ncb.setContentText(msg);
		NotificationManager nm = (NotificationManager) mContext
				.getSystemService(Context.NOTIFICATION_SERVICE);
		nm.notify(pushId, ncb.build());

	}
}
