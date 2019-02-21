package com.mygod_control.bean;

import com.google.gson.annotations.SerializedName;

/**
 * CommendBean <br/>
 * Created by lianghangbin on 2019-02-18.
 */
public class CommendBean {
    @SerializedName("commend")
    private String commend;

    public CommendBean(String commend) {
        this.commend = commend;
    }

    public String getCommend() {
        return commend;
    }

    public void setCommend(String commend) {
        this.commend = commend;
    }
}
