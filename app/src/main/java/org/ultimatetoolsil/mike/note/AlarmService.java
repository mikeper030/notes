package org.ultimatetoolsil.mike.note;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.JobIntentService;
import android.util.Log;

import com.anupcowkur.reservoir.Reservoir;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.ultimatetoolsil.mike.note.models.NoteTitle;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;

import static org.ultimatetoolsil.mike.note.addnote.RQS_1;

/**
 * Created by mike on 21 Jun 2018.
 */

public class AlarmService extends JobIntentService{

    private FirebaseDatabase mFirebaseDatabase;
    private String userID;
    private DatabaseReference myRef;
    private FirebaseAuth auth;
    @Override

    protected void onHandleWork(@NonNull Intent intent) {

        auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() == null) {
            Log.d("firebase","notifications failed");
            try {
                Reservoir.init(getBaseContext(),200000);
            } catch (IOException e) {
                e.printStackTrace();
            }
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
            //startActivity(new Intent(getActivity(), Signups.class));
            //getActivity().finish();
        } else {
            mFirebaseDatabase = FirebaseDatabase.getInstance();
            myRef = mFirebaseDatabase.getReference();
            auth = FirebaseAuth.getInstance();
            FirebaseUser user2 = auth.getCurrentUser();
            userID = user2.getUid();
            myRef.child("users").child(userID).child("notes").addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    String title= dataSnapshot.child("title").getValue(String.class);
                    long millis=0000;
                    try {
                        if(dataSnapshot.child("notification").getValue(Long.class)!=null){
                            millis= dataSnapshot.child("notification").getValue(Long.class);
                        }

                    }catch (Exception r){
                        r.printStackTrace();
                    }

                    if(millis!=0000){
                       //if the notification passed do not schedule new one
                        if(System.currentTimeMillis()<millis) {
                            Intent intent = new Intent(getBaseContext(), AlarmReciever.class);
                            intent.putExtra("title", title);
                            PendingIntent pendingIntent = PendingIntent.getBroadcast(getBaseContext(), RQS_1, intent, 0);
                            //pass note info to broadcast receiver

                            // Log.d("notification title",itemname.getText().toString());
                            AlarmManager alarmManager = (AlarmManager) getBaseContext().getSystemService(Context.ALARM_SERVICE);
                            alarmManager.set(AlarmManager.RTC_WAKEUP, millis, pendingIntent);
                        }
                    }
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {

                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }

    }

    public static void enqueueWork(Context context, Intent intent) {
        enqueueWork(context,AlarmService.class,9,intent);
    }
}
