package org.ultimatetoolsil.mike.note.models;

import android.os.Parcelable;

import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by mike on 15 Jun 2018.
 */

public  class NoteTitle extends ExpandableGroup<SubItem> implements Serializable{
    String notetitle;
    long mdatetime;
    List<SubItem> itms;
    boolean isImageEnabled;
    String imageURI=null;
    String content;
    Calendar alarm;

    public Calendar getAlarm() {
        return alarm;
    }

    public void setAlarm(Calendar alarm) {
        this.alarm = alarm;
    }

    public String getImageURI() {
        return imageURI;
    }

    public void setImageURI(String imageURI) {
        this.imageURI = imageURI;
    }


    public boolean isImageEnabled() {
        return isImageEnabled;
    }

    public void setImageEnabled(boolean imageEnabled) {
        isImageEnabled = imageEnabled;
    }




    transient private Thread myThread;


    public NoteTitle(String title, List<SubItem> items, long datetime,boolean isImageEnabled){
        super(title,items);
        setItms(items);
        setMdatetime(datetime);
        setNotetitle(title);
        setImageEnabled(isImageEnabled);

    }


    public long getMdatetime() {
        return mdatetime;
    }

    public void setMdatetime(long mdatetime) {
        this.mdatetime = mdatetime;
    }



    public String getNotetitle() {
        return notetitle;
    }

    public void setNotetitle(String notetitle) {
        this.notetitle = notetitle;
    }

    public String getDateTimeFormatted() {
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss"
                , Locale.getDefault());
        formatter.setTimeZone(TimeZone.getDefault());
        return formatter.format(new Date(mdatetime));
    }

    public List<SubItem> getItms() {
        return itms;
    }

    public void setItms(List<SubItem> itms) {
        this.itms = itms;
    }
    //============custom desrializer==============


    private void writeObject(final ObjectOutputStream out) throws IOException
    {
        out.writeUTF(this.getNotetitle());
        //out.writeUTF(this.getDateTimeFormatted());
        out.writeUTF(this.getItms().get(0).getContent());
        out.writeUTF(this.getItms().get(0).getImgurl());
    }

    /**
     * Deserialize this instance from input stream.
     *
     * @param in Input Stream from which this instance is to be deserialized.
     * @throws IOException Thrown if error occurs in deserialization.
     * @throws ClassNotFoundException Thrown if expected class is not found.
     */
    private void readObject(final ObjectInputStream in) throws IOException, ClassNotFoundException
    {
        this.notetitle = in.readUTF();
       // this.mdatetime = in.readUTF();
        ArrayList<SubItem> items=new ArrayList<>();
        SubItem item=new SubItem(in.readUTF(),in.readUTF());
        items.add(item);
        this.itms = items;
    }

    private void readObjectNoData() throws ObjectStreamException
    {
        throw new InvalidObjectException("Stream data required");
    }
}
