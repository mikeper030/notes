package org.ultimatetoolsil.mike.note;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.android.datetimepicker.date.DatePickerDialog;
import com.android.datetimepicker.time.RadialPickerLayout;
import com.android.datetimepicker.time.TimePickerDialog;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Random;

public class addnote extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener,OnClickListener {
    String TAG = "addnote";
    Context ctx;
    private EditText itemname;
    private Item loadednote;
    private String mLoadfilename;
    private EditText postnumber;
    private long mNoteCreationTime;
    private CheckBox rem;
    private CardView mycard;
    private ImageButton date;
    private Animation dateltr, datertl, timertl, timeltr;
    private static final String TIME_PATTERN = "HH:mm";
    final static int RQS_1 = (int) ((new Date().getTime()/1000L) % Integer.MAX_VALUE);

    private String lblDate, lblTime;
    private InterstitialAd mInterstitialAd;
    private Calendar calendar;
    private DateFormat dateFormat;
    private SimpleDateFormat timeFormat;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addnote);

        //intializing variables
        this.mycard = (CardView) findViewById(R.id.input);
        //mycard.setMinimumHeight(250);
        this.itemname = (EditText) findViewById(R.id.name);
        this.postnumber = (EditText) findViewById(R.id.number);
        this.rem = (CheckBox) findViewById(R.id.reminder);

        this.date = (ImageButton) findViewById(R.id.datepicker);
        //this.time = (ImageButton) findViewById(R.id.timepicker);

        //setting onclick listener for date and time pickers

        date.setOnClickListener(this);



        //setting buttons for default hidden
        date.setVisibility(View.INVISIBLE);
       //load ads
        AdView mAdView = (AdView) findViewById(R.id.banner2);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-2883974575291426/2807388488");
        // mInterstitialAd.setAdUnitId("ca-app-pub-3940256099942544/1033173712");

        mInterstitialAd.loadAd(new AdRequest.Builder().build());

        //loading and setting animation listener for LTR effect
        dateltr = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.date_ltr);
        timertl = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.time_rtl);
        timeltr = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.time_ltr);


        dateltr.setAnimationListener(new Animation.AnimationListener() {


            @Override
            public void onAnimationStart(Animation animation) {
              //  time.startAnimation(timertl);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                date.setVisibility(View.VISIBLE);
            //    time.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });


        //loading and setting animation listener for RTL effect
        datertl = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.date_rtl);
        datertl.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                //time.startAnimation(timeltr);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                date.setVisibility(View.INVISIBLE);
              //  time.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });


        //handle checkbox actions on checked and unchecked
        rem.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (rem.isChecked()) {

                    date.startAnimation(dateltr);


                } else
                    date.startAnimation(datertl);


            }
        });



        this.mLoadfilename = getIntent().getStringExtra("item file");
        if (this.mLoadfilename != null) {
            this.loadednote = utils.getitembyfilename(getApplicationContext(), this.mLoadfilename);
            Log.d("this is working", "damn");
            if (this.loadednote != null) {
                this.itemname.setText(this.loadednote.getItem_name());
                this.postnumber.setText(this.loadednote.getPost_number());
            }
        }

        //button click for saving the note


        calendar = Calendar.getInstance();
        dateFormat = DateFormat.getDateInstance(DateFormat.LONG, Locale.getDefault());
        timeFormat = new SimpleDateFormat(TIME_PATTERN, Locale.getDefault());

        //lblDate = (TextView) findViewById(R.id.lblDate);
        // lblTime = (TextView) findViewById(R.id.lblTime);

        update();
        if (Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimary));
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());

        }


    }


    //method for saving the note

    private void save() {

        if (loadednote != null) {
            mNoteCreationTime = loadednote.getMdatetime();
        } else {
            mNoteCreationTime = System.currentTimeMillis();
        }


        if (utils.save(this, new Item(this.itemname.getText().toString(), this.postnumber.getText().toString(), mNoteCreationTime))) {

            Toast.makeText(this, getResources().getString(R.string.file_save), Toast.LENGTH_SHORT).show();
            if (mInterstitialAd.isLoaded()) {
                mInterstitialAd.show();
                mInterstitialAd.setAdListener(new AdListener() {
                    @Override
                    public void onAdClosed() {
                        super.onAdClosed();
                        finish();


                    }
                });

            } else {
                Log.d("TAG", "The interstitial wasn't loaded yet.");
            }

            this.finish();


        }else {




        Toast.makeText(this, getResources().getString(R.string.error), Toast.LENGTH_SHORT).show();

    }}

    //update the date and time from date time picker
    private void update() {
         lblDate = (dateFormat.format(calendar.getTime()));
          lblTime = (timeFormat.format(calendar.getTime()));
    Log.d(lblDate,lblTime);
    }

    @Override
    public void onDateSet(DatePickerDialog dialog, int year, int monthOfYear, int dayOfMonth) {
        calendar.set(year, monthOfYear, dayOfMonth);
        //update();
        TimePickerDialog.newInstance(this, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show(getFragmentManager(), "timePicker");

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.new_note_activity, menu);

        return true;
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder adb =new AlertDialog.Builder(this)
                .setTitle(R.string.cancel)
                .setMessage(R.string.save_before_exit)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                      //save the note
                        if (addnote.this.postnumber.getText().toString().isEmpty() && addnote.this.itemname.getText().toString().isEmpty()) {
                            addnote.this.finish();
                            return;
                        }
                        addnote.this.itemname.getText().toString();
                        addnote.this.postnumber.getText().toString();
                        addnote.this.save();
                    }
                })
                .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    finish();

                    }
                });adb.show();

    }

    @Override
    public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute) {
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
        calendar.set(Calendar.MINUTE, minute);
        update();
    //call notification activation Intent
        Intent intent = new Intent(getBaseContext(), AlarmReciever.class);
        intent.putExtra("title",itemname.getText().toString());
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getBaseContext(), RQS_1, intent, 0);
      //pass note info to broadcast receiver


        Log.d("notification title",itemname.getText().toString());
        AlarmManager alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);



    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.datepicker:
                DatePickerDialog.newInstance(this, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show(getFragmentManager(), "datePicker");
                break;

        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.action_save_note: //save the note
                //or update :P
                if (addnote.this.postnumber.getText().toString().isEmpty() && addnote.this.itemname.getText().toString().isEmpty()||addnote.this.itemname.getText().toString().isEmpty()) {
                    Toast.makeText(getBaseContext(),R.string.please_add,Toast.LENGTH_SHORT).show();
                    // addnote.this.finish();
                 return true;
                }
                addnote.this.itemname.getText().toString();
                addnote.this.postnumber.getText().toString();
                addnote.this.save();


                break;


            case R.id.action_cancel: //cancel the note
                finish();
                break;
        } return super.onOptionsItemSelected(item);


    }
}