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

	static final String DB_NAME = "notificationsdb";

	static final int DB_VERSION = 1;

	static final  String DB_TABLE_NOTIFICATION = "tb_notification";

	static final String ID_KEY = "not_id";

	static final String TITLE_KEY = "title";
	static final String DESCRIPTION_KEY = "text";
	static final String DATE_KEY = "date";
	static final String AGENCYID_KEY = "agencyid";
	static final String ROUTEID_KEY = "routeid";
	static final String ROUTESHORTNAME_KEY = "routename";
	static final String TRIPID_KEY = "tripid";
	static final String DELAY_KEY = "delay";
	static final String STATION_KEY = "station";

	// 0 for read
	// 1 for unread
	static final String READ_KEY = "read";

	

	public NotificationDBHelper(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
	}

	private static final String CREATE_NOTIFICATION_TABLE = "CREATE TABLE IF NOT EXISTS "
			+ DB_TABLE_NOTIFICATION
			+ " ("
			+ ID_KEY
			+ " integer PRIMARY_KEY, "
			+ TITLE_KEY
			+ " text not null, "
			+ DESCRIPTION_KEY
			+ " text not null, "
			+ AGENCYID_KEY
			+ " text not null, "
			+ ROUTEID_KEY
			+ " text not null, "
			+ ROUTESHORTNAME_KEY
			+ " text not null, "
			+ DELAY_KEY
			+ " text not null, "
			+ TRIPID_KEY
			+ " text not null, "
			+ STATION_KEY
			+ " text, "
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

	

}
