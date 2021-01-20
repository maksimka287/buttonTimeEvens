package com.button_time;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.text.format.Time;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.text.DecimalFormat;
import java.util.*;

/**
 * Created by MaKsa on 10.05.16.
 */
public class Statistics extends Activity {
    private Utility util = new Utility();

    private Connection     db_connect; //соединение с БД
    private SQLiteDatabase db;         //подключение к БД
    private Cursor c_stat;

    private android.text.format.Time time = new android.text.format.Time();

    private ListView list_view_stat;
    private ArrayAdapter<String> adapter_stat;
    private List<String> list_stat = new LinkedList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.statistics);

        // создаем объект для создания и управления версиями БД
        db_connect = new Connection(this);
        adapter_stat = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, list_stat);

        Button view_day = (Button) findViewById(R.id.view_time_day);
        view_day.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // подключаемся к БД
                try {
                    db = db_connect.getWritableDatabase();
                } catch (SQLiteException ex) {
                    db = db_connect.getReadableDatabase();
                    util.getMessage(getApplicationContext(), "Error connect " + ex.toString());
                }

                try {
                    String[] columns = {"id", "date_time_event", "name_event", "note_event"};
                    //name event 'enter' and 'exit'
                    String selection_date = "SELECT MAX(date_time_event), MIN(date_time_event) FROM events ";
                    c_stat = db.rawQuery(selection_date, null);
                    //util.getMessage(getApplicationContext(),"size query ="+c_stat.getCount());

                    Calendar min_cal = new GregorianCalendar();
                    Calendar max_cal = new GregorianCalendar();

                    if (c_stat.moveToNext()) {
                        max_cal.setTimeInMillis(c_stat.getLong(0));
                        //util.getMessage(getApplicationContext(),"max time ="+time_max.format("%d-%m-%Y %H:%M:%S"));
                        min_cal.setTimeInMillis(c_stat.getLong(1));
                        //util.getMessage(getApplicationContext(),"min time ="+time_min.format("%d-%m-%Y %H:%M:%S"));
                    }

                    min_cal.set(Calendar.HOUR_OF_DAY, 0);
                    min_cal.set(Calendar.MINUTE, 0);
                    min_cal.set(Calendar.SECOND, 0);

                    //max_cal.add(Calendar.DAY_OF_MONTH, -1);
                    max_cal.set(Calendar.HOUR_OF_DAY, 0);
                    max_cal.set(Calendar.MINUTE, 0);
                    max_cal.set(Calendar.SECOND, 0);

                    list_stat.clear();
                    //цикл по датам минимальная двигается к максимальной
                    for (min_cal.getTime();min_cal.compareTo(max_cal)<=0;min_cal.add(Calendar.DAY_OF_MONTH, 1)) {
                        Time time_begin = new Time();
                        Time time_end = new Time();
                        Calendar s2 = new GregorianCalendar();
                        time_begin.set(min_cal.getTime().getTime());
                        s2.setTime(min_cal.getTime());
                        s2.add(Calendar.DAY_OF_MONTH, 1);
                        time_end.set(s2.getTime().getTime());

                        Time time_enter = new Time();
                        Time time_exit = new Time();

                        long time_day = 0;

                        //list_stat.add("begin="+time_begin.format("%d-%m-%Y %H:%M:%S")+" end="+time_end.format("%d-%m-%Y %H:%M:%S"));

                        if ( (getEnterDate(time_begin, time_end, db).toMillis(false)>=time_begin.toMillis(false)) &&
                                (getExitDate(time_begin, time_end, db).toMillis(false)>=time_begin.toMillis(false)) ) {
                            time_enter.set(getEnterDate(time_begin, time_end, db));
                            time_exit.set(getExitDate(time_begin, time_end, db));
                            if ( time_exit.toMillis(false) > time_enter.toMillis(false) ) {
                                time_day += time_exit.toMillis(false) - time_enter.toMillis(false);
                            } else {
                                time_day += time_enter.toMillis(false) - time_exit.toMillis(false);
                            }
                            list_stat.add(time_enter.format("%d-%m-%y %H:%M:%S")+"-"+time_exit.format("%H:%M:%S")+timeToString(time_day));
                        }
                    }

                    list_view_stat = (ListView) findViewById(R.id.view_stat);
                    if (adapter_stat != null) {
                        list_view_stat.setAdapter(adapter_stat);
                    }

                    // закрываем подключение к БД
                    db_connect.close();
                } catch (Exception ex) {
                    util.getMessage(getApplicationContext(), "Click button exception " + ex.toString());
                }
            }
        });

        Button view_month = (Button) findViewById(R.id.view_time_month);
        view_month.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // подключаемся к БД
                try {
                    db = db_connect.getWritableDatabase();
                } catch (SQLiteException ex) {
                    db = db_connect.getReadableDatabase();
                    util.getMessage(getApplicationContext(), "Error connect " + ex.toString());
                }

                try {
                    String[] columns = {"id", "date_time_event", "name_event", "note_event"};
                    //name event 'enter' and 'exit'
                    String selection_date = "SELECT MAX(date_time_event), MIN(date_time_event) FROM events ";
                    c_stat = db.rawQuery(selection_date, null);
                    //util.getMessage(getApplicationContext(),"size query ="+c_stat.getCount());

                    Calendar min_cal = new GregorianCalendar();
                    Calendar max_cal = new GregorianCalendar();

                    if (c_stat.moveToNext()) {
                        max_cal.setTimeInMillis(c_stat.getLong(0));
                        //util.getMessage(getApplicationContext(),"max time ="+max_cal.getTime().toString());
                        min_cal.setTimeInMillis(c_stat.getLong(1));
                        //util.getMessage(getApplicationContext(),"min time ="+min_cal.getTime().toString());
                    }

                    //max_cal.set(2016,6,1,10,15,5);
                    //min_cal.set(2016,4,28,9,29,40);

                    min_cal.set(Calendar.DAY_OF_MONTH, 1);
                    min_cal.set(Calendar.HOUR_OF_DAY, 0);
                    min_cal.set(Calendar.MINUTE, 0);
                    min_cal.set(Calendar.SECOND, 0);

                    //max_cal.add(Calendar.MONTH, -1);
                    max_cal.set(Calendar.DAY_OF_MONTH, 1);
                    max_cal.set(Calendar.HOUR_OF_DAY, 0);
                    max_cal.set(Calendar.MINUTE, 0);
                    max_cal.set(Calendar.SECOND, 0);

                    list_stat.clear();
                    //цикл по месяцам минимальная двигается к максимальной
                    for (min_cal.getTime();min_cal.compareTo(max_cal)<=0;min_cal.add(Calendar.MONTH, 1)) {
                        Time date_begin = new Time();
                        Time date_end = new Time();
                        long time_month=0;
                        Calendar s2 = new GregorianCalendar();
                        Calendar min_day = new GregorianCalendar();
                        Calendar max_day = new GregorianCalendar();

                        min_day.setTime(min_cal.getTime());
                        date_begin.set(min_day.getTime().getTime());

                        s2.setTime(min_cal.getTime());
                        s2.add(Calendar.MONTH, 1);
                        s2.add(Calendar.DAY_OF_MONTH, -1);

                        max_day.setTime(s2.getTime());
                        date_end.set(max_day.getTime().getTime());

                        //list_stat.add("datebegin="+date_begin.format("%d-%m-%Y %H:%M:%S")+" dateend="+date_end.format("%d-%m-%Y %H:%M:%S"));

                        //цикл по дням минимальная двигается к максимальной
                        for (min_day.getTime();min_day.compareTo(max_day)<=0;min_day.add(Calendar.DAY_OF_MONTH, 1)) {
                            Time time_begin = new Time();
                            Time time_end = new Time();

                            time_begin.set(min_day.getTime().getTime());

                            s2.setTime(min_day.getTime());
                            s2.add(Calendar.DAY_OF_MONTH, 1);

                            time_end.set(s2.getTime().getTime());

                            Time time_enter = new Time();
                            Time time_exit = new Time();

                            //list_stat.add("timebegin="+time_begin.format("%d-%m-%Y %H:%M:%S")+" timeend="+time_end.format("%d-%m-%Y %H:%M:%S"));

                            if ( (getEnterDate(time_begin, time_end, db).toMillis(false)>=time_begin.toMillis(false)) &&
                                 (getExitDate(time_begin, time_end, db).toMillis(false)>=time_begin.toMillis(false)) ) {
                                time_enter.set(getEnterDate(time_begin, time_end, db));
                                time_exit.set(getExitDate(time_begin, time_end, db));
                                if ( time_exit.toMillis(false) > time_enter.toMillis(false) ) {
                                    time_month += time_exit.toMillis(false) - time_enter.toMillis(false);
                                } else {
                                    time_month += time_enter.toMillis(false) - time_exit.toMillis(false);
                                }
                            }

                            //list_stat.add(" S=" + time_month/1000);
                        }

                        //time.set(0,0,0,1,1-1,1970);
                        //time.set(time.toMillis(false) + time_month);
                        float hour_month = time_month/1000/60/60F;
                        DecimalFormat df = new DecimalFormat("####.##");
                        String sec_str = " S="+df.format(time_month/1000);
                        String hour_str = " H="+df.format(hour_month);
                        list_stat.add(date_begin.format("%m-%Y") + timeToString(time_month) + hour_str);
                    }

                    list_view_stat = (ListView) findViewById(R.id.view_stat);
                    if (adapter_stat != null) {
                        list_view_stat.setAdapter(adapter_stat);
                    }

                    // закрываем подключение к БД
                    db_connect.close();
                } catch (Exception ex) {
                    util.getMessage(getApplicationContext(), "Click button exception " + ex.toString());
                }
            }
        });

    }

    private String timeToString(long time) {
        long time_hour = time/1000/60/60;
        long time_min = (time/1000/60)-time_hour*60;
        long time_sec = (time/1000)-time_hour*3600-time_min*60;
        String time_str = " T=" + time_hour + ":" + (time_min<=9 ? "0"+time_min : time_min) + ":" + (time_sec<=9 ? "0"+time_sec : time_sec);
        return  time_str;
    }

    private Time getEnterDate(Time begintime, Time endtime, SQLiteDatabase db) {
        Time time_res = new Time();

        String selection_enter = "SELECT MIN(date_time_event) FROM events WHERE " +
                                 "date_time_event > " + begintime.toMillis(false) + " AND " +
                                 "date_time_event < " + endtime.toMillis(false) + " AND name_event = 'enter' ";

        c_stat = db.rawQuery(selection_enter, null);

        if (c_stat.moveToNext()) {
            time_res.set(c_stat.getLong(0));
        }
        return time_res;
    }

    private Time getExitDate(Time begintime, Time endtime, SQLiteDatabase db) {
        Time time_res = new Time();

        String selection_exit = "SELECT MAX(date_time_event) FROM events WHERE " +
                "date_time_event > " + begintime.toMillis(false) + " AND " +
                "date_time_event < " + endtime.toMillis(false) + " AND name_event = 'exit' ";

        c_stat = db.rawQuery(selection_exit, null);

        if (c_stat.moveToNext()) {
            time_res.set(c_stat.getLong(0));
        }
        return time_res;
    }

}
