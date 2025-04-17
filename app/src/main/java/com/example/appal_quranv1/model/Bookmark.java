package com.example.appal_quranv1.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "bookmarks")
public class Bookmark {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private int surahId;
    private String surahName;
    private int ayatNumber;
    private String ayatText;
    private String translation;
    private String folder;
    private boolean isLastRead;

    public Bookmark(int surahId, String surahName, int ayatNumber, String ayatText, String translation) {
        this.surahId = surahId;
        this.surahName = surahName != null ? surahName : "Unknown";
        this.ayatNumber = ayatNumber;
        this.ayatText = ayatText != null ? ayatText : "";
        this.translation = translation != null ? translation : "";
        this.folder = "Default";
        this.isLastRead = false;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSurahId() {
        return surahId;
    }

    public void setSurahId(int surahId) {
        this.surahId = surahId;
    }

    public String getSurahName() {
        return surahName;
    }

    public void setSurahName(String surahName) {
        this.surahName = surahName;
    }

    public int getAyatNumber() {
        return ayatNumber;
    }

    public void setAyatNumber(int ayatNumber) {
        this.ayatNumber = ayatNumber;
    }

    public String getAyatText() {
        return ayatText;
    }

    public void setAyatText(String ayatText) {
        this.ayatText = ayatText;
    }

    public String getTranslation() {
        return translation;
    }

    public void setTranslation(String translation) {
        this.translation = translation;
    }

    public String getFolder() {
        return folder;
    }

    public void setFolder(String folder) {
        this.folder = folder;
    }

    public boolean isLastRead() {
        return isLastRead;
    }

    public void setLastRead(boolean lastRead) {
        isLastRead = lastRead;
    }
}