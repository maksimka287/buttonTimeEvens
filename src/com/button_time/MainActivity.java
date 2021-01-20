package com.button_time;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.text.format.Time;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import java.util.LinkedList;
import java.util.List;

public class MainActivity extends Activity {
    private Utility util = new Utility();

    private Connection     db_connect; //соединение с БД
    private SQLiteDatabase db;         //подключение к БД

    private ListView list_events_view;
    private List<CurrEvent> list_events = new LinkedList<CurrEvent>();
    private CurrEvent currEvent;
    private ArrayAdapter<String> adapter_events;
    private List<String> event_titles = new LinkedList<String>();
    private android.text.format.Time time = new android.text.format.Time();

    @Override
	protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
        adapter_events = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, event_titles);

        try {
            //кнопка отображения добавленных событий
            Button enter = (Button) findViewById(R.id.entry);
            enter.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        loadData();
                        list_events_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                if (id >= 0) {
                                    final long index_delete = list_events.get( Integer.decode( Long.toString(id) ) ).getId();
                                    AlertDialog dialog = getDialog(index_delete);
                                    //util.getMessage(getApplicationContext(), "Click id=" + index_delete);
                                    dialog.show();
                                }
                            }
                        });

                    } catch (Exception ex) {
                        util.getMessage(getApplicationContext(), "Click button exception " + ex.toString());
                    }
                }
            });

        } catch(Exception e) {
            util.getMessage(getApplicationContext(), e.toString());
        }

        //кнопка отображения статистики
        Button viewStat = (Button) findViewById(R.id.view_stat);
        viewStat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Statistics.class);
                intent.addFlags(1073741824);
                startActivity(intent);
            }
        });

		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
    }

    private void connectDataBase() {
        try {
            // создаем объект для создания и управления версиями БД
            db_connect = new Connection(this);
            // подключаемся к БД
            db = db_connect.getWritableDatabase();
        } catch (SQLiteException ex) {
            db = db_connect.getReadableDatabase();
            util.getMessage(getApplicationContext(), "Error connect DB " + ex.toString());
        }
    }

    private void loadData() {
        try {
            connectDataBase();
            Cursor c_data;
            String[] columns ={"id","date_time_event","name_event","note_event"};
            //получение всех событий пользователя
            c_data = db.query("events", columns, null, null, null, null, null);

            list_events.clear();

            while(c_data.moveToNext()){
                time = new Time();
                currEvent = new CurrEvent();
                currEvent.setId(c_data.getInt(0));
                time.set(c_data.getLong(1));
                currEvent.setDate_time_event(time);
                currEvent.setName_event(c_data.getString(2));
                currEvent.setNote_event(c_data.getString(3));
                list_events.add(currEvent);
            };

            event_titles.clear();

            for(CurrEvent toevent : list_events) {
                String events="";
                events+="Id="+toevent.getId();
                events+=" Time="+toevent.getDate_time_event().format("%d-%m-%Y %H:%M:%S");
                events+=" Name="+toevent.getName_event();
                events+=" Note="+toevent.getNote_event();
                event_titles.add(events);
            }

            list_events_view = (ListView) findViewById(R.id.list_view_events);
            if (adapter_events != null) {
                list_events_view.setAdapter(adapter_events);
            }

            // закрываем подключение к БД
            db_connect.close();
        } catch (Exception ex) {
            util.getMessage(getApplicationContext(), "Exception load data " + ex.toString());
        }
    }

    private AlertDialog getDialog(final long index_delete) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Удаление события");
        builder.setMessage("Уверены, что хотите удалить событие?");
        builder.setCancelable(true);
        builder.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss(); // Отпускает диалоговое окно
            }
        });
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() { // Кнопка ОК
            @Override
            public void onClick(DialogInterface dialog, int which) {
                connectDataBase();
                dialog.dismiss(); // Отпускает диалоговое окно
                deleteEvent(index_delete, db);
                db_connect.close();
                loadData();
            }
        });
        return builder.create();
    }

    private void deleteEvent(long id, SQLiteDatabase db) {
        try {
            String where_text = " id= " + id;
            db.delete("events", where_text, null);
        } catch (Exception ex) {
            util.getMessage(getApplicationContext(), "Delete exception " + ex.toString());
        }
    }

}
