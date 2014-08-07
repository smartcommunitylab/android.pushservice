package eu.trentorise.smartcampus.pushservice;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

public class NotificationCenter {

	private final static String FIELD_TITLE = "title";
	private final static String FIELD_DESCRIPTION = "description";
	private final static String FIELD_AGENCYID = "content.agencyId";
	private final static String FIELD_ROUTEID = "content.routeId";
	private final static String FIELD_ROUTESHORTNAME = "content.routeShortName";
	private final static String FIELD_TRIPID = "content.tripId";
	private final static String FIELD_DELAY = "content.delay";
	private final static String FIELD_ENTITY = "entities";
	private final static String OPTIONAL_FIELD_FROMTIME = "content.from";
	private final static String OPTIONAL_FIELD_STATION = "content.station";

	private NotificationDBHelper mDB;
	private Context mContext;

	public NotificationCenter(Context ctx) {
		mContext = ctx.getApplicationContext();
		mDB = new NotificationDBHelper(mContext);
	}

	public void publishNotification(Intent i, int pushId,
			Class<? extends Activity> resultActivity) {
		PushNotification notif = buildPushNotification(i);
		insertNotification(notif);
		showSystemNotification(pushId, notif, resultActivity);
	}

	public List<PushNotification> getNotifications() {
		SQLiteDatabase db = mDB.getReadableDatabase();
		String sql = " select * from "
				+ NotificationDBHelper.DB_TABLE_NOTIFICATION + " order by "
				+ NotificationDBHelper.DATE_KEY + " DESC";
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
								.getColumnIndex(NotificationDBHelper.TITLE_KEY)),
						cursor.getString(cursor
								.getColumnIndex(NotificationDBHelper.DESCRIPTION_KEY)),
						cursor.getString(cursor
								.getColumnIndex(NotificationDBHelper.AGENCYID_KEY)),
						cursor.getString(cursor
								.getColumnIndex(NotificationDBHelper.ROUTEID_KEY)),
						cursor.getString(cursor
								.getColumnIndex(NotificationDBHelper.ROUTESHORTNAME_KEY)),
						cursor.getString(cursor
								.getColumnIndex(NotificationDBHelper.TRIPID_KEY)),
						cursor.getString(cursor
								.getColumnIndex(NotificationDBHelper.JOURNEY_KEY)),
						Integer.parseInt(cursor.getString(cursor
								.getColumnIndex(NotificationDBHelper.DELAY_KEY))),
						Long.parseLong(cursor.getString(cursor
								.getColumnIndex(NotificationDBHelper.FROMTIME_KEY))),
						cursor.getString(cursor
								.getColumnIndex(NotificationDBHelper.STATION_KEY)),
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
			ContentValues cv = new ContentValues();
			cv.put(NotificationDBHelper.ID_KEY, id + "");
			db.update(NotificationDBHelper.DB_TABLE_NOTIFICATION, cv,
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

	public void markNotificationAsRead(PushNotification notif) {
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

	public void markAllNotificationAsRead() {
		SQLiteDatabase db = mDB.getWritableDatabase();
		// little hack
		db.execSQL("update " + NotificationDBHelper.DB_TABLE_NOTIFICATION
				+ " set " + NotificationDBHelper.READ_KEY + "=1 where "
				+ NotificationDBHelper.READ_KEY + "=0");
		db.close();
	}

	public int getUnreadNotificationCount() {
		SQLiteDatabase db = mDB.getReadableDatabase();
		Cursor mCount = db.rawQuery("select count(*) from "
				+ NotificationDBHelper.DB_TABLE_NOTIFICATION + " where "
				+ NotificationDBHelper.READ_KEY + " =0", null);
		mCount.moveToFirst();
		int count = mCount.getInt(0);
		mCount.close();
		return count;
	}

	private PushNotification buildPushNotification(Intent i) {
		String station = null;
		Long from = null;
		if (i.hasExtra(OPTIONAL_FIELD_STATION))
			station = i.getStringExtra(OPTIONAL_FIELD_STATION);
		if (i.hasExtra(OPTIONAL_FIELD_FROMTIME))
			from = Long.parseLong(i.getStringExtra(OPTIONAL_FIELD_FROMTIME));

		// String test = "[{"type":"journey","id":"123455476346353","title":"My
		// trip"}]";

		String journeyId = getJourneyId(i.getStringExtra(FIELD_ENTITY));

		return new PushNotification(i.getStringExtra(FIELD_TITLE),
				i.getStringExtra(FIELD_DESCRIPTION),
				i.getStringExtra(FIELD_AGENCYID),
				i.getStringExtra(FIELD_ROUTEID),
				i.getStringExtra(FIELD_ROUTESHORTNAME),
				i.getStringExtra(FIELD_TRIPID), journeyId, Integer.parseInt(i
						.getStringExtra(FIELD_DELAY)), from, station, null,
				false);
	}

	private String getJourneyId(String json) {
		try {
			JSONArray jsarr = new JSONArray(json);
			JSONObject entity = new JSONObject(jsarr.get(0).toString());
			return entity.getString("id");
		} catch (JSONException ex) {

			Log.e(this.getClass().getName(), ex.toString());
			Log.e(this.getClass().getName() + "CONTENT", json.toString());
		}
		return null;
	}

	public void showSystemNotification(int pushId, PushNotification notif,
			Class<? extends Activity> resultActivity) {
		NotificationCompat.Builder ncb = new NotificationCompat.Builder(
				mContext);
		ncb.setSmallIcon(R.drawable.ic_launcher);
		int unread = getUnreadNotificationCount();
		if (unread > 1) {
			ncb.setContentText(mContext.getResources().getString(
					R.string.push_notification_msg));
			ncb.setContentTitle(mContext.getResources().getString(
					R.string.push_notification_msg));
			ncb.setContentInfo(unread + "");
		} else {
			ncb.setContentText(notif.getDescription());
			ncb.setContentTitle(notif.getTitle());
			ncb.setContentInfo(notif.getDelay()/60000 + "m");
		}

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
