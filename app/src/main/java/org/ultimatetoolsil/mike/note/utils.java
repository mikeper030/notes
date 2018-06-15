package org.ultimatetoolsil.mike.note;

import android.content.Context;
import android.util.Log;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class utils {
    public static final String EXTRAS_NOTE_FILENAME = "EXRAS_NOTES_FILENAME";
    public static final String FILE_EXTENSION = ".bin";

    public static boolean save(Context context, Item item) {
        String filename = String.valueOf(item.getItem_name()) + FILE_EXTENSION;
        try {
            FileOutputStream fos = context.openFileOutput(filename, 0);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(item);
            oos.close();
            fos.close();
            Log.d("utils", filename);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static ArrayList<Item> getallsaveditems(Context context) {
        Exception e;
        ArrayList<Item> items = new ArrayList();
        File filesdir = context.getFilesDir();
        ArrayList<String> itemsfiles = new ArrayList();
        for (String file : filesdir.list()) {
            if (file.endsWith(FILE_EXTENSION)) {
                itemsfiles.add(file);
            }
        }
        int i = 0;
        while (i < itemsfiles.size()) {
            try {
                FileInputStream fis = context.openFileInput((String) itemsfiles.get(i));
                ObjectInputStream ois = new ObjectInputStream(fis);
                items.add((Item) ois.readObject());
                fis.close();
                ois.close();
                i++;
            } catch (IOException e2) {
                e = e2;
            } catch (ClassNotFoundException e3) {
                e = e3;
            }
        }
        return items;
        //e.printStackTrace();
       // return null;
    }

    public static Item getitembyfilename(Context context, String filename) {
        Exception e;
        File file = new File(context.getFilesDir(), filename);
        if (!file.exists() || file.isDirectory()) {
            return null;
        }
        Log.v("utils", "file exists" + filename);
        try {
            FileInputStream fis = context.openFileInput(filename);
            ObjectInputStream ois = new ObjectInputStream(fis);
            Item item = (Item) ois.readObject();
            fis.close();
            ois.close();
            return item;
        } catch (IOException e2) {
            e = e2;
        } catch (ClassNotFoundException e3) {
            e = e3;
        }
        e.printStackTrace();
        return null;
    }

    public static boolean delete(Context ctx, String filename) {
        File file = new File(ctx.getFilesDir(), filename);
        if (!file.exists() || file.isDirectory()) {
            return false;
        }
        return file.delete();
    }
}