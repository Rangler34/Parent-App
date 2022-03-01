package ca.cmpt276.parentApp.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * NotificationSnooze class - display timer notification
 */
public class NotificationSnooze extends BroadcastReceiver {

    private final String ACTION_NAME = "Stop";
    private final String EXTRA_NAME = "Snooze";
    @Override
    public void onReceive(Context context, Intent intent) {
        //https://www.youtube.com/watch?v=rRoHBWKQoRE
        context.sendBroadcast(new Intent(ACTION_NAME).putExtra(EXTRA_NAME,intent.getAction()));
        
    }
}
