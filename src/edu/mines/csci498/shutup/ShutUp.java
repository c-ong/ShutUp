package edu.mines.csci498.shutup;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import android.app.ListActivity;
import android.content.Context;
import android.database.Cursor;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.v4.widget.CursorAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public final class ShutUp extends ListActivity {

	private CalendarReader reader;
	private Cursor eventCursor;
	private EventHelper helper;
	private AudioManager audioManager;
	private CalendarEventAdapter adapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.shut_up);

		reader = new CalendarReader();
		helper = new EventHelper(this);

		//TODO: Put these in an AsyncTask/separate thread
		deletePastEvents();
		refreshEvents();
		configureList();
	}

	/** 
	 * Reads events from calendar and adds new ones to the database and event list 
	 */
	private void refreshEvents() {
		List<CalendarEvent> eventsFromReader = reader.readCalendar(this);
		for (CalendarEvent e : eventsFromReader) {
			if (!helper.eventInDatabase(e.getEventId())) {
				helper.insert(e.getEventId(), e.getTitle(), e.getStartTime(), e.getEndTime(), RingVolume.NOT_SELECTED.getId());
			}	
		}
	}

	/** 
	 * Deletes past events from database (past end date)
	 */
	private void deletePastEvents() {
		Cursor c = helper.getAllEvents();
		if (c.getCount() <= 0) {
			return; //No events in database
		}
		c.moveToFirst();
		do {
			long endTime = Long.parseLong(helper.getEndTime(c));
			if (endTime < System.currentTimeMillis()) { //If end date is before right now, delete from database
				helper.deleteEvent(helper.getId(c)); 
			}
		} while (c.moveToNext());
		c.close();
	}

	/**
	 * Configures the cursor and sets the adapter for the list
	 */
	//TODO: Try using a CursorLoader
	private void configureList() {
		if (eventCursor != null) {
			stopManagingCursor(eventCursor);
			eventCursor.close();
		}
		eventCursor = helper.getAllEvents();
		startManagingCursor(eventCursor);
		adapter = new CalendarEventAdapter(eventCursor);

		setListAdapter(adapter);
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

	/**
	 * Handles populating the ListView with Calendar Events
	 *
	 */
	class CalendarEventAdapter extends CursorAdapter {

		CalendarEventAdapter(Cursor c) {
			super(ShutUp.this, c, 0);
		}

		/**
		 * Binds row with event data
		 * @param row - row to put event data in (existing view)
		 * @param context - interface to application's global information
		 * @param cursor - the cursor from which to get the data
		 */
		@Override
		public void bindView(View row, Context ctx, Cursor c) {
			CalendarEventHolder holder = (CalendarEventHolder) row.getTag();
			holder.populateFrom(c, helper);
			updateRowColorsFromRingVolume(row, helper.getRingVolume(c));
		}

		/**
		 * Creates a new row with event data
		 * @param context - interface to application's global information
		 * @param cursor - the cursor from which to get the data
		 * @param parent - the parent to which the new view is attached
		 */
		@Override
		public View newView(Context ctx, Cursor c, ViewGroup parent) {
			View row = getLayoutInflater().inflate(R.layout.row, parent, false);

			CalendarEventHolder holder = new CalendarEventHolder(row);
			row.setTag(holder);
			updateRowColorsFromRingVolume(row, helper.getRingVolume(c));
			return row;
		}

		/**
		 * Updates background and text of row according to ring volume
		 * @param row - row to update color for
		 * @param volume - ring volume for current event
		 */
		private void updateRowColorsFromRingVolume(View row, String volumeString) {
			CalendarEventHolder holder = (CalendarEventHolder) row.getTag();

			RingVolume volume = RingVolume.values()[Integer.parseInt(volumeString) - 1];

			switch (volume) {
			case NOT_SELECTED:
				row.setBackgroundColor(ShutUp.this.getResources().getColor(R.color.grey));
				holder.title.setTextColor(ShutUp.this.getResources().getColor(R.color.black));
				holder.time.setTextColor(ShutUp.this.getResources().getColor(R.color.black));
				break;
			case SILENT:
				row.setBackgroundColor(ShutUp.this.getResources().getColor(R.color.red));
				holder.title.setTextColor(ShutUp.this.getResources().getColor(R.color.white));
				holder.time.setTextColor(ShutUp.this.getResources().getColor(R.color.white));
				break;
			case VIBRATE:
				row.setBackgroundColor(ShutUp.this.getResources().getColor(R.color.yellow));
				holder.title.setTextColor(ShutUp.this.getResources().getColor(R.color.black));
				holder.time.setTextColor(ShutUp.this.getResources().getColor(R.color.black));
				break;
			case LOUD:
				row.setBackgroundColor(ShutUp.this.getResources().getColor(R.color.green));
				holder.title.setTextColor(ShutUp.this.getResources().getColor(R.color.white));
				holder.time.setTextColor(ShutUp.this.getResources().getColor(R.color.white));
				break;
			}	
		}
	}

	/**
	 * This class allows us to use the ViewHolder pattern to make our ListView more efficient
	 */
	static class CalendarEventHolder {

		private TextView title;
		private TextView time;
		private static Calendar calendar = GregorianCalendar.getInstance();

		CalendarEventHolder(View row) {
			title = ((TextView)row.findViewById(R.id.title));
			time = ((TextView)row.findViewById(R.id.time));	
		}

		/**
		 * Populates the holder with the event details
		 * @param e - event to populate
		 */
		void populateFrom(Cursor c, EventHelper helper) {
			title.setText(helper.getTitle(c));
			time.setText(formatDateString(Long.parseLong(helper.getStartTime(c))) + 
					" \nto " + 
					formatDateString(Long.parseLong(helper.getStartTime(c))));
		}

		/**
		 * Formats a long into a human readable date
		 * @param date - date to format
		 * @return string of date in human readable form
		 */
		private String formatDateString(long date) {
			SimpleDateFormat format = new SimpleDateFormat("EEE, MMM d, yyyy - h:mm aaa");
			calendar.setTimeInMillis(date);
			return format.format(calendar.getTime());
		}

	}
}

