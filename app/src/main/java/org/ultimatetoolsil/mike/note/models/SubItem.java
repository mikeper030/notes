package org.ultimatetoolsil.mike.note.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by mike on 15 Jun 2018.
 */

public class SubItem implements Parcelable,Serializable{
String content;
     public SubItem (String content,String imgurl){
       this.content=content;
    }
    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getImgurl() {
        return imgurl;
    }

    public void setImgurl(String imgurl) {
        this.imgurl = imgurl;
    }

    String imgurl;

    @Override
    public int describeContents() {
        return 0;
    }

    protected SubItem(Parcel in) {
        this.content = in.readString();
    }
    @Override
    public void writeToParcel(Parcel dest, int i) {
        dest.writeString(this.content);
    }
    public static final Parcelable.Creator<SubItem> CREATOR = new Parcelable.Creator<SubItem>() {
        @Override
        public SubItem createFromParcel(Parcel source) {
            return new SubItem(source);
        }

        @Override
        public SubItem[] newArray(int size) {
            return new SubItem[size];
        }
    };
}
