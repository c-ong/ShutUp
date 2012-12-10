package edu.mines.csci498.shutup;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;


public class OnBootReceiver extends BroadcastReceiver {
	
	public static String EXTRA_VOLUME_STRING = "edu.mines.csci498.shutup.volume_string";

	@Override
	public void onReceive(Context context, Intent intent) {
		//setAlarm(context);
		Log.i("OnBootReciever", "In onRecieve!");
	}
	
	public static void setAlarm(Context context, long startTime, int eventId, String volumeString) {
		//TODO: Actually use startTime
		AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		manager.set(AlarmManager.RTC_WAKEUP, startTime, getPendingIntent(context, eventId, volumeString));
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
		return PendingIntent.getBroadcast(context, eventId, i, PendingIntent.FLAG_CANCEL_CURRENT);
	}
}
