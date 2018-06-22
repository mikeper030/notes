package org.ultimatetoolsil.mike.note;
//created by Mike peretz
//all rights reserved
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.provider.BaseColumns;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.CursorAdapter;
import android.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.anupcowkur.reservoir.Reservoir;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;


import org.ultimatetoolsil.mike.note.adapters.RecyclerAdapter;
import org.ultimatetoolsil.mike.note.models.ColoredCursorAdapter;
import org.ultimatetoolsil.mike.note.models.NoteTitle;


import java.util.ArrayList;
import java.util.Collections;

public class MainActivity extends AppCompatActivity  {
    RecyclerView rec;
     Item mLoadeditem;
     private  String password_correct = null;
    RecyclerAdapter adapter;
     ArrayList<NoteTitle> items;
    private InterstitialAd mInterstitialAd;
    SearchView   searchView;
    private SimpleCursorAdapter mAdapter;
    TextView nit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(Build.VERSION.SDK_INT>=21){
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.material_gray));
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());

        }

//app id ca-app-pub-2883974575291426~8162645182
//banner ad id ca-app-pub-2883974575291426/6274848447
// interstitial id ca-app-pub-2883974575291426/2807388488

        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-2883974575291426/4870827395");
        // mInterstitialAd.setAdUnitId("ca-app-pub-3940256099942544/1033173712");

        mInterstitialAd.loadAd(new AdRequest.Builder().build());

        final SharedPreferences sharedPreferences= PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        PreferenceManager.setDefaultValues(this, R.xml.preference, false);
        try {
            Reservoir.init(this, 2048); //in bytes
        }catch (Exception e){
            e.printStackTrace();
        }

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


        setContentView(R.layout.main_activity);
        //Log.d ("shouldopen mainactivity",String.valueOf(getIntent().getBooleanExtra("tag",false)));
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        MobileAds.initialize(this, "ca-app-pub-2883974575291426~8103495391");

        toolbar.setTitleTextColor(getResources().getColor(R.color.material_gray));
        AdView mAdView = (AdView) findViewById(R.id.banner);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        nit=(TextView)findViewById(R.id.noitms);
        rec = (RecyclerView) findViewById (R.id.recycler_view);

        rec.setLayoutManager(new LinearLayoutManager(this));

        rec.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              startActivity(new Intent(MainActivity.this,addnote.class));
              overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);

            }
        });


        final String[] from = new String[] {"Name"};
        final int[] to = new int[] {android.R.id.text1};
        mAdapter=new ColoredCursorAdapter(this ,
                android.R.layout.simple_list_item_1,"#FFFFFF",
                null,
                from,
                to,
                CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
        if(getIntent().getBooleanExtra("restore",false)){
         final ArrayList<NoteTitle> newlist=utils.desrializeListFromFile(MainActivity.this);
         Log.d("listxx",String.valueOf(newlist.size()));
         utils.saveAllNotes(newlist);
         this.runOnUiThread(new Runnable() {
             @Override
             public void run() {
                 RecyclerAdapter adapter= new RecyclerAdapter(MainActivity.this,newlist);
                 rec.setAdapter(adapter);
                 ImageView default1 = (ImageView) findViewById(R.id.imageV);
                 TextView main = (TextView) findViewById(R.id.main);


             }
         });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
          searchView  = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setMaxWidth(Integer.MAX_VALUE);
        searchView.setSuggestionsAdapter(mAdapter);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
              //adapter.getFilter().filter(query);

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
               // adapter.getFilter().filter(newText);
                newText=newText.toLowerCase();
                ArrayList<NoteTitle> newList=new ArrayList<>();
                for(NoteTitle title :items){
                    if(title.getNotetitle().toLowerCase().contains(newText.toLowerCase())||title.getItems().get(0).getContent().toLowerCase().contains(newText)||title.getDateTimeFormatted().contains(newText)){
                        newList.add(title);
                    }
                }

                adapter=new RecyclerAdapter(getBaseContext(),newList);
                rec.setAdapter(adapter);
                adapter.setFilter(newList,newText);
                if(newList.size()==0){
                    nit.setVisibility(View.VISIBLE);
                    Log.d("defff","visible");
                }else {
                    nit.setVisibility(View.INVISIBLE);
                    Log.d("defff","invisible");
                }
                String [] suggestions=new String[newList.size()];

                for(int i=0;i<newList.size();i++)
                    suggestions[i]=newList.get(i).getNotetitle();

                populateAdapter(newText,suggestions);
                return true;
            }
        });


        searchView.setOnSuggestionListener(new SearchView.OnSuggestionListener() {
            @Override
            public boolean onSuggestionSelect(int position) {

                searchView.clearFocus();
                return true;
            }

            @Override
            public boolean onSuggestionClick(int position) {
               //handle seach suggestion click
                Cursor searchCursor = mAdapter.getCursor();
                String selectedItem=null;
                if(searchCursor.moveToPosition(position)) {
                     selectedItem = searchCursor.getString(1);
                }
                Log.d("selll",selectedItem);
                ArrayList<NoteTitle> titles=utils.getallsavednotes();
                for(int i=0;i<titles.size();i++){
                    if(titles.get(i).getNotetitle().equals(selectedItem)){
                        ArrayList<NoteTitle> single= new ArrayList<>();
                        single.add(titles.get(i));
                        rec.setAdapter(new RecyclerAdapter(MainActivity.this,single));
                    }
                }
                return false;
            }
        });


        return true;

    }

    @Override
    protected void onResume() {
        super.onResume();

        ImageView default1 = (ImageView) findViewById(R.id.imageV);
        TextView main = (TextView) findViewById(R.id.main);
        items = utils.getallsavednotes();
        if (items == null || items.size() == 0) {
            //Toast.makeText(this,"You have no items saved",Toast.LENGTH_SHORT).show();
            main.setVisibility(View.VISIBLE);
            default1.setVisibility(View.VISIBLE);
            return;
        } else {
            main.setVisibility(View.INVISIBLE);
            default1.setVisibility(View.INVISIBLE);
            //  final Itemadapter ia = new Itemadapter(this, item,items);

            adapter = new RecyclerAdapter(this, items);
            rec.setAdapter(adapter);
            rec.setItemAnimator(new DefaultItemAnimator());
            setSwipeLeft(items, adapter);
            setSwipeRight();
            attachDragListener();
        }
    }
    boolean doubleBackToExitPressedOnce = false;

    @Override
    public void onBackPressed() {
        // close search view on back button pressed
        if (!searchView.isIconified()) {
            searchView.setIconified(true);
            return;
        }
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
public void setSwipeRight(){
    //handle left swipe events


            ItemTouchHelper.SimpleCallback simpleCallback1 = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
                @Override
                public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder viewHolder1) {
                    return false;
                }

                @Override
                public void onSwiped(RecyclerView.ViewHolder viewHolder, int i) {
                    //share cardview content
                    NoteTitle titler= items.get(viewHolder.getAdapterPosition());

                    StringBuilder sb = new StringBuilder();
                    String title = titler.getTitle();
                    String content = titler.getItems().get(0).getContent();
                    String date = titler.getDateTimeFormatted();
                    sb.append(title);
                    sb.append(System.getProperty("line.separator"));
                    sb.append(content);
                    sb.append(System.getProperty("line.separator"));
                    sb.append(getResources().getString(R.string.created) + " " + date);
                    final String itemcontent = sb.toString();
                    Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                    sharingIntent.setType("text/plain");
                    sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, R.string.check);
                    sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, itemcontent);
                    startActivity(Intent.createChooser(sharingIntent, getResources().getString(R.string.share_using)));


                }
            };
            ItemTouchHelper itemTouchHelper2 = new ItemTouchHelper(simpleCallback1);
            itemTouchHelper2.attachToRecyclerView(rec); //set swipe left to recylcer


}
 public void setSwipeLeft(final ArrayList<NoteTitle> items,final RecyclerAdapter adapter){
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


                         //    String filename = ((TextView) viewHolder.itemView.findViewById(R.id.itemdetailsdisplay)).getText().toString() + ".bin";

                         //    mLoadeditem = utils.getitembyfilename(getBaseContext(), filename);


                            // utils.delete(getBaseContext(), mLoadeditem.getItem_name() + ".bin");
                             utils.deleteNoteByIndex(getBaseContext(),position);
                             Log.d("testxxxx", String.valueOf(position));

                             items.remove(viewHolder.getAdapterPosition());

                             adapter.notifyDataSetChanged();
                             adapter.notifyItemRemoved(viewHolder.getAdapterPosition());
                             rec.invalidate();
                             Toast.makeText(getBaseContext(), R.string.item_deleted, Toast.LENGTH_SHORT).show();
                             return;
                         } catch (Exception e) {
                             e.printStackTrace();

                         }
                     }
                 }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {  //not removing items if cancel is done
                     @Override
                     public void onClick(DialogInterface dialog, int which) {
                         rec.setAdapter(adapter);
                         return;
                     }
                 }).show();  //show alert dialog
             }
         }
     };
     ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
     itemTouchHelper.attachToRecyclerView(rec); //set swipe right to recylcer
     //     }
 }
   private void attachDragListener(){

     ItemTouchHelper.Callback ithcallback =new ItemTouchHelper.Callback() {
         @Override
         public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
             return makeFlag(ItemTouchHelper.ACTION_STATE_DRAG,
                     ItemTouchHelper.DOWN | ItemTouchHelper.UP );
         }

         @Override
         public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
             Collections.swap(items,viewHolder.getAdapterPosition(),target.getAdapterPosition());
             adapter.notifyItemMoved(viewHolder.getAdapterPosition(),target.getAdapterPosition());
             utils.saveAllNotes(items);
             return false;
         }

         @Override
         public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {

         }
     };
       ItemTouchHelper ith = new ItemTouchHelper(ithcallback);
       ith.attachToRecyclerView(rec);
   }

    private void populateAdapter(String query,String[] SUGGESTIONS) {
        final MatrixCursor c = new MatrixCursor(new String[]{ BaseColumns._ID, "Name" });
        for (int i=0; i<SUGGESTIONS.length; i++) {
            if (SUGGESTIONS[i].toLowerCase().startsWith(query.toLowerCase()))
                c.addRow(new Object[] {i, SUGGESTIONS[i]});
        }
        mAdapter.changeCursor(c);
    }

}
