package org.ultimatetoolsil.mike.note;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentSender;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.preference.SwitchPreference;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.widget.Toast;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.drive.DriveApi;

import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.DriveResource;

import com.google.android.gms.drive.MetadataBuffer;
import com.google.android.gms.drive.MetadataChangeSet;
import com.google.android.gms.drive.query.Filters;
import com.google.android.gms.drive.query.Query;
import com.google.android.gms.drive.query.SearchableField;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.TaskCompletionSource;

import org.ultimatetoolsil.mike.note.models.NoteTitle;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import static android.content.ContentValues.TAG;


/**
 * Created by mike on 7 Aug 2017.
 */

public class settings extends PreferenceActivity implements  GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener{
   private InterstitialAd mInterstitialAd;
  boolean download=false;
    private static final String TAG = "BaseDriveActivity";

    /**
     * DriveId of an existing folder to be used as a parent folder in
     * folder operations samples.
     */
    public static final String EXISTING_FOLDER_ID = "0B2EEtIjPUdX6MERsWlYxN3J6RU0";

    /**
     * DriveId of an existing file to be used in file operation samples..
     */
    public static final String EXISTING_FILE_ID = "0ByfSjdPVs9MZTHBmMVdSeWxaNTg";

    /**
     * Extra for account name.
     */
    protected static final String EXTRA_ACCOUNT_NAME = "account_name";

    /**
     * Request code for auto Google Play Services error resolution.
     */
    protected static final int REQUEST_CODE_RESOLUTION = 1;

    /**
     * Next available request code.
     */
    protected static final int NEXT_AVAILABLE_REQUEST_CODE = 2;

