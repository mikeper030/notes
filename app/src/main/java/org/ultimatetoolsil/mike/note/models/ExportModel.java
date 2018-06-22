package org.ultimatetoolsil.mike.note.models;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by mike on 20 Jun 2018.
 */

public class ExportModel implements Serializable
{
    String notetitle;
    long mdatetime;
    String content;
    boolean isImageEnabled;
    String imageURI=null;
    Calendar alarm;

    public Calendar getAlarm() {
        return alarm;
    }

    public void setAlarm(Calendar alarm) {
        this.alarm = alarm;
    }

    public String getNotetitle() {
        return notetitle;
    }

    public void setNotetitle(String notetitle) {
        this.notetitle = notetitle;
    }

    public long getMdatetime() {
        return mdatetime;
    }

    public void setMdatetime(long mdatetime) {
        this.mdatetime = mdatetime;
    }



    public boolean isImageEnabled() {
        return isImageEnabled;
    }

    public void setImageEnabled(boolean imageEnabled) {
        isImageEnabled = imageEnabled;
    }

    public String getImageURI() {
        return imageURI;
    }

    public void setImageURI(String imageURI) {
        this.imageURI = imageURI;
    }

    public ExportModel(String notetitle, long mdatetime,String cont, boolean isImageEnabled, String imageURI) {

        this.notetitle = notetitle;
        this.mdatetime = mdatetime;
        this.content= cont;
        this.isImageEnabled = isImageEnabled;
        this.imageURI = imageURI;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
