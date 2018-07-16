package org.ultimatetoolsil.mike.note;

import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.android.datetimepicker.date.DatePickerDialog;
import com.android.datetimepicker.time.RadialPickerLayout;
import com.android.datetimepicker.time.TimePickerDialog;
import com.anupcowkur.reservoir.Reservoir;
import com.anupcowkur.reservoir.ReservoirPutCallback;
import com.devlomi.record_view.OnRecordClickListener;
import com.devlomi.record_view.OnRecordListener;
import com.devlomi.record_view.RecordButton;
import com.devlomi.record_view.RecordView;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.mindorks.paracamera.Camera;
import com.nabinbhandari.android.permissions.PermissionHandler;
import com.nabinbhandari.android.permissions.Permissions;

import org.ultimatetoolsil.mike.note.models.NoteTitle;
import org.ultimatetoolsil.mike.note.models.SubItem;
import android.Manifest;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Random;


public class addnote extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener,OnClickListener {
    String TAG = "addnote";
    Context ctx;
    private EditText itemname;
    private NoteTitle loadednote;
    private String mLoadfilename;
    private EditText postnumber;
    private long mNoteCreationTime;
    private CheckBox rem,picture;
    private CardView mycard;
    private ImageButton date, play, image;
    private Animation dateltr, datertl, timertl, timeltr;
    private static final String TIME_PATTERN = "HH:mm";
    final static int RQS_1 = (int) ((new Date().getTime() / 1000L) % Integer.MAX_VALUE);
    boolean isPlaying = false;
    private String lblDate, lblTime;
    private InterstitialAd mInterstitialAd;
    private Calendar calendar;
    private DateFormat dateFormat;
    private SimpleDateFormat timeFormat;
    private static final String LOG_TAG = "AudioRecordTest";
    private static String mFileName = null;
    private Camera camera;
    private RecordButton mRecordButton = null;
    private MediaRecorder mRecorder = null;
    boolean save=false;
    private MediaPlayer mPlayer = null;

