package com.example.appal_quranv1.model;

import com.google.gson.annotations.SerializedName;

public class JuzResponse {
    @SerializedName("code")
    private int code;

    @SerializedName("status")
    private String status;

    @SerializedName("data")
    private Juz data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Juz getData() {
        return data;
    }

    public void setData(Juz data) {
        this.data = data;
    }
}