    /**
     * Google API client.
     */
    private GoogleApiClient mGoogleApiClient;
   @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preference);
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-2883974575291426/2807388488");
        // mInterstitialAd.setAdUnitId("ca-app-pub-3940256099942544/1033173712");

        mInterstitialAd.loadAd(new AdRequest.Builder().build());



        if(Build.VERSION.SDK_INT>=21){
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimary));

        }

        ListPreference language = (ListPreference) getPreferenceManager().findPreference("lang");
        language.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                switch (newValue.toString()) {
                    case "he":
                        setLocale("he");
                        break;
                    case "en":
                        setLocale("en");
                        break;
                    case "fr":
                        setLocale("fr");
                        break;
                    case "de":
                        setLocale("de");
                        break;
                    case "jp":
                        setLocale("jp");
                        break;
                    case "tr":
                        setLocale("tr");
                        break;


                }


                return true;
            }
        });

       final Preference  restore=(Preference) getPreferenceManager().findPreference("restore");
       restore.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
           @Override
           public boolean onPreferenceClick(Preference preference) {
               restoreFilesFromDrive();
               return false;
           }
       });
        final SwitchPreference lock_pattern = (SwitchPreference) getPreferenceManager().findPreference("pattern");
        lock_pattern.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object o) {
                return true;
            }
        });
       final SwitchPreference backup=(SwitchPreference) getPreferenceManager().findPreference("bck");
       if(PreferenceManager.getDefaultSharedPreferences(settings.this).getBoolean("bck", false)){
           backup.setTitle("On");
       }
       backup.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
           @Override
           public boolean onPreferenceChange(Preference preference, Object o) {
               boolean checked= (Boolean) o;
               if (checked) {
                    backup.setTitle("On");
                   checkUserSignedIn();
                   setUpAutoBackup();

               }else
                   backup.setTitle("Off");
            return true;
           }

       });
       Preference patternchange =(Preference) findPreference("pt");
       patternchange.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
           @Override
           public boolean onPreferenceClick(Preference preference) {
               Intent toswitchpattern =new Intent(settings.this,PatternActivity.class);
               toswitchpattern.putExtra("toswitch","true");
               startActivity(toswitchpattern);
               return false;
           }
       });




   }

    private void setUpAutoBackup() {

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY,18);
        calendar.set(Calendar.MINUTE, 00);
        calendar.set(Calendar.SECOND, 00);
        Intent intent1 = new Intent(getBaseContext(), BackupReciever.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getBaseContext(), 0, intent1, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager am = (AlarmManager) getBaseContext().getSystemService(getBaseContext().ALARM_SERVICE);
        am.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
    }


    private void checkUserSignedIn() {
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(settings.this)
                    .addApi(Drive.API)
                    .addScope(Drive.SCOPE_FILE)
                    .addScope(Drive.SCOPE_APPFOLDER) // required for App Folder sample
                    .addConnectionCallbacks(settings.this)
                    .addOnConnectionFailedListener(settings.this)
                    .build();
        }
        mGoogleApiClient.connect();
    }




    @Override
    public void onBackPressed() {
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


    @Override
    protected void onResume() {
        super.onResume();

    }

    /**
     * Handles resolution callbacks.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_RESOLUTION && resultCode == RESULT_OK) {
            mGoogleApiClient.connect();
        }
    }

    /**
     * Called when activity gets invisible. Connection to Drive service needs to
     * be disconnected as soon as an activity is invisible.
     */
    @Override
    protected void onPause() {
        if (mGoogleApiClient != null) {
           mGoogleApiClient.disconnect();
        }
       super.onPause();
    }

    /**
     * Called when {@code mGoogleApiClient} is connected.
     */
    @Override
    public void onConnected(Bundle connectionHint) {
        Log.i(TAG, "GoogleApiClient connected");
        Drive.DriveApi.newDriveContents(getGoogleApiClient())
                .setResultCallback(driveContentsCallback);
    }

    /**
     * Called when {@code mGoogleApiClient} is disconnected.
     */
    @Override
    public void onConnectionSuspended(int cause) {
        Log.i(TAG, "GoogleApiClient connection suspended");
    }

    /**
     * Called when {@code mGoogleApiClient} is trying to connect but failed.
     * Handle {@code result.getResolution()} if there is a resolution is
     * available.
     */
    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Log.i(TAG, "GoogleApiClient connection failed: " + result.toString());
        if (!result.hasResolution()) {
            // show the localized error dialog.
            GoogleApiAvailability.getInstance().getErrorDialog(this, result.getErrorCode(), 0).show();
            return;
        }
        try {
            result.startResolutionForResult(this, REQUEST_CODE_RESOLUTION);
        } catch (IntentSender.SendIntentException e) {
            Log.e(TAG, "Exception while starting resolution activity", e);
        }
    }

    /**
     * Shows a toast message.
     */
    public void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    /**
     * Getter for the {@code GoogleApiClient}.
     */
    public GoogleApiClient getGoogleApiClient() {
        return mGoogleApiClient;
    }
    final private ResultCallback<DriveApi.DriveContentsResult> driveContentsCallback =
            new ResultCallback<DriveApi.DriveContentsResult>() {
                @Override
                public void onResult(DriveApi.DriveContentsResult result) {
                    if (!result.getStatus().isSuccess()) {
                        showMessage("Error while trying to create new file contents");
                        return;
                    }else {
                        if(download){
                            DriveId id=DriveId.decodeFromString( PreferenceManager.getDefaultSharedPreferences(settings.this).getString("fileid", null));
                            if(PreferenceManager.getDefaultSharedPreferences(settings.this).getString("fileid", null)!=null) {


                                Log.d("fileid", id.encodeToString());
                                DownloadFile(id, new File(Environment.getExternalStorageDirectory() + "/MyNotes/data.bin"));
                            }

                        }else
                        uploadFileToDrive(result);
                    }
                    download=false;

                }
            };


    // [END drive_contents_callback]

    final private ResultCallback<DriveFolder.DriveFileResult> fileCallback = new
            ResultCallback<DriveFolder.DriveFileResult>() {
                @Override
                public void onResult(DriveFolder.DriveFileResult result) {
                    if (!result.getStatus().isSuccess()) {
                        showMessage("Error while trying to create the file");
                        return;
                    }
                    showMessage("Backup successful"
                            + result.getDriveFile().getDriveId());
                    PreferenceManager.getDefaultSharedPreferences(settings.this).edit().putString("fileid", result.getDriveFile().getDriveId().encodeToString()).apply();
                    utils.deleteFile(Environment.getExternalStorageDirectory()+"/MyNotes/data.bin");
                }
            };

    public void setLocale(String lang) {
        Locale myLocale = new Locale(lang);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);
        Intent refresh = new Intent(this, settings.class);
        finish();
        startActivity(refresh);





    }
    private void uploadFileToDrive(DriveApi.DriveContentsResult result) {
    ArrayList<NoteTitle> titles=utils.getallsavednotes();
    if(titles!=null){
        final DriveContents driveContents = result.getDriveContents();

        // Perform I/O off the UI thread.
        new Thread() {
            @Override
            public void run() {


                utils.serializeListToFile(settings.this);
                String storage_path= Environment.getExternalStorageDirectory()+"/MyNotes/data.bin";


                Log.d("file",storage_path);


                FileInputStream inputStream = null;
                try {
                    inputStream = new FileInputStream(storage_path);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
                byte[] buffer = new byte[8 * 1024];

                BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(driveContents.getOutputStream());
                int n = 0;
                try {
                    while ((n = bufferedInputStream.read(buffer)) > 0) {
                        bufferedOutputStream.write(buffer, 0, n);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {

                    bufferedInputStream.close();
                    bufferedOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                final MimeTypeMap mime = MimeTypeMap.getSingleton();
                //String tmptype = mime.getMimeTypeFromExtension("vcf");
                MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
                        .setTitle("data.bin")
                        .setMimeType("text/plain")
                        .setStarred(true).build();

                Drive.DriveApi.getAppFolder(getGoogleApiClient())
                            .createFile(getGoogleApiClient(), changeSet, driveContents)
                            .setResultCallback(fileCallback);
            }
        }.start();
    }

    }
    private void restoreFilesFromDrive() {


       if(mGoogleApiClient==null||!mGoogleApiClient.isConnected()) {
           download = true;
           checkUserSignedIn();
       }else
           DownloadFile( DriveId.decodeFromString( PreferenceManager.getDefaultSharedPreferences(settings.this).getString("fileid", null)),new File(Environment.getExternalStorageDirectory()+"/MyNotes/data.bin"));

    }
    private void DownloadFile(final DriveId driveId, final File filename) {
        AsyncTask<Void, Void, Boolean> task = new AsyncTask<Void, Void, Boolean>() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                if (!filename.exists()) {
                    try {
                        filename.createNewFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            protected void onPostExecute(Boolean result) {
                super.onPostExecute(result);
                       Intent update = new Intent(settings.this,MainActivity.class);
       update.putExtra("restore",true);
       startActivity(update);
       finish();


            }

            @Override
            protected Boolean doInBackground(Void... params) {


                    DriveFile file = Drive.DriveApi.getFile(
                            mGoogleApiClient, driveId);
                    file.getMetadata(mGoogleApiClient)
                            .setResultCallback(metadataRetrievedCallback);
                    DriveApi.DriveContentsResult driveContentsResult = file.open(
                            mGoogleApiClient,
                            DriveFile.MODE_READ_ONLY, null).await();
                    DriveContents driveContents = driveContentsResult
                            .getDriveContents();
                    InputStream inputstream = driveContents.getInputStream();

                    try {
                        FileOutputStream fileOutput = new FileOutputStream(filename);

                        byte[] buffer = new byte[1024];
                        int bufferLength = 0;
                        while ((bufferLength = inputstream.read(buffer)) > 0) {
                            fileOutput.write(buffer, 0, bufferLength);
                        }
                        fileOutput.close();
                        inputstream.close();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
              //  }
                    return true;

                }

            }

            ;
        task.execute();
        }

    private ResultCallback<DriveResource.MetadataResult> metadataRetrievedCallback = new ResultCallback<DriveResource.MetadataResult>() {
        @Override
        public void onResult(DriveResource.MetadataResult result) {
            if (!result.getStatus().isSuccess()) {
                return;
            }
            //metadata = result.getMetadata();
        }
    };


}
