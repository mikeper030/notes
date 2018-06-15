package org.ultimatetoolsil.mike.note;

import android.content.Context;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by Mike on 11/07/2017.
 */

public class Item implements Serializable {
  private Long mdatetime;
  private String item_name,post_number;



    public Item (String item_name, String post_number, Long dateinmillis)



    {
        setItem_name(item_name);
        setPost_number(post_number);
        setMdatetime(dateinmillis);
    }
    public Long getMdatetime() {
        return mdatetime;
    }

    public void setMdatetime(Long mdatetime) {
        this.mdatetime = mdatetime;
    }

    public String getItem_name() {
        return this.item_name;
    }

    public void setItem_name(String item_name) {
        this.item_name = item_name;
    }

    public String getPost_number() {
        return post_number;
    }

    public void setPost_number(String post_number) {
        this.post_number = post_number;
    }

    public String getDateTimeFormatted() {
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss"
                , Locale.getDefault());
        formatter.setTimeZone(TimeZone.getDefault());
        return formatter.format(new Date(mdatetime));
    }





}


