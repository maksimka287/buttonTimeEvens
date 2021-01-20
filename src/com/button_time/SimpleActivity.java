package com.button_time;

import android.app.Activity;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.widget.RemoteViews;
import android.widget.Toast;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by MaKsa on 03.05.16.
 */
public class SimpleActivity extends AppWidgetProvider {

    public static String ACTION_IN_EVENT = "ActionInEvent";
    public static String ACTION_OUT_EVENT = "ActionOutEVent";
    public static String ACTION_VIEW = "ActionView";
    public static String ACTION_MAIN_VIEW = "ActionMainView";
    private Utility util = new Utility();
    private Connection     db_connect; //соединение с БД
    private SQLiteDatabase db;         //подключение к БД
    private Cursor c_user;

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        //Создаем новый RemoteViews
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.main);

        //Подготавливаем Intent для Broadcast
        //вход
        Intent activeIn = new Intent(context, SimpleActivity.class);
        activeIn.setAction(ACTION_IN_EVENT);
        activeIn.putExtra("msg", "You enter");
        //выход
        Intent activeOut = new Intent(context, SimpleActivity.class);
        activeOut.setAction(ACTION_OUT_EVENT);
        activeOut.putExtra("msg", "You exit");
        //показать события
        Intent actView = new Intent(context, SimpleActivity.class);
        actView.setAction(ACTION_VIEW);
        actView.putExtra("msg", "View events");
        //показать приложение
        //Intent actMainView = new Intent(context, SimpleActivity.class);
        //actMainView.setAction(ACTION_MAIN_VIEW);
        //actMainView.putExtra("msg", "View app");

        //создаем наше событие
        //событие входа
        PendingIntent actionPendIntIn = PendingIntent.getBroadcast(context, 0, activeIn, 0);
        //событие выхода
        PendingIntent actionPendIntOut = PendingIntent.getBroadcast(context, 0, activeOut, 0);
        //отображение событий
        PendingIntent actionViewPendInt = PendingIntent.getBroadcast(context, 0, actView, 0);
        //показать приложение
        //PendingIntent actionMainViewPendInt = PendingIntent.getBroadcast(context, 0, actMainView, 0);

        //регистрируем наше событие
        //событие входа
        remoteViews.setOnClickPendingIntent(R.id.add_event_in, actionPendIntIn);
        //событие выхода
        remoteViews.setOnClickPendingIntent(R.id.add_event_out, actionPendIntOut);
        //отображение событий
        remoteViews.setOnClickPendingIntent(R.id.view_event, actionViewPendInt);
        //показать приложение
        //remoteViews.setOnClickPendingIntent(R.id.view_main, actionMainViewPendInt);

        //обновляем виджет
        appWidgetManager.updateAppWidget(appWidgetIds, remoteViews);
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        //Ловим наш Broadcast, проверяем и выводим сообщение
        final String action = intent.getAction();
        if (ACTION_IN_EVENT.equals(action)) {
            //В этом месте вставляем новое событие входа в БД

            // создаем объект для создания и управления версиями БД
            db_connect = new Connection(context);

            try {
                // подключаемся к БД
                try {
                    db = db_connect.getWritableDatabase();
                } catch (SQLiteException ex) {
                    db = db_connect.getReadableDatabase();
                }

                //получаем текущее время
                android.text.format.Time time = new android.text.format.Time();

                time.setToNow();
                //util.getMessage(context, "Get time");
                //util.getMessage(context, time.format("%d-%m-%Y %H:%M:%S"));

                // создаем объект для данных
                ContentValues cv = new ContentValues();
                //создаем вставку события
                cv.put("date_time_event", time.toMillis(false));
                cv.put("name_event", "enter");
                cv.put("note_event", "n");
                // вставляем запись и получаем ее ID
                //util.getMessage(context, "Insert time");
                long rowID = db.insert("events", null, cv);
                //util.getMessage(context, "Get row id =" + rowID);

                // закрываем подключение к БД
                db_connect.close();
            } catch(Exception e) {
                util.getMessage(context, e.toString());
            }

        }else if (ACTION_OUT_EVENT.equals(action)) {
            //В этом месте вставляем новое событие выхода в БД

            // создаем объект для создания и управления версиями БД
            db_connect = new Connection(context);

            try {
                // подключаемся к БД
                try {
                    db = db_connect.getWritableDatabase();
                } catch (SQLiteException ex) {
                    db = db_connect.getReadableDatabase();
                }

                //получаем текущее время
                android.text.format.Time time = new android.text.format.Time();

                time.setToNow();
                //util.getMessage(context, "Get time");
                //util.getMessage(context, time.format("%d-%m-%Y %H:%M:%S"));

                // создаем объект для данных
                ContentValues cv = new ContentValues();
                //создаем вставку события
                cv.put("date_time_event", time.toMillis(false));
                cv.put("name_event", "exit");
                cv.put("note_event", "n");
                // вставляем запись и получаем ее ID
                //util.getMessage(context, "Insert time");
                long rowID = db.insert("events", null, cv);
                //util.getMessage(context, "Get row id =" + rowID);

                //c_user = db.query("user", columns, selection, null, null, null, null);

                // закрываем подключение к БД
                db_connect.close();
            } catch(Exception e) {
                util.getMessage(context, e.toString());
            }

        }else if (ACTION_VIEW.equals(action)) {
            //В этом месте получаем все события из БД

            // создаем объект для создания и управления версиями БД
            db_connect = new Connection(context);

            try {
                // подключаемся к БД
                try {
                    db = db_connect.getWritableDatabase();
                } catch (SQLiteException ex) {
                    db = db_connect.getReadableDatabase();
                }

                String[] columns ={"id","date_time_event","name_event","note_event"};
                //String selection = "login = '" + edit_login.getText().toString() + "' and password = '" + edit_pass.getText().toString() +"'";
                String where = "id = 1";

                //получение всех событий пользователя
                c_user = db.query("events", columns, null, null, null, null, null);
                //c_user = db.rawQuery("SELECT * FROM events", null);

                util.getMessage(context, "Size query=" + c_user.getCount());
                //получаем текущее время
                android.text.format.Time time = new android.text.format.Time();
                String text;

                    while(c_user.moveToNext()){
                        util.getMessage(context, "Id=" + c_user.getInt(0));
                        time.set(c_user.getLong(1));
                        util.getMessage(context, "Time=" + time.format("%d-%m-%Y %H:%M:%S"));
                        text = c_user.getString(2);
                        util.getMessage(context, "Name=" + text);
                        text = c_user.getString(3);
                        util.getMessage(context, "Note=" + text);
                    };

                // закрываем подключение к БД
                db_connect.close();
            } catch(Exception e) {
                util.getMessage(context, e.toString());
            }

        }//else if (ACTION_MAIN_VIEW.equals(action)) {
            //Activity act = new Activity();
            //Intent intent_main = new Intent(context, MainActivity.class);
            //intent.addFlags(1073741824);
            //act.startActivity(intent_main);
            //startActivity(intent_main);
        //}
        super.onReceive(context, intent);
    }
}
