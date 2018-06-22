package org.ultimatetoolsil.mike.note.viewholders;

import android.view.View;
import android.widget.TextView;

import com.thoughtbot.expandablerecyclerview.viewholders.ChildViewHolder;

import org.ultimatetoolsil.mike.note.R;

/**
 * Created by mike on 15 Jun 2018.
 */

public class SubItemviewHolder extends ChildViewHolder{
    private TextView subTitleTextView;

    public SubItemviewHolder(View itemView) {
        super(itemView);
        subTitleTextView = (TextView) itemView.findViewById(R.id.subtitle);
    }

    public void setSubTitletName(String name) {
        subTitleTextView.setText(name);

    }
}