    @Override
    protected void onUserLeaveHint() {
        if(save) {
           save(false);
            super.onUserLeaveHint();

        }
        save=true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

            super.onActivityResult(requestCode, resultCode, data);
            if(requestCode == Camera.REQUEST_TAKE_PHOTO){
                Bitmap bitmap = camera.getCameraBitmap();
                if(bitmap != null) {
                    try {
                        savebitmap(bitmap,itemname.getText().toString());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    image.setImageBitmap(bitmap);
                    image.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            openImageInGallery(Uri.parse("file://"+Environment.getExternalStorageDirectory()+"/MyNotes/pictures/"+itemname.getText().toString()+".jpg"));
                        }
                    });
                }else{
                    Toast.makeText(this.getApplicationContext(),"Picture not taken!",Toast.LENGTH_SHORT).show();
                }
            }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addnote);
        image = (ImageButton) findViewById(R.id.imageButton2);
        //intializing variables
        this.mycard = (CardView) findViewById(R.id.input);
        //mycard.setMinimumHeight(250);
        this.itemname = (EditText) findViewById(R.id.name);
        this.postnumber = (EditText) findViewById(R.id.number);
        this.rem = (CheckBox) findViewById(R.id.reminder);
        picture=(CheckBox)findViewById(R.id.checkBox);
        this.date = (ImageButton) findViewById(R.id.datepicker);
        //this.time = (ImageButton) findViewById(R.id.timepicker);
        play = (ImageButton) findViewById(R.id.play);
        play.setVisibility(View.VISIBLE);
        RecordView recordView = (RecordView) findViewById(R.id.record_view);
        RecordButton recordButton = (RecordButton) findViewById(R.id.record_button);
        setupDir();
        //IMPORTANT
        recordButton.setRecordView(recordView);
        setuprDir();
        //setting onclick listener for date and time pickers
        if(Build.VERSION.SDK_INT>=23&&!hasPermissions(addnote.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO, Manifest.permission.CAMERA}))
            requestpermissions(addnote.this);



            recordView.setOnRecordListener(new OnRecordListener() {
                @Override
                public void onStart() {
                    //Start Recording..



                    startRecording();
                    play.setVisibility(View.INVISIBLE);
                    Log.d("RecordView", "onStart");
                    image.setVisibility(View.INVISIBLE);
                }

                @Override
                public void onCancel() {
                    //On Swipe To Cancel
                    play.setVisibility(View.VISIBLE);
                    Log.d("RecordView", "onCancel");
                    if (deleteRecording())
                        Toast.makeText(addnote.this, R.string.cancel, Toast.LENGTH_SHORT).show();
                    else
                        Toast.makeText(addnote.this, "Error", Toast.LENGTH_SHORT).show();
                    if(picture.isChecked())
                    image.setVisibility(View.VISIBLE);
                }

                @Override
                public void onFinish(long recordTime) {
                    //Stop Recording..
                    //  String time = getHumanTimeText(recordTime);
                    play.setVisibility(View.VISIBLE);
                    Log.d("RecordView", "onFinish");
                    if(picture.isChecked())
                    image.setVisibility(View.VISIBLE);

                    stopRecording();
                    setupPlayButton();
                    //   Log.d("RecordTime", time);
                }

                @Override
                public void onLessThanSecond() {
                    play.setVisibility(View.VISIBLE);
                    image.setVisibility(View.VISIBLE);
                    if (deleteRecording())
                        Toast.makeText(addnote.this, R.string.cancel, Toast.LENGTH_SHORT).show();
                    else
                        Toast.makeText(addnote.this, "Error", Toast.LENGTH_SHORT).show();
                    Log.d("RecordView", "onLessThanSecond");
                }
            });

        //if recording is present than enable play button
        //else
        //disable button


        date.setOnClickListener(this);



        //setting buttons for default hidden
        date.setVisibility(View.INVISIBLE);
        //load ads
        AdView mAdView = (AdView) findViewById(R.id.banner2);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-2883974575291426/2807388488");
        //mInterstitialAd.setAdUnitId("ca-app-pub-3940256099942544/1033173712");

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
        //check on activity created state of pic checkbox




        int i = getIntent().getIntExtra("index", -1);
        if (i != -1) {
            loadednote = utils.getNotebyfilename(getApplicationContext(), i);
            Log.d("this is working", "damn");
            if (this.loadednote != null) {
                this.itemname.setText(this.loadednote.getNotetitle());
                this.postnumber.setText(loadednote.getItems().get(0).getContent());
            }
        }
        String fromnotif=getIntent().getStringExtra("notification");
        if(fromnotif!=null){
            try {
                Reservoir.init(addnote.this,2048);
            } catch (IOException e) {
                e.printStackTrace();
            }
            ArrayList<NoteTitle> titles= utils.getallsavednotes();
            for(int a=0;a<titles.size();a++){
                if(titles.get(a).getNotetitle().equals(fromnotif));
                loadednote = utils.getNotebyfilename(getApplicationContext(), a);
                Log.d("this is working", "damn");
                if (this.loadednote != null) {
                    this.itemname.setText(this.loadednote.getNotetitle());
                    this.postnumber.setText(loadednote.getItems().get(0).getContent());
                }
            }
        }
        //button click for saving the note


        calendar = Calendar.getInstance();
        dateFormat = DateFormat.getDateInstance(DateFormat.LONG, Locale.getDefault());
        timeFormat = new SimpleDateFormat(TIME_PATTERN, Locale.getDefault());

        //lblDate = (TextView) findViewById(R.id.lblDate);
        // lblTime = (TextView) findViewById(R.id.lblTime);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar1);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.material_gray));
        update();
        if (Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimary));
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());

        }
    //==== use direct title implementations from here only=========================================
        if (recorded(itemname.getText().toString())) {
            play.setEnabled(true);
            setupPlayButton();

        } else {
            play.setEnabled(false);
            Log.d("disabled","play");
        }


        if(loadednote!=null) {
            picture.setChecked(loadednote.isImageEnabled());
            if(picture.isChecked())
            image.setVisibility(View.VISIBLE);
            final Uri src = Uri.parse("file://" + Environment.getExternalStorageDirectory() + "/MyNotes/pictures/" + itemname.getText().toString() + ".jpg");
            File f = new File(Environment.getExternalStorageDirectory() + "/MyNotes/pictures/" + itemname.getText().toString() + ".jpg");
            if (f.exists()) {
                image.setImageURI(src);
                image.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        openImageInGallery(src);
                    }
                });

            }
        }
        picture.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(isChecked){
                    //save state to object
                    //show image icon
                    //setup pic onclick event

                    if(imageTaken()) {
                        final Uri source= Uri.parse("file://"+Environment.getExternalStorageDirectory()+"/MyNotes/pictures/"+itemname.getText().toString()+".jpg");

                        image.setImageURI(source);
                        image.setVisibility(View.VISIBLE);
                        image.setOnClickListener(new OnClickListener() {
                            @Override
                            public void onClick(View view) {
                               openImageInGallery(source);
                                //open image

                            }
                        });
                    }else {
                      //take pic and show in icon
                        image.setVisibility(View.VISIBLE);
                        image.setOnClickListener(new OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                takePicture();
                            }
                        });

                    }

                }else {
                    //disable the icon and delete image
                    image.setImageResource(R.drawable.add_image);
                    image.setVisibility(View.INVISIBLE);
                    deleteImage(Environment.getExternalStorageDirectory()+"/MyNotes/pictures/"+itemname.getText().toString()+".jpg");
                }
            }
        });
    }

    private boolean deleteImage(String imgname) {
       File f= new File(imgname);
        return f.delete();
    }

    private void takePicture() {
        save=false;
        camera = new Camera.Builder()
                .resetToCorrectOrientation(true)// it will rotate the camera bitmap to the correct orientation from meta data
                .setTakePhotoRequestCode(1)
                .setDirectory("MyNotes/pictures")
                .setName(itemname.getText().toString())
                .setImageFormat(Camera.IMAGE_JPEG)
                .setCompression(75)
                .setImageHeight(1000)// it will try to achieve this height as close as possible maintaining the aspect ratio;
                .build(this);
        try {
            camera.takePicture();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void openImageInGallery(Uri source) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setDataAndType(source, "image/*");
        startActivity(intent);

    }

    private boolean imageTaken() {
        Uri source= Uri.parse("file://"+Environment.getExternalStorageDirectory()+"/MyNotes/pictures/"+itemname.getText().toString()+".jpg");
        File img = new File(source.getPath());

        return img.exists();
    }

    private void setupPlayButton() {
        play.setEnabled(true);
        play.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isPlaying) {
                    mFileName = Environment.getExternalStorageDirectory()+"/MyNotes/recordings/"+itemname.getText().toString()+".3gp";
                    startPlaying(mFileName);
                    play.setImageResource(R.drawable.pause);
                    isPlaying = true;
                } else {
                    stopPlaying();
                    isPlaying = false;
                    play.setImageResource(R.drawable.play);
                }
            }
        });
    }



        public static void requestpermissions(Activity activity) {


                // Should we show an explanation?
                if (ActivityCompat.shouldShowRequestPermissionRationale(activity,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    Toast.makeText(activity, "Write permission is needed for camera and recorder ", Toast.LENGTH_SHORT).show();
                    // Show an explanation to the user *asynchronously* -- don't
                    // block this thread waiting for the user's response! After the
                    // user sees the explanation, try again to request the
                    // permission.
                    ActivityCompat.requestPermissions(activity,  new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO, Manifest.permission.CAMERA },
                            5);

                    Toast.makeText(activity, "REQUEST  PERMISSIONS", Toast.LENGTH_LONG).show();

                } else {
                    // No explanation needed, we can request the permission.
                    ActivityCompat.requestPermissions(activity,  new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO, Manifest.permission.CAMERA},
                           6);


            }
        }


    private boolean deleteRecording() {
    File file= new File(Environment.getExternalStorageDirectory()+"/MyNotes/recordings/"+itemname.getText().toString()+".3gp");
    if(file.exists()) {
        file.delete();
    return true;
    }
    return false;
    }


    //method for saving the note

    private void save(boolean showAd) {

        if (loadednote != null) {
            mNoteCreationTime = loadednote.getMdatetime();
        } else {
            mNoteCreationTime = System.currentTimeMillis();
        }


        //if (utils.save(this, new Item(this.itemname.getText().toString(), this.postnumber.getText().toString(), mNoteCreationTime))) {
         List<SubItem> content= new ArrayList<>();
        content.add(new SubItem(postnumber.getText().toString(),null));
        NoteTitle title= new NoteTitle(itemname.getText().toString(),content,mNoteCreationTime,picture.isChecked());
        title.setAlarm(calendar);
        int i  = getIntent().getIntExtra("index",-1);
        if(i!=-1){
            if(utils.saveoldNote(title,i,getApplicationContext())){
                Toast.makeText(this, getResources().getString(R.string.file_save), Toast.LENGTH_SHORT).show();
                if(showAd)
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

            }
        }
        else
        if(utils.savenote(title,this)){



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
        for(int i = 0; i < menu.size(); i++){
            Drawable drawable = menu.getItem(i).getIcon();
            if(drawable != null) {
                drawable.mutate();
                drawable.setColorFilter(getResources().getColor(R.color.material_gray), PorterDuff.Mode.SRC_ATOP);
            }
        }

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
                        addnote.this.save(true);
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
                itemname.getText().toString();
              postnumber.getText().toString();

               save(true);
               save=false;

                break;


            case R.id.action_cancel: //cancel the note
                finish();
                break;
        } return super.onOptionsItemSelected(item);


    }


