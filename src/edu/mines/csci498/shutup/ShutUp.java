/**
 * This class is the main activity for the ShutUp application
 * @author Lauren Aberle
 * @author Thomas Brown
 */

package edu.mines.csci498.shutup;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import android.app.ListActivity;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public final class ShutUp extends ListActivity {

	private CalendarReader reader;
	private Cursor eventCursor;
	private EventHelper helper;
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

	@Override
	public void onDestroy() {
		eventCursor.close();
		helper.close();
		super.onDestroy();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.shut_up, menu);
	    return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	        case R.id.refresh:
	        	deletePastEvents();
	    		refreshEvents();
	    		configureList();
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
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
			updateRowFromRingVolume(row, getRingVolumeEnumFromVolumeId(helper.getRingVolume(c)));

			row.setClickable(true);
			row.setFocusable(true);
			row.setOnClickListener(new ToggleRingerListener(c, row));
		}

		/**
		 * Button click listener to toggle ring volume
		 */
		class ToggleRingerListener implements View.OnClickListener {

			String idString;
			View rowView;
			RingVolume volume;

			public ToggleRingerListener(Cursor cursor, View rowView) {
				this.rowView = rowView;
				idString = helper.getId(cursor);
				volume = getRingVolumeEnumFromVolumeId(helper.getRingVolume(cursor));
			}

			/**
			 * Toggles volume on button click and delegates updating the database, UI, and alarms
			 */
			public void onClick(View v) {				
				switch (volume) {
				case NOT_SELECTED:
					helper.updateRingVolume(idString, RingVolume.SILENT.getId());
					volume = RingVolume.SILENT;
					break;
				case SILENT:
					helper.updateRingVolume(idString, RingVolume.VIBRATE.getId());
					volume = RingVolume.VIBRATE;
					break;
				case VIBRATE:
					helper.updateRingVolume(idString, RingVolume.LOUD.getId());
					volume = RingVolume.LOUD;
					break;
				case LOUD:
					helper.updateRingVolume(idString, RingVolume.NOT_SELECTED.getId());
					volume = RingVolume.NOT_SELECTED;
					break;
				}

				updateRowFromRingVolume(rowView, volume);
				setEventAlarms(idString);
			}
		};

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
			updateRowFromRingVolume(row, getRingVolumeEnumFromVolumeId(helper.getRingVolume(c)));
			return row;
		}

		/**
		 * Updates background and text of row according to ring volume
		 * @param row - row to update color for
		 * @param volume - ring volume for current event
		 */
		private void updateRowFromRingVolume(View row, RingVolume volume) {
			CalendarEventHolder holder = (CalendarEventHolder) row.getTag();

			switch (volume) {
			case NOT_SELECTED:
				row.setBackgroundColor(ShutUp.this.getResources().getColor(R.color.grey));
				holder.image.setImageResource(R.drawable.ic_no_selection);
				break;
			case SILENT:
				row.setBackgroundColor(ShutUp.this.getResources().getColor(R.color.red));
				holder.image.setImageResource(R.drawable.ic_silent);
				break;
			case VIBRATE:
				row.setBackgroundColor(ShutUp.this.getResources().getColor(R.color.yellow));
				holder.image.setImageResource(R.drawable.ic_vibrate);
				break;
			case LOUD:
				row.setBackgroundColor(ShutUp.this.getResources().getColor(R.color.green));
				holder.image.setImageResource(R.drawable.ic_loud);
				break;
			}	
		}

		/**
		 * Delegates creating/removing alarms for event to AlarmHelper 
		 * @param idString - database id for event to set alarm for (string)
		 */
		public void setEventAlarms(String idString) {
			CalendarEvent event = helper.getCalendarEventObjectById(idString);
			AlarmHelper.handleAlarms(ShutUp.this, event);
		}

		/**
		 * Returns the RingVolume enum of a volume id
		 * @param volumeString - id for volume (from database)
		 * @return - RingVolume enum for given volume id
		 */
		public RingVolume getRingVolumeEnumFromVolumeId(String volumeString) {
			return RingVolume.values()[Integer.parseInt(volumeString) - 1];
		}

	}

	/**
	 * This class allows us to use the ViewHolder pattern to make our ListView more efficient
	 */
	static class CalendarEventHolder {

		private TextView title;
		private TextView time;
		private ImageView image;
		private static Calendar calendar = GregorianCalendar.getInstance();

		CalendarEventHolder(View row) {
			title = (TextView) row.findViewById(R.id.title);
			time = (TextView) row.findViewById(R.id.time);	
			image = (ImageView) row.findViewById(R.id.volume_icon);
		}

		/**
		 * Populates the holder with the event details
		 * @param e - event to populate
		 */
		void populateFrom(Cursor c, EventHelper helper) {
			title.setText(helper.getTitle(c));
			StringBuilder timeString = new StringBuilder();
			timeString.append(formatDateString(Long.parseLong(helper.getStartTime(c))));
			timeString.append("\nto ");
			timeString.append(formatDateString(Long.parseLong(helper.getEndTime(c))));
			time.setText(timeString);
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

