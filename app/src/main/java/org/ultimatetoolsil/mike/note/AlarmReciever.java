package org.ultimatetoolsil.mike.note;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import java.util.Date;

/**
 * Created by mike on 4 Aug 2017.
 */

public class AlarmReciever extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
       // Toast.makeText(context,"alarm is activated",Toast.LENGTH_SHORT).show();

        String title = intent.getStringExtra("title");
        //creating unique id for each specific notification
        int ID = (int) ((new Date().getTime()/1000L) % Integer.MAX_VALUE);
        Log.d("random",String.valueOf(ID));
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Intent notification = new Intent(context,MainActivity.class);
        notification.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pi = PendingIntent.getActivity(context,ID,notification,PendingIntent.FLAG_UPDATE_CURRENT);

        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder mynotification = new NotificationCompat.Builder(context)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText("you have a reminder about a note")
                .setAutoCancel(true)
                .setContentIntent(pi)
                .setSound(defaultSoundUri)
                .setVibrate(new long[]{1000,1000,1000,1000,1000});

        notificationManager.notify(ID,mynotification.build());
        Log.d("random",String.valueOf(ID));

    }
}
