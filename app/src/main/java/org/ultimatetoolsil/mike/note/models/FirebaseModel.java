package org.ultimatetoolsil.mike.note.models;

/**
 * Created by mike on 10 Jul 2018.
 */

public class FirebaseModel {
String title;
String content;
long date;
    public FirebaseModel(){

    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }
}
