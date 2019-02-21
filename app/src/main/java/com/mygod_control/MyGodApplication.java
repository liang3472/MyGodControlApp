package com.mygod_control;

import android.app.Application;
import android.util.Log;

import com.mygod_control.message.MessageManager;

/**
 * MyGodApplication <br/>
 * Created by lianghangbin on 2019-02-18.
 */
public class MyGodApplication extends Application {

    private static final String TAG = MyGodApplication.class.getName();

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "setup mqtt client");
        MessageManager.getInstance().setUp(this);
    }
}
