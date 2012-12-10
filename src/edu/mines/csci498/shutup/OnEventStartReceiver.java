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
			String volumeString = (String) intent.getStringExtra(OnBootReceiver.EXTRA_VOLUME_STRING);
			changeRingVolume(context, volumeString);
		}
		else {
			Log.e("OnEventStartReciever", "No extra in intent!");
		}
			
	}

	private void changeRingVolume(Context context, String volumeString) {
		
		AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
		
		RingVolume volume = RingVolume.values()[Integer.parseInt(volumeString) - 1];
		
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