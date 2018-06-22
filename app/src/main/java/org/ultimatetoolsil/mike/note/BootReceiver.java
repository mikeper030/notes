package org.ultimatetoolsil.mike.note;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by mike on 21 Jun 2018.
 */

public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        AlarmService.enqueueWork(context,new Intent());
    }
}
