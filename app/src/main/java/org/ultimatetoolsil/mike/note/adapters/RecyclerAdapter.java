package org.ultimatetoolsil.mike.note.adapters;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;

import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.thoughtbot.expandablerecyclerview.ExpandableRecyclerViewAdapter;
import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;
import com.thoughtbot.expandablerecyclerview.viewholders.GroupViewHolder;

import org.ultimatetoolsil.mike.note.Constants;
import org.ultimatetoolsil.mike.note.Item;
import org.ultimatetoolsil.mike.note.Itemadapter;
import org.ultimatetoolsil.mike.note.R;
import org.ultimatetoolsil.mike.note.addnote;
import org.ultimatetoolsil.mike.note.addnoteFragment;
import org.ultimatetoolsil.mike.note.models.NoteTitle;
import org.ultimatetoolsil.mike.note.models.SubItem;
import org.ultimatetoolsil.mike.note.utils;
import org.ultimatetoolsil.mike.note.viewholders.SubItemviewHolder;
import org.ultimatetoolsil.mike.note.viewholders.TitleviewHolder;

import java.util.ArrayList;
import java.util.List;

import static org.ultimatetoolsil.mike.note.MainFragment.auth;
import static org.ultimatetoolsil.mike.note.MainFragment.myRef;
import static org.ultimatetoolsil.mike.note.MainFragment.userID;

/**
 * Created by mike on 15 Jun 2018.
 */

public class RecyclerAdapter extends ExpandableRecyclerViewAdapter <RecyclerAdapter.NoteViewHolder,SubItemviewHolder> {
    static List<NoteTitle> items = new ArrayList();

    private Context context;
    private static String mFilename;
    private static String mFilename2;
    private List<NoteTitle> itemsFiltered;
    private FirebaseDatabase mFirebaseDatabase;
    private String userID;
    private DatabaseReference myRef;
    String searchText;
    FirebaseAuth auth;
    private boolean db;
    public RecyclerAdapter(Context context, List<NoteTitle> titles,boolean isdbsource) {
        super(titles);
        this.context = context;
        this.items=titles;
        this.itemsFiltered = titles;
        this.db=isdbsource;

    }

    @Override
    public void registerAdapterDataObserver(RecyclerView.AdapterDataObserver observer) {
        super.registerAdapterDataObserver(observer);
    }



