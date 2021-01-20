package com.button_time;

import android.app.Activity;
import android.content.Context;
import android.view.Gravity;
import android.widget.Toast;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by MaKsa on 27.03.16.
 */
public class Utility {

    public void getMessage(Context context, String text) {
        //создаем и отображаем текстовое уведомление
        Toast toast = Toast.makeText(context, text, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

}
