package com.mygod_control.message;

import android.app.Application;

import com.google.gson.Gson;
import com.mygod_control.bean.CommendBean;

/**
 * MessageManager <br/>
 * Created by lianghangbin on 2019-02-18.
 */
public class MessageManager {
    private static final String TAG = MessageManager.class.getName();
    private static final MessageManager instance = new MessageManager();
    private Gson gson;
    private BaseMessageClient mqttClient;

    private MessageManager() {
        gson = new Gson();
    }

    public static MessageManager getInstance() {
        return instance;
    }

    public void setUp(Application app) {
        mqttClient = new MqttClient().init(app);
    }

    public void sendMessage(String msg){
        String json = gson.toJson(new CommendBean(msg));
        mqttClient.sendMessage(json);
    }
}
