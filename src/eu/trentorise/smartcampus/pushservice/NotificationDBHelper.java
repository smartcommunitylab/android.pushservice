package eu.trentorise.smartcampus.pushservice;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.util.Log;

public class NotificationDBHelper extends SQLiteOpenHelper {

	private static final String DB_NAME = "notificationsdb";

	private static final int DB_VERSION = 1;

	private static String DB_TABLE_NOTIFICATION = "tb_notification";

	public static String ID_KEY = "not_id";

	public static String TEXT_KEY = "text";

	// 0 for read
	// 1 for unread
	public static String READ_KEY = "read";

	public static String DATE_KEY = "date";

	public NotificationDBHelper(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
	}

	private static final String CREATE_NOTIFICATION_TABLE = "CREATE TABLE IF NOT EXISTS "
			+ DB_TABLE_NOTIFICATION
			+ " ("
			+ ID_KEY
			+ " integer PRIMARY_KEY, "
			+ TEXT_KEY
			+ " text not null, "
			+ READ_KEY
			+ " integer DEFAULT 0, "
			+ DATE_KEY + " DATE DEFAULT (datetime('now')));";

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREATE_NOTIFICATION_TABLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO nothing for now.
	}

	public List<PushNotification> getNotifications() {
		SQLiteDatabase db = this.getReadableDatabase();
		String sql = " select * from " + DB_TABLE_NOTIFICATION;
		ArrayList<PushNotification> notifs = new ArrayList<PushNotification>();
		try {
			db.beginTransaction();
			Cursor cursor = db.rawQuery(sql, null);
			db.setTransactionSuccessful();

			while (cursor.moveToNext()) {
				PushNotification notif = new PushNotification(
						cursor.getInt(cursor.getColumnIndex(ID_KEY)),
						cursor.getString(cursor.getColumnIndex(TEXT_KEY)),
						new Date(
								cursor.getLong(cursor.getColumnIndex(DATE_KEY))),
						cursor.getInt(cursor.getColumnIndex(READ_KEY)) > 0 ? true
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
		SQLiteDatabase db = this.getWritableDatabase();
		try {
			db.beginTransaction();
			db.insert(DB_TABLE_NOTIFICATION, null, notif.toContentValues());
			db.setTransactionSuccessful();
		} catch (Exception ex) {
			Log.e(this.getClass().getName(), ex.toString());
		} finally {
			db.endTransaction();
		}
		db.close();
	}

	public void insertNotification(String msg) {
		SQLiteDatabase db = this.getWritableDatabase();
		try {
			db.beginTransaction();
			db.insert(DB_TABLE_NOTIFICATION, null,
					new PushNotification(msg).toContentValues());
			db.setTransactionSuccessful();
		} catch (Exception ex) {
			Log.e(this.getClass().getName(), ex.toString());
		} finally {
			db.endTransaction();
		}
		db.close();
	}

	public void deleteNotification(int id) {
		SQLiteDatabase db = this.getWritableDatabase();
		try {
			db.beginTransaction();
			db.delete(DB_TABLE_NOTIFICATION, ID_KEY + "=?", new String[] { id
					+ "" });
			db.setTransactionSuccessful();
		} catch (Exception ex) {
			Log.e(this.getClass().getName(), ex.toString());
		} finally {
			db.endTransaction();
		}
		db.close();
	}

	public void setNotificationRead(int id) {
		SQLiteDatabase db = this.getWritableDatabase();
		try {
			db.beginTransaction();
			db.update(DB_TABLE_NOTIFICATION,
					new PushNotification(id).toContentValues(), ID_KEY + "=?",
					new String[] { id + "" });
			db.setTransactionSuccessful();
		} catch (Exception ex) {
			Log.e(this.getClass().getName(), ex.toString());
		} finally {
			db.endTransaction();
		}
		db.close();
	}
}
