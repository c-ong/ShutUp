/**
 * OnEventStartReceiver receives alarms when an event starts and changes ring volume accordingly
 * @author Lauren Aberle
 * @author Thomas Brown
 */
package edu.mines.csci498.shutup;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.util.Log;


public class OnEventStartReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		if (intent != null) {
			int volumeId = (int) intent.getIntExtra(AlarmHelper.EXTRA_VOLUME_ID, 1);
			changeRingVolume(context, volumeId);
			Log.i("OnEventStartReciever", "Received event!");
		}
		else {
			Log.e("OnEventStartReciever", "No extra in intent!");
		}		
	}

	/**
	 * Changes system's ring volume to volume specified by volumeId
	 * @param context - context under which to operate
	 * @param volumeId - volume to change to
	 */
	private void changeRingVolume(Context context, int volumeId) {
		AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
		RingVolume volume = RingVolume.values()[volumeId - 1];
		
		switch (volume) {
		case NOT_SELECTED:
			Log.e("OnEventStartReceiver", "Shouldn't have set an alarm for an unselected ring volume!");
			break;
		case SILENT:
			audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
			audioManager.setStreamVolume(AudioManager.STREAM_RING, 0, AudioManager.FLAG_SHOW_UI);		
			break;
		case VIBRATE:
			audioManager.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
			audioManager.setStreamVolume(AudioManager.STREAM_RING, 0, AudioManager.FLAG_SHOW_UI);
			break;
		case LOUD:
			audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
			audioManager.setStreamVolume(AudioManager.STREAM_RING, 
			audioManager.getStreamMaxVolume(AudioManager.STREAM_RING), AudioManager.FLAG_SHOW_UI);
			break;
		}	
	}
}