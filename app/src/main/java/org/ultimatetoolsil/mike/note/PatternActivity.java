package org.ultimatetoolsil.mike.note;

import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.Toast;

import com.andrognito.patternlockview.PatternLockView;
import com.andrognito.patternlockview.listener.PatternLockViewListener;
import com.andrognito.patternlockview.utils.PatternLockUtils;
import com.dd.CircularProgressButton;

import java.util.List;

import io.paperdb.Paper;

import static android.R.attr.button;


public class PatternActivity extends AppCompatActivity {
String save_pattern_key = "pattern_code";
    PatternLockView mPatternLockView;
    String final_pattern = "";
    String pattern_correct = "no";

    private static int TIME_OUT = 2000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Paper.init(this);


        if(getIntent().getStringExtra("toswitch")!= null&&getIntent().getStringExtra("toswitch").equals("true")) {
            Log.d("should","input new password");
            inputPattern();

           inputPattern();

       }else {

//move to main pattern lock screen
        final String save_pattern = Paper.book().read(save_pattern_key);

        //check if db has already signed pattrn and if yes load main input pattern screen

        if (save_pattern != null && !save_pattern.equals("null")) {
            setContentView(R.layout.activity_pattern);
            mPatternLockView = (PatternLockView) findViewById(R.id.pattern_lock_view);
            mPatternLockView.addPatternLockListener(new PatternLockViewListener() {
                @Override
                public void onStarted() {

                }

                @Override
                public void onProgress(List<PatternLockView.Dot> progressPattern) {

                }

                //if the password is correct redirect to mainactivity
                @Override
                public void onComplete(List<PatternLockView.Dot> pattern) {
                    final_pattern = PatternLockUtils.patternToString(mPatternLockView, pattern);
                    if (final_pattern.equals(save_pattern)) {
                        Toast.makeText(getBaseContext(), R.string.password_correct, Toast.LENGTH_SHORT).show();
                        pattern_correct = "yes";
                        Log.d("pattern activity string", pattern_correct);

                        Intent main_activity = new Intent(PatternActivity.this, MainActivity.class);

                        main_activity.putExtra("tag", pattern_correct);

                        startActivity(main_activity);
                     finish();
                    }

                }

                @Override
                public void onCleared() {

                }
            });

        } else
            inputPattern();


    }}



    private void inputPattern() {



              setContentView(R.layout.pattern_screeen);
              mPatternLockView =(PatternLockView )findViewById(R.id.pattern_lock_set) ;
              mPatternLockView.addPatternLockListener(new PatternLockViewListener() {
                  @Override
                  public void onStarted() {

                  }

                  @Override
                  public void onProgress(List<PatternLockView.Dot> progressPattern) {

                  }

                  @Override
                  public void onComplete(List<PatternLockView.Dot> pattern) {
                      final_pattern =PatternLockUtils.patternToString(mPatternLockView,pattern);
                  }

                  @Override
                  public void onCleared() {

                  }
              });

              final CircularProgressButton btnsetup = (CircularProgressButton) findViewById(R.id.setupbtn);

              btnsetup.setOnClickListener(new View.OnClickListener() {
                  @Override
                  public void onClick(View view) {
                      if (btnsetup.getProgress() == 0) {
                          simulateSuccessProgress(btnsetup);
                      } else {
                          btnsetup.setProgress(0);
                      }

                      Paper.book().write(save_pattern_key,final_pattern);
                      new Handler().postDelayed(new Runnable() {
                          @Override
                          public void run() {
                              Intent i = new Intent(PatternActivity.this, MainActivity.class);
                              startActivity(i);
                              finish();
                          }
                      }, TIME_OUT);

                  }
              });
          }











    private void simulateSuccessProgress(final CircularProgressButton btnsetup) {
        ValueAnimator widthAnimation = ValueAnimator.ofInt(1, 100);
        widthAnimation.setDuration(1500);
        widthAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
        widthAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                Integer value = (Integer) animation.getAnimatedValue();
                btnsetup.setProgress(value);
            }
        });
        widthAnimation.start();





    }


}

