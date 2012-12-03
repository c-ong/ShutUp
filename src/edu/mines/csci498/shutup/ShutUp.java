package edu.mines.csci498.shutup;

import java.util.ArrayList;
import java.util.List;

import android.app.ListActivity;
import android.content.Context;
import android.database.Cursor;
import android.media.AudioManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public final class ShutUp extends ListActivity {

	private CalendarReader reader;
	private List<CalendarEvent> events;
	private EventHelper helper;
	private AudioManager audioManager;
	private CalendarEventAdapter adapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.shut_up);

		reader = new CalendarReader();
		helper = new EventHelper(this);
		events = new ArrayList<CalendarEvent>();

		adapter = new CalendarEventAdapter(this, R.layout.row, events);
		setListAdapter(adapter);

		initializeEvents();
		refreshEvents();
	}

	/** 
	 * Adds all events in database to event list at start of application 
	 */
	private void initializeEvents() {
		Cursor c = helper.getAllEvents();
		if (c.getCount() > 0) {
			c.moveToFirst();
			do {
				CalendarEvent e = new CalendarEvent(helper.getTitle(c), 
						Long.parseLong(helper.getStartTime(c)),
						Long.parseLong(helper.getEndTime(c)),
						Integer.parseInt(helper.getId(c)));
				//events.add(e);
				adapter.add(e);

			} while (c.moveToNext());
		}
	}

	/** 
	 * Reads events from calendar and adds new ones to the database and event list 
	 */
	private void refreshEvents() {
		List<CalendarEvent> eventsFromReader = reader.readCalendar(this);
		for (CalendarEvent e : eventsFromReader) {
			if (!helper.eventInDatabase(e.getEventId())) {
				helper.insert(e.getEventId(), e.getTitle(), e.getStartTime(), e.getEndTime(), RingVolume.NOT_SELECTED.getId());
				//events.add(e);
				adapter.add(e);
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

	class CalendarEventAdapter extends ArrayAdapter<CalendarEvent> {

		CalendarEventAdapter(Context context, int row, List<CalendarEvent> e) {
			super(context, row, e);
		}
		public View getView(int position, View convertView, ViewGroup parent) {
			return initializeRow(position, parent, convertView);
		}
		private View initializeRow(int position, ViewGroup parent, View row) {
			CalendarEventHolder holder;
			if( row == null ){
				row = getLayoutInflater().inflate(R.layout.row, parent, false);
				holder = new CalendarEventHolder(row);
				row.setTag(holder);
			} else {
				holder = (CalendarEventHolder)row.getTag();				
			}
			holder.populateFrom(events.get(position));
			return row;
		}
		public int getViewTypeCount() {
			return 1;
		}
		public int getItemViewType(int position) {
			return 1;
		}
	}

	static class CalendarEventHolder {

		private TextView title;
		private TextView time;

		CalendarEventHolder(View row) {
			title = ((TextView)row.findViewById(R.id.title));
			time = ((TextView)row.findViewById(R.id.time));		
		}

		void populateFrom(CalendarEvent e) {
			title.setText(e.getTitle());
			time.setText(e.getStartTime() + " - " + e.getEndTime());
		}

	}
}

