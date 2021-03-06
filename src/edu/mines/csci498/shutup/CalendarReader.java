/**
 * Helper class that retrieves a user's calendars and their events from Google Calendar.
 * Use the readCalendar(context) method to retrieve events
 * @author Lauren Aberle
 * @author Thomas Brown
 */

package edu.mines.csci498.shutup;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.text.format.DateUtils;
import android.util.Log;

public class CalendarReader {

	private ContentResolver contentResolver;

	/**
	 * Retrieves a user's calendars and their events from Google Calendar
	 * @param context - the activity
	 * @return list of user's events
	 */
	public List<CalendarEvent> readCalendar(Context context) {
		contentResolver = context.getContentResolver();

		HashSet<String> calendarIds = (HashSet<String>) getCalendars();
		List<CalendarEvent> events = getEvents(calendarIds);
		return events;
	}

	/** 
	 * Retrieves the user's events for each calendar
	 * @param calendarIds - set of user's calendar ids
	 * @return list of user's events
	 */
	private List<CalendarEvent> getEvents(Set<String> calendarIds) {
		List<CalendarEvent> events = new ArrayList<CalendarEvent>();		
		long now = new Date().getTime();
		
		// For each calendar, display all the events from now until 4 weeks from now.
		// TODO: Decide how far out we want to grab events
		for (String id : calendarIds) {
			Uri.Builder builder = Uri.parse("content://com.android.calendar/instances/when").buildUpon();
			ContentUris.appendId(builder, now);
			ContentUris.appendId(builder, now + 4 * DateUtils.WEEK_IN_MILLIS);

			// Find column names at core/java/android/provider/Calendar.java
			Cursor eventCursor = contentResolver.query(builder.build(),
					new String[] { "title", "begin", "end", "event_id"}, "Calendars._id=" + id,
					null, "startDay ASC, startMinute ASC"); 
			if (eventCursor.getCount()  <= 0) {
				Log.e("CalendarReader", "No events found!");
				//TODO: Throw exception?
				CalendarEvent e = new CalendarEvent("No events found - add some to your Google Calendar!", 
						System.currentTimeMillis(), System.currentTimeMillis(), 0, RingVolume.NOT_SELECTED);
				events.add(e);
				return events;
			}
			eventCursor.moveToFirst();
			
			do {
				String title = eventCursor.getString(0);
				long start = eventCursor.getLong(1);
				long end = eventCursor.getLong(2);
				int eventId = eventCursor.getInt(3);
				
				CalendarEvent e = new CalendarEvent(title, start, end, eventId, RingVolume.NOT_SELECTED);
				events.add(e);
			} while (eventCursor.moveToNext());
			eventCursor.close();
		}
		
		return events;
	}
	
	/**
	 * Retrieves the user's calendars
	 * @return - set of calendar ids
	 */
	private Set<String> getCalendars() {
		HashSet<String> calendarIds = new HashSet<String>();
		
		// Fetch a list of all calendars synced with the device, their display names and whether the
		// user has them selected for display.
		final Cursor cursor = contentResolver.query(Uri.parse("content://com.android.calendar/calendars"),
				(new String[] { "_id" }), null, null, null);

		cursor.moveToFirst();
		
		do {
			String id = cursor.getString(0);
			calendarIds.add(id);
		} while (cursor.moveToNext());
		
		cursor.close();
		
		if (calendarIds.isEmpty()) {
			//TODO: Decide what we want to do if there are no calendars (throw an exception?)
			Log.e("CalendarReader", "No calendars found!");
		}
		
		return calendarIds;
	}

}