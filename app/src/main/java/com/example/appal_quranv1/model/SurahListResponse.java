package com.example.appal_quranv1.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class SurahListResponse {
    @SerializedName("code")
    private int code;

    @SerializedName("status")
    private String status;

    @SerializedName("data")
    private List<Surah> data;

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

    public List<Surah> getData() {
        return data;
    }

    public void setData(List<Surah> data) {
        this.data = data;
    }
}

