package org.ultimatetoolsil.mike.note;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by mike on 20 Jun 2018.
 */

class BackupReciever extends BroadcastReceiver{
    @Override
    public void onReceive(Context context, Intent intent) {
        BackupJob.enqueueWork(context,new Intent());
    }
}
