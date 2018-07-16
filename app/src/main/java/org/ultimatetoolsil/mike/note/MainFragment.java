package org.ultimatetoolsil.mike.note;


import android.app.AlarmManager;
import android.app.FragmentTransaction;
import android.app.PendingIntent;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.BaseColumns;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.anupcowkur.reservoir.Reservoir;
import com.firebase.client.Firebase;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.ultimatetoolsil.mike.note.adapters.RecyclerAdapter;
import org.ultimatetoolsil.mike.note.models.ColoredCursorAdapter;
import org.ultimatetoolsil.mike.note.models.NoteTitle;
import org.ultimatetoolsil.mike.note.models.SubItem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static org.ultimatetoolsil.mike.note.addnote.RQS_1;

/**
 * Created by mike on 22 Jun 2018.
 */

public class MainFragment extends Fragment {

    RecyclerView rec;
    public static String userID;
    public static DatabaseReference myRef;
    private  String password_correct = null;
    RecyclerAdapter adapter;
    ArrayList<NoteTitle> items;
    private InterstitialAd mInterstitialAd;
    SearchView searchView;
    private SimpleCursorAdapter mAdapter;
    TextView nit;
    ImageView default1;
    TextView main;
    public static FirebaseAuth auth;
    private boolean dbsource=false;
    public static FirebaseDatabase mFirebaseDatabase;
    ProgressBar progressBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.main_list,
                container, false);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() == null) {

            startActivity(new Intent(getActivity(), Signups.class));
           getActivity().finish();
        } else {
            mFirebaseDatabase = FirebaseDatabase.getInstance();
            myRef = mFirebaseDatabase.getReference();
            FirebaseUser user = auth.getCurrentUser();
            userID = user.getUid();
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
            dbsource=true;
        }
        if (getView() == null) {
            return;
        }
        progressBar.setVisibility(View.VISIBLE);
        getView().setFocusableInTouchMode(true);
        getView().requestFocus();
        getView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
                    if (!searchView.isIconified()) {
                        searchView.setIconified(true);
                        return true;
                    }
                    if (doubleBackToExitPressedOnce) {
                        Intent homeIntent = new Intent(Intent.ACTION_MAIN);
                        homeIntent.addCategory(Intent.CATEGORY_HOME);
                        homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(homeIntent);
                        getActivity().onBackPressed();
                        return true;
                    }

                    doubleBackToExitPressedOnce = true;
                    Toast.makeText(getActivity(), R.string.exit, Toast.LENGTH_SHORT).show();

                    new Handler().postDelayed(new Runnable() {

                        @Override
                        public void run() {
                            doubleBackToExitPressedOnce = false;
                        }
                    }, 2000);
                    return true;
                }
                return false;
            }
        });
        if (!dbsource) {
            items = utils.getallsavednotes();
            if (items == null || items.size() == 0) {
                //Toast.makeText(this,"You have no items saved",Toast.LENGTH_SHORT).show();
                main.setVisibility(View.VISIBLE);
                default1.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.INVISIBLE);
                return;
            } else {
                main.setVisibility(View.INVISIBLE);
                default1.setVisibility(View.INVISIBLE);
                //  final Itemadapter ia = new Itemadapter(this, item,items);

                adapter = new RecyclerAdapter(getActivity(), items,dbsource);
                rec.setAdapter(adapter);
                rec.setItemAnimator(new DefaultItemAnimator());
                setSwipeLeft(items, adapter);
                setSwipeRight();
                attachDragListener();
                progressBar.setVisibility(View.INVISIBLE);
            }
        } else {
            setTimeout(5000);
            items = new ArrayList<>();

                myRef.child("users").child(userID).child("notes").addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        default1.setVisibility(View.INVISIBLE);
                        main.setVisibility(View.INVISIBLE);
                        progressBar.setVisibility(View.INVISIBLE);
                        getUpdates(dataSnapshot);

                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                        getUpdates(dataSnapshot);
                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {

                        onResume();
                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                        getUpdates(dataSnapshot);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        main.setVisibility(View.VISIBLE);
                        default1.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.INVISIBLE);
                    }
                });

        }



        }

    private void setTimeout(int time){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                progressBar.setVisibility(View.INVISIBLE);
            }
        }, time);
    }
    private void getUpdates(DataSnapshot dataSnapshot) {
          long notification=0000;
          String title=dataSnapshot.child("title").getValue(String.class);
          long d = dataSnapshot.child("date").getValue(long.class);
        String content=dataSnapshot.child("content").getValue(String.class);
         try {
             notification=dataSnapshot.child("notification").getValue(Long.class);
         }catch (Exception e){
             e.printStackTrace();
         }


          for(int i=0;i<items.size();i++){
              if(items.get(i).getMdatetime()==d){
                  SubItem item= new SubItem(content,"def");
                  ArrayList<SubItem> tms=new ArrayList<>();
                  tms.add(item);
                  items.set(i,new NoteTitle(title,tms,d,false));
                  adapter=new RecyclerAdapter(getActivity(),items,true);
                  rec.setAdapter(adapter);
                  setSwipeLeft(items, adapter);
                  setSwipeRight();
                  attachDragListener();
                //update notification
                  if(notification!=0000&&notification>System.currentTimeMillis()) {
                      Intent intent = new Intent(getActivity(), AlarmReciever.class);
                      intent.putExtra("title", title);
                      PendingIntent pendingIntent = PendingIntent.getBroadcast(getActivity(), RQS_1, intent, 0);
                      //pass note info to broadcast receiver
                      Log.d("notification title", title);
                      AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
                      alarmManager.set(AlarmManager.RTC_WAKEUP, notification, pendingIntent);
                  }
                  return;
              }
          }
          //update notification
        if(notification!=0000&&notification>System.currentTimeMillis()) {
            Intent intent = new Intent(getActivity(), AlarmReciever.class);
            intent.putExtra("title", title);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(getActivity(), RQS_1, intent, 0);
            //pass note info to broadcast receiver
            Log.d("notification title", title);
            AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
            alarmManager.set(AlarmManager.RTC_WAKEUP, notification, pendingIntent);
        }
         //load the notes
          ArrayList<SubItem> itms= new ArrayList<>();
          itms.add(new SubItem(content,null));
          NoteTitle title1=new NoteTitle(title,itms,d,false);
          items.add(title1);
          adapter=new RecyclerAdapter(getActivity(),items,dbsource);
          rec.setAdapter(adapter);
        setSwipeLeft(items, adapter);
        setSwipeRight();
        attachDragListener();

    }

    boolean doubleBackToExitPressedOnce = false;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {

            //move to settings activity


                    Intent in = new Intent(getActivity(),settings.class);
            startActivity(in);



            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        getActivity().getMenuInflater().inflate(R.menu.menu_main, menu);
        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        searchView  = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
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

                adapter=new RecyclerAdapter(getActivity(),newList,dbsource);
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
                        rec.setAdapter(new RecyclerAdapter(getActivity(),single,dbsource));
                    }
                }
                return false;
            }
        });



    }

    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        default1 = (ImageView) view.findViewById(R.id.imageV);
         main = (TextView) view.findViewById(R.id.main);
        mInterstitialAd = new InterstitialAd(getActivity());
        // mInterstitialAd.setAdUnitId("ca-app-pub-2883974575291426/4870827395");
        mInterstitialAd.setAdUnitId("ca-app-pub-3940256099942544/1033173712");
         setHasOptionsMenu(true);
        mInterstitialAd.loadAd(new AdRequest.Builder().build());

        final SharedPreferences sharedPreferences= PreferenceManager.getDefaultSharedPreferences(getActivity());
        PreferenceManager.setDefaultValues(getActivity(), R.xml.preference, false);
        try {
            Reservoir.init(getActivity(), 200000); //in bytes
        }catch (Exception e){
            e.printStackTrace();
        }

        //when redirected from patternlock screen check if the password was inputed correctly
        //if yes load the app

        //if locking screen is enabled redirect to locking screen
        if(getActivity().getIntent().getStringExtra("tag")!=null){

            password_correct = getActivity().getIntent().getStringExtra("tag");}


        //  Log.d ("shouldopen mainactivity",password_correct);
        if(sharedPreferences.getBoolean("pattern",true)&& password_correct == null||sharedPreferences.getBoolean("pattern",true)&& password_correct=="no")
        //   sharedPreferences.getBoolean("pattern",true) && password_correct.isEmpty())
        {

            // Log.d (,password_correct);
            Intent lockingscreen = new Intent(getActivity(),PatternActivity.class);
            startActivity(lockingscreen);

        }


        //Log.d ("shouldopen mainactivity",String.valueOf(getIntent().getBooleanExtra("tag",false)));
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);

        MobileAds.initialize(getActivity(), "ca-app-pub-2883974575291426~8103495391");

        toolbar.setTitleTextColor(getResources().getColor(R.color.material_gray));
        AdView mAdView = (AdView) view.findViewById(R.id.banner);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        progressBar=(ProgressBar)view.findViewById(R.id.progressBar);
        nit=(TextView)view.findViewById(R.id.noitms);

        rec = (RecyclerView) view.findViewById (R.id.recycler_view);

        rec.setLayoutManager(new LinearLayoutManager(getActivity()));

        rec.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL));


        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addnoteFragment nextFrag= new addnoteFragment();
                android.support.v4.app.FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                transaction.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right, R.anim.slide_in_right, R.anim.slide_out_left);
                transaction

                        .replace(android.R.id.content, nextFrag,"note")
                        .addToBackStack(null)
                        .commit();
            }
        });

        try {


            if(isFirstTime()){
                Log.d("first time","app instaled");
                if(isInstallFromUpdate(getActivity())){
                    Log.d("updating from pre","version");

                    ArrayList<Item> olditems=utils.getallsaveditems(getActivity());
                    items=new ArrayList<>();
                    for (int i=0;i<olditems.size();i++){
                        List<SubItem> items1=new ArrayList<>();
                        items1.add(new SubItem(olditems.get(i).getPost_number(),null));

                        NoteTitle title= new NoteTitle(olditems.get(i).getItem_name(),items1,olditems.get(i).getMdatetime(),false);
                        items.add(title);
                    }
                    utils.saveAllNotes(items);
//refresh layout
//                    Intent intent = getIntent();
//                     getActivity().overridePendingTransition(0, 0);
//                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
//                    finish();
//                    getActivity().overridePendingTransition(0, 0);
//                    startActivity(intent);

                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        final String[] from = new String[] {"Name"};
        final int[] to = new int[] {android.R.id.text1};
        mAdapter=new ColoredCursorAdapter(getActivity() ,
                android.R.layout.simple_list_item_1,"#FFFFFF",
                null,
                from,
                to,
                CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
        if(getActivity().getIntent().getBooleanExtra("restore",false)){
            final ArrayList<NoteTitle> newlist=utils.desrializeListFromFile(getActivity());
            Log.d("listxx",String.valueOf(newlist.size()));
            utils.saveAllNotes(newlist);
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    RecyclerAdapter adapter= new RecyclerAdapter(getActivity(),newlist,false);
                    rec.setAdapter(adapter);
                    ImageView default1 = (ImageView) view.findViewById(R.id.imageV);
                    TextView main = (TextView) view.findViewById(R.id.main);


                }
            });
        }

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

                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity()); //alert for confirm to delete
                    builder.setMessage(R.string.question_delete);    //set message

                    builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() { //when click on DELETE
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            try {


                                //    String filename = ((TextView) viewHolder.itemView.findViewById(R.id.itemdetailsdisplay)).getText().toString() + ".bin";

                                //    mLoadeditem = utils.getitembyfilename(getBaseContext(), filename);


                                // utils.delete(getBaseContext(), mLoadeditem.getItem_name() + ".bin");
                                utils.deleteNoteByIndex(getActivity(),position);
                                Log.d("testxxxx", String.valueOf(position));

                                items.remove(viewHolder.getAdapterPosition());

                                adapter.notifyDataSetChanged();
                                adapter.notifyItemRemoved(viewHolder.getAdapterPosition());
                                rec.invalidate();
                                Toast.makeText(getActivity(), R.string.item_deleted, Toast.LENGTH_SHORT).show();
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
    public  boolean isFirstInstall(Context context) {
        try {
            long firstInstallTime = context.getPackageManager().getPackageInfo(getActivity().getPackageName(), 0).firstInstallTime;
            long lastUpdateTime = context.getPackageManager().getPackageInfo(getActivity().getPackageName(), 0).lastUpdateTime;
            return firstInstallTime == lastUpdateTime;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return true;
        }
    }



    public  boolean isInstallFromUpdate(Context context) {
        try {
            long firstInstallTime =   context.getPackageManager().getPackageInfo(getActivity().getPackageName(), 0).firstInstallTime;
            long lastUpdateTime = context.getPackageManager().getPackageInfo(getActivity().getPackageName(), 0).lastUpdateTime;
            return firstInstallTime != lastUpdateTime;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }
    private boolean isFirstTime() {
        boolean result=true;
        if(PreferenceManager.getDefaultSharedPreferences(getActivity()).getString("first",null)==null)
            PreferenceManager.getDefaultSharedPreferences(getActivity()).edit().putString("first", "asfd").apply();
        else
            result= false;
        return result;
    }
    public interface OnBackPressed {
        void onBackPressed();
    }
    public void onBackPressed() {}
}
