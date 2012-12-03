package edu.mines.csci498.shutup;

import java.util.ArrayList;
import java.util.List;

import android.app.ListActivity;
import android.content.Context;
import android.database.Cursor;
import android.media.AudioManager;
import android.os.Bundle;

public final class ShutUp extends ListActivity {

	private CalendarReader reader;
	private List<CalendarEvent> events;
	private EventHelper helper;
	private AudioManager audioManager;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.shut_up);

		reader = new CalendarReader();
		helper = new EventHelper(this);
		audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

		initializeEvents();
		refreshEvents();

	}

	/** 
	 * Adds all events in database to event list at start of application 
	 */
	private void initializeEvents() {
		events = new ArrayList<CalendarEvent>();

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

	//Testing
	private void changeRingVolume() {	 

		//SILENT
		//audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
		//audioManager.setStreamVolume(AudioManager.STREAM_RING, 0, AudioManager.FLAG_SHOW_UI); //Probably don't need this, good for debugging

		//VIBRATE
		//audioManager.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
		//audioManager.setStreamVolume(AudioManager.STREAM_RING, 0, AudioManager.FLAG_SHOW_UI); //Probably don't need this, good for debugging

		//LOUD
		//audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
		//audioManager.setStreamVolume(AudioManager.STREAM_RING, audioManager.getStreamMaxVolume(AudioManager.STREAM_RING), AudioManager.FLAG_SHOW_UI + AudioManager.FLAG_PLAY_SOUND);	
	}
}

