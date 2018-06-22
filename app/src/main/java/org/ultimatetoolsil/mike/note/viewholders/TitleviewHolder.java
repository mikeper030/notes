package org.ultimatetoolsil.mike.note.viewholders;

import android.content.Context;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;
import com.thoughtbot.expandablerecyclerview.viewholders.GroupViewHolder;

import org.ultimatetoolsil.mike.note.R;
import org.ultimatetoolsil.mike.note.models.NoteTitle;

/**
 * Created by mike on 15 Jun 2018.
 */

public class TitleviewHolder extends GroupViewHolder {
    private TextView titleName,date;
    private ImageView arrow;

    public TitleviewHolder(View itemView) {
        super(itemView);
        date=(TextView)itemView.findViewById(R.id.date_title);
        titleName = (TextView) itemView.findViewById(R.id.list_item_genre_name);
        arrow = (ImageView) itemView.findViewById(R.id.list_item_genre_arrow);

    }

    public void setGenreicTitle(Context context, ExpandableGroup title) {
        if (title instanceof NoteTitle) {
            titleName.setText(title.getTitle());
            date.setText(String.valueOf(((NoteTitle)title).getDateTimeFormatted()));
//            if (((Title) title).getImageUrl()!= null && !((Title) title).getImageUrl().isEmpty()){
//                Glide.with(context)
//                        .load(((Title) title).getImageUrl())
//                        .into(icon);
//
//            }
        }
    }

    @Override
    public void expand() {
        animateExpand();
    }

    @Override
    public void collapse() {
        animateCollapse();
    }

    private void animateExpand() {
        RotateAnimation rotate =
                new RotateAnimation(360, 180, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotate.setDuration(300);
        rotate.setFillAfter(true);
        arrow.setAnimation(rotate);
    }

    private void animateCollapse() {
        RotateAnimation rotate =
                new RotateAnimation(180, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotate.setDuration(300);
        rotate.setFillAfter(true);
        arrow.setAnimation(rotate);
    }
}
