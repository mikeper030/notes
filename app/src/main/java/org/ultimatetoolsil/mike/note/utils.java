package org.ultimatetoolsil.mike.note;

import android.content.Context;
import android.graphics.Color;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.anupcowkur.reservoir.Reservoir;
import com.anupcowkur.reservoir.ReservoirGetCallback;
import com.anupcowkur.reservoir.ReservoirPutCallback;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.gson.reflect.TypeToken;

import org.ultimatetoolsil.mike.note.models.ExportModel;
import org.ultimatetoolsil.mike.note.models.NoteTitle;
import org.ultimatetoolsil.mike.note.models.SubItem;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.ultimatetoolsil.mike.note.Constants.FILE_EXTENSION;

public class utils {
    public static ArrayList<NoteTitle> its;
    public  static  NoteTitle current;
    public static boolean temp=false;
    public static boolean savenote(NoteTitle item,final  Context context) {
        //String filename = String.valueOf(item.getNotetitle()) + FILE_EXTENSION;
        ArrayList<NoteTitle> note=getallsavednotes();
        if(note==null) {
            Log.d("no","motes found");
            note = new ArrayList<NoteTitle>();
             note.add(item);
        }
        else
            note.add(item);

        try {


            Reservoir.putAsync("main", note, new ReservoirPutCallback() {
                @Override
                public void onSuccess() {
                   Log.d("serialize","success");
                }

                @Override
                public void onFailure(Exception e) {
                    Toast.makeText(context,"Error. please try later",Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            });



            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    public static ArrayList<NoteTitle> getallsavednotes() {

            Type resultType = new TypeToken<ArrayList<NoteTitle>>() {}.getType();

        try {
            List<NoteTitle> testResultCollection= Reservoir.get("main", resultType);
            its=new ArrayList<NoteTitle>(testResultCollection);
        } catch (Exception e) {
           its=null;
            e.printStackTrace();
        }


        return its;
    }
    public static boolean saveoldNote(NoteTitle title ,int index,final Context context){
        //String filename = String.valueOf(item.getNotetitle()) + FILE_EXTENSION;
        ArrayList<NoteTitle> notes=getallsavednotes();

          notes.set(index,title);

        try {

            Reservoir.putAsync("main", notes, new ReservoirPutCallback() {
                @Override
                public void onSuccess() {
                    Log.d("serialize","success");
                }

                @Override
                public void onFailure(Exception e) {
                    Toast.makeText(context,"Error. please try later",Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            });



            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    public static boolean deleteNoteByIndex(final Context context, int index){
        Type resultType = new TypeToken<List<NoteTitle>>() {}.getType();
        try {
            List<NoteTitle> titles=  Reservoir.get("main", resultType);
            titles.remove(index);
            Reservoir.putAsync("main", titles, new ReservoirPutCallback() {
                @Override
                public void onSuccess() {
                    Log.d("serialize","success");
                    temp=true;
                }

                @Override
                public void onFailure(Exception e) {
                    Toast.makeText(context,"Error. please try later",Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            });

        } catch (IOException e) {
            e.printStackTrace();//failure
        }
       return temp;
    }
    public static NoteTitle getNotebyfilename(Context context, final int index) {

        Type resultType = new TypeToken<List<NoteTitle>>() {}.getType();
        try {
          List<NoteTitle> titles=  Reservoir.get("main", resultType);
           current=titles.get(index);
        } catch (IOException e) {
            e.printStackTrace();//failure
        }
        Log.d("loaded",current.getNotetitle());
        return current;
    }




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
public static boolean serializeListToFile(Context context){

    try{
        ArrayList<NoteTitle> titles= getallsavednotes();
        ArrayList<ExportModel> models= new ArrayList<>();
        for(int i=0;i<titles.size();i++){
            NoteTitle title=titles.get(i);
           ExportModel md=new ExportModel(title.getNotetitle(),title.getMdatetime(),title.getItms().get(0).getContent(),title.isImageEnabled(),title.getImageURI());
           md.setAlarm(title.getAlarm());
            models.add(md);
        }
        FileOutputStream fos= new FileOutputStream(Environment.getExternalStorageDirectory()+"/MyNotes/data.bin");
        ObjectOutputStream oos= new ObjectOutputStream(fos);
        oos.writeObject(models);
        oos.close();
        fos.close();



    }catch(IOException ioe){
        ioe.printStackTrace();
       // Toast.makeText(context,"Error. please try later",Toast.LENGTH_SHORT).show();
        return false;
    }
        return true;
}
public static ArrayList<NoteTitle> desrializeListFromFile(Context context){
    ArrayList<ExportModel> arraylist= new ArrayList<ExportModel>();
    ArrayList<NoteTitle> titles=new ArrayList<>();
    try
    {
        FileInputStream fis = new FileInputStream(Environment.getExternalStorageDirectory()+"/MyNotes/data.bin");
        ObjectInputStream ois = new ObjectInputStream(fis);
        arraylist = (ArrayList< ExportModel>) ois.readObject();
        ois.close();
        fis.close();
        for(int i=0;i<arraylist.size();i++){
            ArrayList<SubItem> subtitle= new ArrayList<>();
            subtitle.add(new SubItem(arraylist.get(i).getContent(),arraylist.get(i).getImageURI()));
            ExportModel model= arraylist.get(i);
            NoteTitle title= new NoteTitle(model.getNotetitle(),subtitle,model.getMdatetime(),model.isImageEnabled());
            titles.add(title);
        }

    }catch(IOException ioe){
        ioe.printStackTrace();
        return null;
    }catch(ClassNotFoundException c){
        c.printStackTrace();
        return null;
    }
   return titles;
  }
  public static void saveAllNotes(ArrayList<NoteTitle> titles) {


      Reservoir.putAsync("main", titles, new ReservoirPutCallback() {
          @Override
          public void onSuccess() {
              Log.d("serialize", "success");

          }

          @Override
          public void onFailure(Exception e) {
              //Toast.makeText(context,"Error. please try later",Toast.LENGTH_SHORT).show();
              e.printStackTrace();
          }
      });


  }

    public static void deleteFile(String s) {
    File f =new File(s);
    if(f.exists()){
        f.delete();
    }

  }


}