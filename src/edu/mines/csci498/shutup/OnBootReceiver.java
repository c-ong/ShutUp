/**
 * This class receives an ON_BOOT_COMPLETED event and delegates AlarmHelper to set all proper alarms
 * @author Lauren Aberle
 * @author Thomas Brown
 */
package edu.mines.csci498.shutup;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;


public class OnBootReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		AlarmHelper.setAllAlarms(context);
	}
}
