package com.example.appal_quranv1.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class Juz {
    @SerializedName("number")
    private int number;

    @SerializedName("ayahs")
    private List<Ayat> ayahs;

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public List<Ayat> getAyahs() {
        return ayahs;
    }

    public void setAyahs(List<Ayat> ayahs) {
        this.ayahs = ayahs;
    }
}