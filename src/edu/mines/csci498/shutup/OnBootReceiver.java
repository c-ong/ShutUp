package edu.mines.csci498.shutup;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.util.Log;


public class OnBootReceiver extends BroadcastReceiver {

	public static String EXTRA_VOLUME_STRING = "edu.mines.csci498.shutup.volume_string";

	@Override
	public void onReceive(Context context, Intent intent) {
		//setAlarm(context);
		Log.i("OnBootReciever", "In onRecieve!");
	}

	public static void setAlarm(Context context, long startTime, long endTime, int eventId, String volumeString) {
		AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		String currentVolumeString = getVolumeStringForCurrentRingVolume(context);

		manager.set(AlarmManager.RTC_WAKEUP, startTime, getPendingIntent(context, eventId, volumeString)); //Event start
		manager.set(AlarmManager.RTC_WAKEUP, endTime, getPendingIntent(context, -1 * eventId, currentVolumeString)); //Event end
		Log.i("OnBootReciever", "Set alarm!");
	}

	public static void cancelAlarm(Context context, int eventId, String volumeString) {
		AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		manager.cancel(getPendingIntent(context, eventId, volumeString));
		Log.i("OnBootReciever", "Cancel alarm!");
	}

	private static PendingIntent getPendingIntent(Context context, int eventId, String volumeString) {
		Intent i = new Intent(context, OnEventStartReceiver.class);
		i.putExtra(EXTRA_VOLUME_STRING, volumeString);
		Log.i("OnBootReceiver", "EventId: " + eventId);
		Log.i("OnBootReceiver", "Volume: " + volumeString);
		return PendingIntent.getBroadcast(context, eventId, i, PendingIntent.FLAG_CANCEL_CURRENT);
	}

	private static String getVolumeStringForCurrentRingVolume(Context context) {
		AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
		int currentMode = audioManager.getRingerMode();

		switch (currentMode) {
		case AudioManager.RINGER_MODE_NORMAL:
			return Integer.toString(RingVolume.LOUD.getId());
		case AudioManager.RINGER_MODE_VIBRATE:
			return Integer.toString(RingVolume.VIBRATE.getId());
		case AudioManager.RINGER_MODE_SILENT:
			return Integer.toString(RingVolume.SILENT.getId());
		default:
			return null;
		}
	}
}
