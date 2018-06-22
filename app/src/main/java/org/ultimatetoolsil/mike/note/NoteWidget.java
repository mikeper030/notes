package org.ultimatetoolsil.mike.note;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;

import org.ultimatetoolsil.mike.note.models.NoteTitle;

import java.util.ArrayList;

/**
 * Implementation of App Widget functionality.
 */
public class NoteWidget extends AppWidgetProvider {

    private static final String OnClickprev="tag1",onClickNext="tag2";
    static int counter=0;
    static ArrayList<NoteTitle> titles;
    static AppWidgetManager appWManager;
    static int WidgetId;
    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        titles=utils.getallsavednotes();
        if (OnClickprev.equals(intent.getAction())){
            //your onClick action is here
            counter--;
            if(counter<0)
                counter=0;
            Log.d("counter",String.valueOf(counter));
            updateAppWidget(context, appWManager, WidgetId);

        }
        if(onClickNext.equals((intent.getAction()))){
        counter++;
        if (counter==titles.size())
            counter=titles.size()-1;
            Log.d("counter",String.valueOf(counter));
            updateAppWidget(context, appWManager, WidgetId);
        }
    }

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {


        // Construct the RemoteViews object
        titles=utils.getallsavednotes();

         WidgetId=appWidgetId;
         appWManager=appWidgetManager;
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.note_widget);
        views.setTextViewText(R.id.appwidget_text,titles.get(counter).getItems().get(0).getContent());
        views.setImageViewResource(R.id.next,R.drawable.next);
        views.setImageViewResource(R.id.prv,R.drawable.prev);
        views.setOnClickPendingIntent(R.id.next, getPendingSelfIntent(context,onClickNext));
        views.setOnClickPendingIntent(R.id.prv,getPendingSelfIntent(context,OnClickprev));
        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);

    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
    protected static PendingIntent getPendingSelfIntent(Context context, String action) {
        Intent intent = new Intent(context, NoteWidget.class);
        intent.setAction(action);
        return PendingIntent.getBroadcast(context, 0, intent, 0);
    }
}

