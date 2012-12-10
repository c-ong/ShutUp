/**
 * This class handles setting and cancelling all "alarms" to change ring volume.
 * @author Lauren Aberle
 * @author Thomas Brown
 */

package edu.mines.csci498.shutup;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.AudioManager;
import android.util.Log;

public class AlarmHelper {

	public static String EXTRA_VOLUME_ID = "edu.mines.csci498.shutup.volume_id";

	/**
	 * Sets an alarm for start of event (to change to specified ring volume)
	 * and an alarm for the end of the event (to change back to previous ring volume)
	 * @param context - context under which the AlarmHelper operates
	 * @param event - event to set alarms for
	 */
	public static void setAlarm(Context context, CalendarEvent event) {
		AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		int currentVolumeId = getVolumeIdForCurrentRingVolume(context);

		manager.set(AlarmManager.RTC_WAKEUP, event.getStartTime(), 
				getPendingIntent(context, event.getEventId(), event.getRingVolume().getId())); //Event start
		manager.set(AlarmManager.RTC_WAKEUP, event.getEndTime(),
				getPendingIntent(context, -1 * event.getEventId(), currentVolumeId)); //Event end
		Log.i("AlarmHelper", "Set alarm!");
	}

	/**
	 * Cancels an alarm for an event
	 * This happens when the ring volume was previously set during event, then set to "NOT_SELECTED"
	 * @param context - context under which the AlarmHelper operates
	 * @param event - event to cancel alarm for
	 */
	public static void cancelAlarm(Context context, CalendarEvent event) {
		AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

		manager.cancel(getPendingIntent(context, event.getEventId(), event.getRingVolume().getId()));
		Log.i("AlarmHelper", "Cancel alarm!");
	}

	/**
	 * Creates pending intent to fire OnEventStartReceiver when it's time to change the ring volume
	 * @param context - context under which the AlarmHelper operates
	 * @param eventId - eventId for event to change volume for
	 * @param volumeId - volumeId to change to
	 * @return
	 */
	private static PendingIntent getPendingIntent(Context context, int eventId, int volumeId) {
		Intent i = new Intent(context, OnEventStartReceiver.class);
		i.putExtra(EXTRA_VOLUME_ID, volumeId);
		return PendingIntent.getBroadcast(context, eventId, i, PendingIntent.FLAG_CANCEL_CURRENT);
	}

	/**
	 * Gets current ring volume from phone
	 * @param context - context under which the AlarmHelper operates
	 * @return - volumeId for the current ring volume
	 */
	private static int getVolumeIdForCurrentRingVolume(Context context) {
		AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
		int currentMode = audioManager.getRingerMode();

		switch (currentMode) {
		case AudioManager.RINGER_MODE_NORMAL:
			return RingVolume.LOUD.getId();
		case AudioManager.RINGER_MODE_VIBRATE:
			return RingVolume.VIBRATE.getId();
		case AudioManager.RINGER_MODE_SILENT:
			return RingVolume.SILENT.getId();
		default:
			return -1;
		}
	}

	/**
	 * Sets proper alarms for all events in the database
	 * Called when device reboots
	 * @param context - context under which the AlarmHelper operates
	 */
	public static void setAllAlarms(Context context) {
		EventHelper helper = new EventHelper(context);
		Cursor c = helper.getAllEvents();
		if (c.getCount() <= 0) {
			Log.e("AlarmHelper", "Could not get any events!");
			return;
		}
		
		c.moveToFirst();
		do {
			CalendarEvent event = helper.getCalendarEventObjectById(helper.getId(c));
			Log.i("AlarmHelper", "Creating alarms for event: " + event);
			AlarmHelper.handleAlarms(context, event);
			
		} while (c.moveToNext());
		helper.close();
	}

	/**
	 * Sets/cancels alarm according to event
	 * @param context - context under which the AlarmHelper operates
	 * @param event - event to set/cancel alarm for
	 */
	public static void handleAlarms(Context context, CalendarEvent event) {
		//Enable alarm if we want to alter ring volume
		boolean enabled = true;
		if (event.getRingVolume() == RingVolume.NOT_SELECTED) {
			enabled = false;
		}
		int componentState = (enabled ?
				PackageManager.COMPONENT_ENABLED_STATE_ENABLED :
					PackageManager.COMPONENT_ENABLED_STATE_DISABLED);

		ComponentName component = new ComponentName(context, OnEventStartReceiver.class);
		context.getPackageManager().setComponentEnabledSetting(component, componentState, PackageManager.DONT_KILL_APP);

		if (enabled) { AlarmHelper.setAlarm(context, event); }
		else 		 { AlarmHelper.cancelAlarm(context, event);}
	}
}