    public void setFilter(ArrayList<NoteTitle> notes,String searchText){
     items=new ArrayList<>();
     items.addAll(notes);
     notifyDataSetChanged();
     this.searchText=searchText;
    }
    @Override
    public NoteViewHolder onCreateGroupViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_title, parent, false);
        return new NoteViewHolder(view);
    }

    @Override
    public SubItemviewHolder onCreateChildViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_content, parent, false);
        return new SubItemviewHolder(view);
    }

    @Override
    public void onBindChildViewHolder(SubItemviewHolder holder, int flatPosition,
                                      ExpandableGroup group, int childIndex) {

        final SubItem subTitle = ((NoteTitle) group).getItems().get(childIndex);
        holder.setSubTitletName(subTitle.getContent());

    }

    @Override
    public void onBindGroupViewHolder(NoteViewHolder holder, int position, ExpandableGroup group) {
        holder.setGenreicTitle(context, group);

        if(searchText!=null)
        if(searchText.length()>0) {
            //color your text here
            String desc = items.get(position).getNotetitle();
            SpannableStringBuilder sb=null;
            int index = desc.indexOf(searchText);
            while (index > -1) {
                 sb = new SpannableStringBuilder(desc);
                ForegroundColorSpan fcs = new ForegroundColorSpan(Color.rgb(255, 255, 51)); //specify color here
                sb.setSpan(fcs, index, index+searchText.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                index = desc.indexOf(searchText, index + 1);

            }
            holder.titleName.setText(sb);

        }




}
    public class NoteViewHolder extends GroupViewHolder  {
        boolean isExpanded=false;
        private TextView titleName,date;
        private ImageView arrow;
        private ImageView menu;
        Item mLoadeditem = null;
        int counter=0;
        private PopupMenu popupMenu;
        public NoteViewHolder(View view) {
            super(view);

            date=(TextView)itemView.findViewById(R.id.date_title);
            titleName = (TextView) itemView.findViewById(R.id.list_item_genre_name);
            arrow = (ImageView) itemView.findViewById(R.id.list_item_genre_arrow);
            menu=(ImageView)itemView.findViewById(R.id.menu);

            view.setOnClickListener(this);
            this.menu.setOnClickListener(this);

        }
        public void setGenreicTitle(Context context, ExpandableGroup title) {
            if (title instanceof NoteTitle) {
                titleName.setText(title.getTitle());
                date.setText(String.valueOf(((NoteTitle) title).getDateTimeFormatted()));
//            if (((Title) title).getImageUrl()!= null && !((Title) title).getImageUrl().isEmpty()){
//                Glide.with(context)
//                        .load(((Title) title).getImageUrl())
//                        .into(icon);
//
//            }
            }
        }

//        @Override
//        public void expand() {
//
//        }
//
//        @Override
//        public void collapse() {
//
//        }

        private void animateExpand() {
            Log.d("ANIMATING","expand");

            Animation aniRotate = AnimationUtils.loadAnimation(context,R.anim.collapse);
            aniRotate.setFillAfter(true);
            arrow.startAnimation(aniRotate);
        }

        private void animateCollapse() {
            Log.d("ANIMATING","collapse");
            Animation aniRotate = AnimationUtils.loadAnimation(context,R.anim.expand);
            aniRotate.setFillAfter(true);
            arrow.startAnimation(aniRotate);
        }

        @Override
        public void onClick(final View v) {
            Log.d("counter",String.valueOf(counter));

            counter++;
            if (isExpanded){
                isExpanded=false;
                animateCollapse();
            }else {
                isExpanded=true;
                animateExpand();
            }
            //            try {
            if (v.getId() == this.menu.getId()) {
                auth=FirebaseAuth.getInstance();

                if(auth.getCurrentUser()!=null){
                    db=true;
                    mFirebaseDatabase = FirebaseDatabase.getInstance();
                    myRef = mFirebaseDatabase.getReference();
                    FirebaseUser user = auth.getCurrentUser();
                    userID = user.getUid();
                }
                this.popupMenu = new PopupMenu(v.getContext(), v);
                this.popupMenu.inflate(R.menu.recycler_item_menu);
                this.popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {


                            case R.id.edit /*2131558586*/:

                              if(!db) {
                                  int itemPoition = getLayoutPosition();

                                  Log.d("position", String.valueOf(itemPoition));
                                  Bundle bundle = new Bundle();
                                  bundle.putInt("index", itemPoition);
                                   // set Fragmentclass Arguments
                                  addnoteFragment fragobj = new addnoteFragment();
                                  fragobj.setArguments(bundle);
                                  AppCompatActivity activity = (AppCompatActivity) v.getContext();

                                  activity.getSupportFragmentManager().beginTransaction().replace(android.R.id.content, fragobj).addToBackStack(null).commit();
                                 Log.d("name","no db");
                              }else {

                                  RecyclerAdapter.this.mFilename=((TextView) NoteViewHolder.this.itemView.findViewById(R.id.list_item_genre_name)).getText().toString();
                                  Log.d("name",mFilename);

                                      myRef.child("users").child(userID).child("notes").orderByChild("title").equalTo(mFilename).addChildEventListener(new ChildEventListener() {
                                          @Override
                                          public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                                              String title = dataSnapshot.child("title").getValue(String.class);
                                              String content = dataSnapshot.child("content").getValue(String.class);
                                              Bundle bundle = new Bundle();
                                              bundle.putString("title", title);
                                              bundle.putString("content", content);
                                              addnoteFragment fragobj = new addnoteFragment();
                                              fragobj.setArguments(bundle);
                                              AppCompatActivity activity = (AppCompatActivity) v.getContext();

                                              activity.getSupportFragmentManager().beginTransaction().replace(android.R.id.content, fragobj).addToBackStack(null).commit();
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


                              break;
                            case R.id.item_delete /*2131558587*/:

                                //RecyclerAdapter.this.mFilename = ((TextView) RecyclerAdapter.NoteViewHolder.this.itemView.findViewById(R.id.list_item_genre_name)).getText().toString() + Constants.FILE_EXTENSION;
//                                Log.d("file name is", RecyclerAdapter.this.mFilename);
//                                RecyclerAdapter.NoteViewHolder.this.mLoadeditem = utils.getitembyfilename(v.getContext(), RecyclerAdapter.this.mFilename);
//                                utils.delete(v.getContext(), RecyclerAdapter.NoteViewHolder.this.mLoadeditem.getItem_name() + Constants.FILE_EXTENSION);
                                if(!db) {
                                    try {
                                        utils.deleteNoteByIndex(context, getAdapterPosition());
                                        RecyclerAdapter.items.remove(RecyclerAdapter.NoteViewHolder.this.getAdapterPosition());
                                        notifyDataSetChanged();
                                        notifyItemRemoved(RecyclerAdapter.NoteViewHolder.this.getAdapterPosition());
                                        Toast.makeText(v.getContext(), R.string.item_deleted, Toast.LENGTH_SHORT).show();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }else{
                                    Query query=myRef.child("users").child(userID).child("notes").orderByChild("date").equalTo(items.get(getAdapterPosition()).getMdatetime());
                                    query.addChildEventListener(new ChildEventListener() {
                                        @Override
                                        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                                            dataSnapshot.getRef().setValue(null);
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
                                break;
                            case R.id.item_share /*2131558588*/:
                                NoteTitle titler= items.get(getAdapterPosition());

                                StringBuilder sb = new StringBuilder();
                                String title = titler.getTitle();
                                String content = titler.getItems().get(0).getContent();
                                String date = titler.getDateTimeFormatted();
                                sb.append(title);
                                sb.append(System.getProperty("line.separator"));
                                sb.append(content);
                                sb.append(System.getProperty("line.separator"));
                                sb.append(R.string.created + " " + date);
                                final String itemcontent = sb.toString();
                                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                                sharingIntent.setType("text/plain");
                                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, R.string.check);
                                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, itemcontent);
                                v.getContext().startActivity(Intent.createChooser(sharingIntent, v.getResources().getString(R.string.share_using)));
                                break;
                        }
                        return false;
                    }
                });
                this.popupMenu.show();
                return;


            }else
           super.onClick(v);

        }
    }
//    public void filter(String text) {
//        ArrayList<NoteTitle> itemsCopy= new ArrayList<>(items);
//        Log.d("query",text);
//        items.clear();
//        if(text.isEmpty()){
//            items.addAll(itemsCopy);
//        } else{
//            text = text.toLowerCase();
//            for(NoteTitle item: itemsCopy){
//                if(item.getNotetitle().toLowerCase().contains(text) || item.getItems().get(0).getContent().toLowerCase().contains(text)){
//                    items.add(item);
//                }
//            }
//        }
//        notifyDataSetChanged();
//    }
}
