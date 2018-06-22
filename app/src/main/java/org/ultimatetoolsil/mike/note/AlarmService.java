package org.ultimatetoolsil.mike.note;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.JobIntentService;
import android.util.Log;

import org.ultimatetoolsil.mike.note.models.NoteTitle;

import java.util.ArrayList;
import java.util.Calendar;

import static org.ultimatetoolsil.mike.note.addnote.RQS_1;

/**
 * Created by mike on 21 Jun 2018.
 */

public class AlarmService extends JobIntentService{
    Calendar calendar;
    @Override

    protected void onHandleWork(@NonNull Intent intent) {
        ArrayList<NoteTitle> titles= utils.getallsavednotes();
        for(int i=0;i<titles.size();i++){
            if(titles.get(i).getAlarm()!=null){




                //call notification activation Intent
                Intent a = new Intent(getBaseContext(), AlarmReciever.class);
                intent.putExtra("title",titles.get(i).getNotetitle());
                PendingIntent pendingIntent = PendingIntent.getBroadcast(getBaseContext(), RQS_1, a, 0);
                //pass note info to broadcast receiver


                AlarmManager alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
                alarmManager.set(AlarmManager.RTC_WAKEUP, titles.get(i).getAlarm().getTimeInMillis(), pendingIntent);
            }
        }
    }

    public static void enqueueWork(Context context, Intent intent) {
        enqueueWork(context,BackupJob.class,9,intent);
    }
}
