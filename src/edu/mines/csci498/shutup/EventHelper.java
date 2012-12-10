/**
 * Database Helper class that retrieves a user's calendar events from the database.
 * @author Lauren Aberle
 * @author Thomas Brown
 */
package edu.mines.csci498.shutup;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class EventHelper extends SQLiteOpenHelper {

	private static final String DATABASE_NAME = "shutup.db";
	private static final int SCHEMA_VERSION = 1;

	public EventHelper(Context context) {
		super(context, DATABASE_NAME, null, SCHEMA_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE ring_volumes (" +
				"_id INTEGER PRIMARY KEY AUTOINCREMENT," +
				"name TEXT);");
		db.execSQL("CREATE TABLE events (" +
			"_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
			"event_id INTEGER, " +
			"title TEXT, " +
			"start_time INTEGER, " +
			"end_time INTEGER, " +
			"ring_volume_id INTEGER, " +
			"FOREIGN KEY(ring_volume_id) REFERENCES ring_volumes(_id));");
		
		initializeRingVolumesTable(db);
	}
	
	private void initializeRingVolumesTable(SQLiteDatabase db) {
		ContentValues cv = new ContentValues();
		cv.put("name", "not selected");
		db.insert("ring_volumes", null, cv);
		cv.put("name", "silent");
		db.insert("ring_volumes", null, cv);
		cv.put("name", "vibrate");
		db.insert("ring_volumes", null, cv);
		cv.put("name", "loud");
		db.insert("ring_volumes", null, cv);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		//Intentionally blank
	}

	public void insert(int eventId, String title, long startTime, long endTime, int ringVolumeId) {

		ContentValues cv = new ContentValues();
		
		cv.put("event_id", eventId);
		cv.put("title", title);
		cv.put("start_time", startTime);
		cv.put("end_time", endTime);
		cv.put("ring_volume_id", ringVolumeId);

		if (cv.size() > 0) {
			getWritableDatabase().insert("events", null, cv);
		}
	}
	
	public void updateRingVolume(String id, int ringVolumeId) {
		
		ContentValues cv = new ContentValues();
		String[] args = {id};
		
		cv.put("ring_volume_id", ringVolumeId);
			
		if (cv.size() > 0) {
			getWritableDatabase().update("events", cv, "_id=?", args);
		}	
	}
	
	public void update(int eventId, String id, String title, long startTime, long endTime, int ringVolumeId) {

		ContentValues cv = new ContentValues();
		String[] args = {id};

		cv.put("event_id", eventId);
		cv.put("title", title);
		cv.put("start_time", startTime);
		cv.put("end_time", endTime);
		cv.put("ring_volume_id", ringVolumeId);

		if (cv.size() > 0) {
			getWritableDatabase().update("events", cv, "_id=?", args);
		}
	}

	public Cursor getAllEvents() {
		return getReadableDatabase()
			.rawQuery("SELECT * " +
					  "FROM events, ring_volumes " +
					  "WHERE events.ring_volume_id = ring_volumes._id " +
					  "ORDER BY events.start_time",
					  null);
	}
	
	//Debugging purposes only
	public void printAllEvents() {		
		Cursor c = getAllEvents();
		c.moveToFirst();
		do {
			StringBuilder builder = new StringBuilder();
			builder.append(getId(c));
			builder.append(getEventId(c));
			builder.append(getTitle(c));
			builder.append(getStartTime(c));
			builder.append(getEndTime(c));
			builder.append(" ");
			builder.append(getRingVolume(c));
			
			Log.i("Database", builder.toString());
		} while (c.moveToNext());
	}
	
	public Cursor getEventById(String id) {
		String[] args = {id};
		
		return getReadableDatabase()
			.rawQuery("SELECT * " +
					"FROM events, ring_volumes " +
					"WHERE events.ring_volume_id = ring_volumes._id " +
					"AND events._id = ? " +
					"ORDER BY start_time", 
					args);
	}
	
	public void deleteAllEvents() {
		Cursor eventCursor = getAllEvents();
		eventCursor.moveToFirst();
		
		while (eventCursor.moveToNext()) {
			deleteEvent(getId(eventCursor));
		}
	}
	
	public void deleteEvent(String id) {
		String[] args = {id};
		getWritableDatabase().execSQL(
				"DELETE " +
				"FROM events " +
				"WHERE _id = ?",
				args);
	}
	
	/**
	 * Determines if an event already exists in the database
	 * @param eventId - calendar event id for event in question
	 * @return - true if the event is in database, false otherwise
	 */
	public boolean eventInDatabase(int eventId) {
		String[] args = {Integer.toString(eventId)};
		
		Cursor c = getReadableDatabase()
			.rawQuery("SELECT * " +
				      "FROM events " +
					  "WHERE events.event_id = ?",
					  args);
		c.moveToFirst();
		int count = c.getCount();
		if (count > 0) {
			return true;
		}
		else {
			return false;
		}
	}
	
	public String getId(Cursor c) {
		return c.getString(0);
	}
	public String getEventId(Cursor c) {
		return c.getString(1);
	}
	public String getTitle(Cursor c) {
		return c.getString(2);
	}
	public String getStartTime(Cursor c) {
		return c.getString(3);
	}
	public String getEndTime(Cursor c) {
		return c.getString(4);
	}
	public String getRingVolume(Cursor c) {
		return c.getString(5);
	}
}