//    private void onRecord(boolean start) {
//        if (start) {
//            startRecording();
//        } else {
//            stopRecording();
//        }
//    }

//    private void onPlay(boolean start) {
//        if (start) {
//            startPlaying();
//        } else {
//            stopPlaying();
//        }
//    }

    private void startPlaying(String file) {

        mPlayer = new MediaPlayer();
        try {
            mPlayer.setDataSource(file);
            mPlayer.prepare();
            mPlayer.start();
            mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    play.setImageResource(R.drawable.play);
                    isPlaying=false;
                }
            });
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
        }
    }

    private void stopPlaying() {
        try {
            mPlayer.release();
            mPlayer = null;

        }catch (Exception e){
            e.printStackTrace();


        }

    }

    private void startRecording() {
        try {

        if(loadednote==null)
            mFileName=Environment.getExternalStorageDirectory()+"/MyNotes/recordings/"+itemname.getText().toString()+".3gp";
        else
            mFileName=Environment.getExternalStorageDirectory()+"/MyNotes/recordings/"+loadednote.getNotetitle()+".3gp";

        if (itemname.getText().toString().isEmpty()) {
            Toast.makeText(addnote.this, R.string.tit, Toast.LENGTH_SHORT).show();
            return;
        }
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mRecorder.setOutputFile(mFileName);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);


            mRecorder.prepare();
            mRecorder.start();
        } catch (Exception e) {
            Log.e(LOG_TAG, "prepare() failed");
            Toast.makeText(addnote.this,R.string.perm,Toast.LENGTH_LONG).show();

        }


    }

    private void setuprDir() {
        try {


            File dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath().toString() + "/MyNotes/recordings");
            if (!dir.exists()) {
                dir.mkdirs();
                Log.d("created", dir.toString());
            }

        } catch (Exception e) {
            Toast.makeText(addnote.this, "Error. please try later", Toast.LENGTH_SHORT).show();
        }
    }
    private void stopRecording() {
      try {
          mRecorder.stop();
          mRecorder.release();
          mRecorder = null;
      }catch (Exception e){
          e.printStackTrace();
      }


    }

    public boolean recorded(String filename){
        File f=new File(Environment.getExternalStorageDirectory()+"/MyNotes/recordings/"+filename+".3gp");
       Log.d("playing",Environment.getExternalStorageDirectory()+"/MyNotes/recordings/"+filename+".3gp");
       if(f.exists())
           return true;

       return false;
    }
 public void setupDir(){
        File dir =new File( Environment.getExternalStorageDirectory()+"/MyNotes/pictures");
         if(!dir.exists())
             dir.mkdir();
 }
    public static File savebitmap(Bitmap bmp,String filename) throws Exception {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 60, bytes);
        File f = new File(Environment.getExternalStorageDirectory()+"/MyNotes/pictures/"+filename+
             ".jpg");
        f.createNewFile();
        FileOutputStream fo = new FileOutputStream(f);
        fo.write(bytes.toByteArray());
        fo.close();
        return f;

    }
    public static boolean hasPermissions(Context context, String... permissions) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }
}