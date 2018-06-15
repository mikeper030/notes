package org.ultimatetoolsil.mike.note;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.PopupMenu.OnMenuItemClickListener;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;

public class Itemadapter extends RecyclerView.Adapter<Itemadapter.itemviewholder> {
    static ArrayList<Item> items = new ArrayList();
    private String mFilename;
    private String mFilename2;
    Context mcontext;
//itemnumberdisplay = content
    //itemdetailsdisplay = title
    public class itemviewholder extends ViewHolder implements OnClickListener {

        TextView ico;
        ImageButton ico1;
        TextView itemname;
        Item mLoadeditem = null;
        private PopupMenu popupMenu;
        TextView postnumber,date;
        private ImageView arrowup,arrowdown;

        public itemviewholder(View view) {
            super(view);
            this.itemname =  view.findViewById(R.id.itemdetailsdisplay);
            this.postnumber =  view.findViewById(R.id.itemnuberdisplay);
            this.date= view.findViewById(R.id.datedisplay);
            this.ico1 =  view.findViewById(R.id.itemmenu);
           this.arrowdown = view.findViewById(R.id.down);
           this.arrowup = view.findViewById(R.id.upi);


            view.setOnClickListener(this);
            this.ico1.setOnClickListener(this);

        }

        public void onClick(final View v) {
            try {
                if (v.getId() == this.ico1.getId()) {
                    this.popupMenu = new PopupMenu(v.getContext(), v);
                    this.popupMenu.inflate(R.menu.recycler_item_menu);
                    this.popupMenu.setOnMenuItemClickListener(new OnMenuItemClickListener() {
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.edit /*2131558586*/:
                                    Itemadapter.this.mFilename2 = ((TextView) itemviewholder.this.itemView.findViewById(R.id.itemdetailsdisplay)).getText().toString() + utils.FILE_EXTENSION;
                                    Intent editinfo = new Intent(v.getContext(), addnote.class);
                                    editinfo.putExtra("item file", Itemadapter.this.mFilename2);
                                    Log.d("Intent", Itemadapter.this.mFilename2);
                                    v.getContext().startActivity(editinfo);
                                    break;
                                case R.id.item_delete /*2131558587*/:

                                    Itemadapter.this.mFilename = ((TextView) itemviewholder.this.itemView.findViewById(R.id.itemdetailsdisplay)).getText().toString() + utils.FILE_EXTENSION;
                                    Log.d("file name is", Itemadapter.this.mFilename);
                                    itemviewholder.this.mLoadeditem = utils.getitembyfilename(v.getContext(), Itemadapter.this.mFilename);
                                    utils.delete(v.getContext(), itemviewholder.this.mLoadeditem.getItem_name() + utils.FILE_EXTENSION);
                                    Itemadapter.items.remove(itemviewholder.this.getAdapterPosition());
                                    Itemadapter.this.notifyDataSetChanged();
                                    Toast.makeText(v.getContext(), R.string.item_deleted, Toast.LENGTH_SHORT).show();
                                    break;
                                case R.id.item_share /*2131558588*/:
                                    StringBuilder sb = new StringBuilder();
                                    String title = ((TextView) itemviewholder.this.itemView.findViewById(R.id.itemdetailsdisplay)).getText().toString();
                                    String content = ((TextView) itemviewholder.this.itemView.findViewById(R.id.itemnuberdisplay)).getText().toString();
                                    String date = ((TextView)itemviewholder.this.itemView.findViewById(R.id.datedisplay)).getText().toString();
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
                                    v.getContext().startActivity(Intent.createChooser(sharingIntent,v.getResources().getString(R.string.share_using)));
                                    break;
                            }
                            return false;
                        }
                    });
                    this.popupMenu.show();
                    return;
                }


      //HANDLING CARD ARROW ICONS
        if(itemView.findViewById(R.id.itemnuberdisplay).isShown()) {
            v.findViewById(R.id.itemnuberdisplay).setVisibility(View.INVISIBLE);
            ViewGroup.LayoutParams params = v.getLayoutParams();
            params.height =265;
            v.setLayoutParams(params);

            v. findViewById(R.id.down).setVisibility(View.VISIBLE);
            v.findViewById(R.id.upi).setVisibility(View.INVISIBLE);
        }
        else {
            v.findViewById(R.id.itemnuberdisplay).setVisibility(View.VISIBLE);
            ViewGroup.LayoutParams params = v.getLayoutParams();
            params.height = RecyclerView.LayoutParams.MATCH_PARENT;
            v.setLayoutParams(params);
            v. findViewById(R.id.down).setVisibility(View.INVISIBLE);
            v.findViewById(R.id.upi).setVisibility(View.VISIBLE);
        }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public Itemadapter(MainActivity mainActivity, int item, ArrayList<Item> items) {
       this.items = items;
    }

    public itemviewholder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new itemviewholder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item, parent, false));
    }

    public void onBindViewHolder(itemviewholder holder, int position) {

        Item item =  items.get(position);
        holder.postnumber.setText(item.getPost_number());
        holder.itemname.setText(item.getItem_name());
        holder.date.setText(item.getDateTimeFormatted());
        holder.postnumber.setVisibility(View.INVISIBLE);
        holder.arrowdown.setVisibility(View.VISIBLE);
        holder.arrowup.setVisibility(View.INVISIBLE);
    }

    public int getItemCount() {
        return items.size();
    }
}