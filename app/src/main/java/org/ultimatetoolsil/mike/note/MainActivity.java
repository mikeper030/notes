package org.ultimatetoolsil.mike.note;
//created by Mike peretz
//all rights reserved
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.preference.CheckBoxPreference;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.AppLaunchChecker;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;

import java.util.ArrayList;


import static org.ultimatetoolsil.mike.note.R.layout.item;

public class MainActivity extends AppCompatActivity {
    RecyclerView rec;
   Item mLoadeditem;
     private  String password_correct = null;
private InterstitialAd mInterstitialAd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


//app id ca-app-pub-2883974575291426~8162645182
//banner ad id ca-app-pub-2883974575291426/6274848447
// interstitial id ca-app-pub-2883974575291426/2807388488

        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-2883974575291426/4870827395");
        // mInterstitialAd.setAdUnitId("ca-app-pub-3940256099942544/1033173712");

        mInterstitialAd.loadAd(new AdRequest.Builder().build());

        final SharedPreferences sharedPreferences= PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        PreferenceManager.setDefaultValues(this, R.xml.preference, false);


        //when redirected from patternlock screen check if the password was inputed correctly
        //if yes load the app

        //if locking screen is enabled redirect to locking screen
       if(getIntent().getStringExtra("tag")!=null){

         password_correct = getIntent().getStringExtra("tag");}


          //  Log.d ("shouldopen mainactivity",password_correct);
        if(sharedPreferences.getBoolean("pattern",true)&& password_correct == null||sharedPreferences.getBoolean("pattern",true)&& password_correct=="no")
             //   sharedPreferences.getBoolean("pattern",true) && password_correct.isEmpty())
        {

           // Log.d (,password_correct);
           Intent lockingscreen = new Intent(MainActivity.this,PatternActivity.class);
           startActivity(lockingscreen);

       }


        setContentView(R.layout.activity_main);
        //Log.d ("shouldopen mainactivity",String.valueOf(getIntent().getBooleanExtra("tag",false)));
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        MobileAds.initialize(this, "ca-app-pub-2883974575291426~8103495391");

        AdView mAdView = (AdView) findViewById(R.id.banner);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        rec = (RecyclerView) findViewById (R.id.recycler_view);
        rec.setHasFixedSize(true);
        LinearLayoutManager lim = new LinearLayoutManager(this);
        lim.setOrientation(LinearLayoutManager.VERTICAL);
        rec.setLayoutManager(lim);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              startActivity(new Intent(MainActivity.this,addnote.class));
              overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);

            }
        });
        if(Build.VERSION.SDK_INT>=21){
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimary));
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());

        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();

        ImageView default1 = (ImageView) findViewById(R.id.imageV);
        TextView main = (TextView) findViewById(R.id.main);
        final ArrayList<Item> items = utils.getallsaveditems(this);
        if(items == null || items.size() == 0 ){
            //Toast.makeText(this,"You have no items saved",Toast.LENGTH_SHORT).show();
              main.setVisibility(View.VISIBLE);
              default1.setVisibility(View.VISIBLE);
        return;
        }else {
        main.setVisibility(View.INVISIBLE);
       default1.setVisibility(View.INVISIBLE);
         final Itemadapter ia = new Itemadapter(this, item,items);
        rec.setAdapter(ia);

            //handle left swipe events
            ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
                @Override
                public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                    return false;
                }

                @Override
                public void onSwiped(final RecyclerView.ViewHolder viewHolder, int direction) {

                    final int position = viewHolder.getAdapterPosition(); //get position which is swipe

                    if (direction == ItemTouchHelper.RIGHT) {    //if swipe left

                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this); //alert for confirm to delete
                        builder.setMessage(R.string.question_delete);    //set message

                        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() { //when click on DELETE
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                             try {


                                 String filename = ((TextView) viewHolder.itemView.findViewById(R.id.itemdetailsdisplay)).getText().toString() + ".bin";

                                 mLoadeditem = utils.getitembyfilename(getBaseContext(), filename);


                                 utils.delete(getBaseContext(), mLoadeditem.getItem_name() + ".bin");
                                 Log.d("test", filename);

                                items.remove(viewHolder.getAdapterPosition());

                                    ia.notifyDataSetChanged();
                                    ia.notifyItemRemoved(viewHolder.getAdapterPosition());
                                 rec.invalidate();
                                 Toast.makeText(getBaseContext(), R.string.item_deleted, Toast.LENGTH_SHORT).show();
                                 return;
                             }catch (Exception e){
                                 e.printStackTrace();

                                }
                             }
                        }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {  //not removing items if cancel is done
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                               rec.setAdapter(ia);
                                return;
                            }
                        }).show();  //show alert dialog
                    }
                }
            };
            ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
            itemTouchHelper.attachToRecyclerView(rec); //set swipe right to recylcer
                   }

    ItemTouchHelper.SimpleCallback simpleCallback1 = new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.LEFT) {
        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder viewHolder1) {
            return false;
        }

        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int i) {
       //share cardview content
        StringBuilder sb = new StringBuilder();
            String title = ((TextView) viewHolder.itemView.findViewById(R.id.itemdetailsdisplay)).getText().toString();
            String content = ((TextView) viewHolder.itemView.findViewById(R.id.itemnuberdisplay)).getText().toString();
            String date = ((TextView)viewHolder.itemView.findViewById(R.id.datedisplay)).getText().toString();
            sb.append(title);
            sb.append(System.getProperty("line.separator"));
            sb.append(content);
            sb.append(System.getProperty("line.separator"));
            sb.append(R.string.created+" "+date);
            final String itemcontent = sb.toString();
            Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
            sharingIntent.setType("text/plain");
            sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, R.string.check);
            sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, itemcontent);
            startActivity(Intent.createChooser(sharingIntent,getResources().getString(R.string.share_using)));


        }
    };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback1);
        itemTouchHelper.attachToRecyclerView(rec); //set swipe left to recylcer

    }

    boolean doubleBackToExitPressedOnce = false;

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            Intent homeIntent = new Intent(Intent.ACTION_MAIN);
            homeIntent.addCategory( Intent.CATEGORY_HOME );
            homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(homeIntent);
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, R.string.exit, Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
         Intent in = new Intent(MainActivity.this,settings.class);
            startActivity(in);

         

            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
