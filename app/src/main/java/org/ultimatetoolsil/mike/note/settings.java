package org.ultimatetoolsil.mike.note;

import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;
import android.preference.SwitchPreference;
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

import java.util.Locale;


/**
 * Created by mike on 7 Aug 2017.
 */

public class settings extends PreferenceActivity {
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

        final SwitchPreference lock_pattern = (SwitchPreference) getPreferenceManager().findPreference("pattern");
        lock_pattern.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object o) {


                return  true;
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
