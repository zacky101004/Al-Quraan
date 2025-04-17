package com.example.appal_quranv1.model;

import com.google.gson.annotations.SerializedName;

public class Ayat {
    @SerializedName("numberInSurah")
    private int numberInSurah;

    @SerializedName("text")
    private String text;

    @SerializedName("audio")
    private String audio;

    @SerializedName("surah")
    private Surah surah;

    private String translation; // Untuk menyimpan terjemahan dari edisi lain

    // Getters dan setters
    public int getNumberInSurah() {
        return numberInSurah;
    }

    public void setNumberInSurah(int numberInSurah) {
        this.numberInSurah = numberInSurah;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getAudio() {
        return audio;
    }

    public void setAudio(String audio) {
        this.audio = audio;
    }

    public Surah getSurah() {
        return surah;
    }

    public void setSurah(Surah surah) {
        this.surah = surah;
    }

    public String getTranslation() {
        return translation;
    }

    public void setTranslation(String translation) {
        this.translation = translation;
    }
}