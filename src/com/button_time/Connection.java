package com.button_time;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by MaKsa on 20.03.16.
 */
public class Connection extends SQLiteOpenHelper {

    private final String TABLE_NAME = "events";

    public Connection(Context context) {
        /* конструктор суперкласса создания базы данных
         * класс создатель    context
         * имя базы данных    event_statistic
         * параметр курсора   null
         * версия базы данных 1
         */
        super(context, "event_statistic", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // создаем таблицы с полями

        //таблица с событиями
        db.execSQL("CREATE TABLE " + TABLE_NAME + " (" +
                   " id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                   " date_time_event TIMESTAMP, " +
                   " name_event TEXT, "+
                   " note_event TEXT);");

        // создаем объект для данных
        //ContentValues cv = new ContentValues();
        //создаем демо пользователя
        //cv.put("login", "demo");
        //cv.put("password", "demo");
        //cv.put("role", 3);
        // вставляем запись и получаем ее ID
        //long rowID = db.insert("user", null, cv);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //Обновлять базу не будем
    }

}
