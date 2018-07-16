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
import static org.ultimatetoolsil.mike.note.MainFragment.auth;


/**
 * Created by mike on 7 Aug 2017.
 */

public class settings extends PreferenceActivity{
   private InterstitialAd mInterstitialAd;

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

//       final Preference  restore=(Preference) getPreferenceManager().findPreference("restore");
//       restore.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
//           @Override
//           public boolean onPreferenceClick(Preference preference) {
//               restoreFilesFromDrive();
//               return false;
//           }
//       });
        final  Preference signin=(Preference)getPreferenceManager().findPreference("acc");
       if(auth!=null) {
           try {
               String email = auth.getCurrentUser().getEmail();
               if (email != null) {
                   signin.setTitle(email);

               } else
                   signin.setTitle(R.string.signed);
           } catch (Exception e) {
               signin.setTitle(R.string.btn_login);
           }

       }
        signin.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent i = new Intent(settings.this,Signups.class);
                startActivity(i);
                finish();
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
        final Preference sigout=(Preference)getPreferenceManager().findPreference("signo");
        sigout.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                auth.signOut();
                signin.setTitle(R.string.btn_login);
                return false;
            }
        });
       final Preference settings=(Preference)getPreferenceManager().findPreference("sett");
       settings.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
           @Override
           public boolean onPreferenceClick(Preference preference) {
               startActivity(new Intent(settings.this,MyAccount.class));
               return false;
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
}
