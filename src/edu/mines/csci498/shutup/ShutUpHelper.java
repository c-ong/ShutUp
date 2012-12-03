package edu.mines.csci498.shutup;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ShutUpHelper extends SQLiteOpenHelper {

	private static final String DATABASE_NAME = "shutup.db";
	private static final int SCHEMA_VERSION = 1;

	public ShutUpHelper(Context context) {
		super(context, DATABASE_NAME, null, SCHEMA_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE ring_volumes (" +
				"_id INTEGER PRIMARY KEY AUTOINCREMENT," +
				"name TEXT);");
		db.execSQL("CREATE TABLE events (" +
			"_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
			"title TEXT, " +
			"start_time INTEGER, " +
			"end_time INTEGER, " +
			"ring_volume_id INTEGER," +
			"FOREIGN KEY(ring_volume_id) REFERENCES ring_volumes(_id));");
		
		initializeRingVolumesTable(db);
	}

	private void initializeRingVolumesTable(SQLiteDatabase db) {
		ContentValues cv = new ContentValues();
		cv.put("name", "loud");
		db.insert("ring_volumes", null, cv);
		cv.put("name", "vibrate");
		db.insert("ring_volumes", null, cv);
		cv.put("name", "silent");
		db.insert("ring_volumes", null, cv);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		//Intentionally blank
	}

	public void insert(String title, long startTime, long endTime, int ringVolumeId) {

		ContentValues cv = new ContentValues();
		
		cv.put("title", title);
		cv.put("start_time", startTime);
		cv.put("end_time", endTime);
		cv.put("ring_volume_id", ringVolumeId);

		if (cv.size() > 0) {
			getWritableDatabase().insert("events", null, cv);
		}
	}
	
	public void update(String id, String title, long startTime, long endTime, int ringVolumeId) {

		ContentValues cv = new ContentValues();
		String[] args = {id};

		cv.put("title", title);
		cv.put("start_time", startTime);
		cv.put("end_time", endTime);
		cv.put("ring_volume_id", ringVolumeId);

		if (cv.size() > 0) {
			getWritableDatabase().update("events", cv, "_id=?", args);
		}
	}

	public Cursor getAll() {
		return getReadableDatabase()
			.rawQuery("SELECT * " +
					  "FROM events, ring_volumes " +
					  "WHERE events.ring_volume_id = ring_volumes._id " + 
					  "ORDER BY start_time", 
					  null);
	}
	
	public Cursor getById(String id) {
		String[] args = {id};
		
		return getReadableDatabase()
			.rawQuery("SELECT * " +
					"FROM events, ring_volumes " +
					"WHERE events.ring_volume_id = ring_volumes._id " +
					"AND events.id = ? " +
					"ORDER BY start_time", 
					args);
	}

	public String getTitle(Cursor c) {
		return c.getString(1);
	}
	public String getStartTime(Cursor c) {
		return c.getString(2);
	}
	public String getEndTime(Cursor c) {
		return c.getString(3);
	}
	public String getRingVolume(Cursor c) {
		return c.getString(4);
	}
}
