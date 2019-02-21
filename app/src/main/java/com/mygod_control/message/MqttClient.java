package com.mygod_control.message;

import android.app.Application;
import android.provider.Settings;
import android.util.Log;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.DisconnectedBufferOptions;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

/**
 * MqttClient <br/>
 * Created by lianghangbin on 2019-02-18.
 */
public class MqttClient extends BaseMessageClient {
    private static final String TAG = MqttClient.class.getName();
    private static final String SERVER_URI = "tcp://69.171.79.169:1883";
    private static final String TOPIC = "MyGodTopic";
    private MqttAndroidClient mqttAndroidClient;
    private Application mApp;

    @Override
    public BaseMessageClient init(Application app) {
        mApp = app;
        initMqttConfig(app);
        return this;
    }

    @Override
    public boolean sendMessage(String msg) {
        try {
            MqttMessage message = new MqttMessage();
            message.setPayload(msg.getBytes());
            mqttAndroidClient.publish(TOPIC, message);
            Log.d(TAG, "Message Published");
            if (!mqttAndroidClient.isConnected()) {
                Log.d(TAG, mqttAndroidClient.getBufferedMessageCount() + " messages in buffer.");
            }
        } catch (MqttException e) {
            Log.e(TAG, "Error Publishing: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    private void initMqttConfig(Application app) {
        if (app == null) {
            Log.e(TAG, "init mqtt client fail");
            return;
        }
        Log.d(TAG, "init mqtt client success");

        String androidId = Settings.Secure.getString(app.getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        mqttAndroidClient = new MqttAndroidClient(app, SERVER_URI, androidId);
        mqttAndroidClient.setCallback(new MqttCallbackExtended() {
            @Override
            public void connectComplete(boolean reconnect, String serverURI) {

                if (reconnect) {
                    Log.d(TAG, "Reconnected to : " + serverURI);
                } else {
                    Log.d(TAG, "Connected to: " + serverURI);
                }
            }

            @Override
            public void connectionLost(Throwable cause) {
                Log.e(TAG, "The Connection was lost.");
            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                Log.e(TAG, "Incoming message: " + new String(message.getPayload()));
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }
        });

        MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();
        mqttConnectOptions.setAutomaticReconnect(true);
        mqttConnectOptions.setCleanSession(false);

        try {
            mqttAndroidClient.connect(mqttConnectOptions, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    DisconnectedBufferOptions disconnectedBufferOptions = new DisconnectedBufferOptions();
                    disconnectedBufferOptions.setBufferEnabled(true);
                    disconnectedBufferOptions.setBufferSize(100);
                    disconnectedBufferOptions.setPersistBuffer(false);
                    disconnectedBufferOptions.setDeleteOldestMessages(false);
                    mqttAndroidClient.setBufferOpts(disconnectedBufferOptions);
                    Log.d(TAG, "connect to: " + SERVER_URI);
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Log.e(TAG, "Failed to connect to: " + SERVER_URI);
                }
            });


        } catch (MqttException ex) {
            Log.e(TAG, "oops---->!!");
            ex.printStackTrace();
        }
    }
}
