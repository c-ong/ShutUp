package edu.mines.csci498.shutup;

import java.util.List;

import android.app.ListActivity;
import android.database.Cursor;
import android.os.Bundle;

public final class ShutUp extends ListActivity {
	
	private CalendarReader reader;
	private List<CalendarEvent> events;
	private EventHelper helper;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.shut_up);
		
		reader = new CalendarReader();
		helper = new EventHelper(this);
		
		initializeEvents();
		refreshEvents();
	}
	
	/** 
	 * Adds all events in database to event list at start of application 
	 */
	private void initializeEvents() {
		Cursor c = helper.getAllEvents();
		c.moveToFirst();
		do {
			CalendarEvent e = new CalendarEvent(helper.getTitle(c), 
				Long.parseLong(helper.getStartTime(c)),
				Long.parseLong(helper.getEndTime(c)),
				Integer.parseInt(helper.getId(c)));
			events.add(e);
			
		} while (c.moveToNext());
	}

	/** 
	 * Reads events from calendar and adds new ones to the database and event list 
	 */
	private void refreshEvents() {
		List<CalendarEvent> eventsFromReader = reader.readCalendar(this);
		for (CalendarEvent e : eventsFromReader) {
			if (!helper.eventInDatabase(e.getEventId())) {
				helper.insert(e.getEventId(), e.getTitle(), e.getStartTime(), e.getEndTime(), RingVolume.NOT_SELECTED.getId());
				events.add(e);
			}	
		}
	}
}

