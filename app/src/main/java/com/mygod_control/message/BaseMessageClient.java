package com.mygod_control.message;

import android.app.Application;

/**
 * BaseMessageClient <br/>
 * Created by lianghangbin on 2019-02-18.
 */
public abstract class BaseMessageClient {
    private static final String TAG = BaseMessageClient.class.getName();

    /**
     * 初始化
     *
     * @param app
     * @return
     */
    public abstract BaseMessageClient init(Application app);

    /**
     * 发送消息
     * @param msg
     * @return
     */
    public abstract boolean sendMessage(String msg);
}
