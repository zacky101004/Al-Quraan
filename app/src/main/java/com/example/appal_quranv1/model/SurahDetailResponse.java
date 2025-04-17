package com.example.appal_quranv1.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class SurahDetailResponse {
    @SerializedName("data")
    private List<SurahEdition> editions;

    public List<SurahEdition> getEditions() {
        return editions;
    }

    public void setEditions(List<SurahEdition> editions) {
        this.editions = editions;
    }

    public static class SurahEdition {
        @SerializedName("number")
        private int number;

        @SerializedName("name")
        private String name;

        @SerializedName("englishName")
        private String englishName;

        @SerializedName("englishNameTranslation")
        private String englishNameTranslation;

        @SerializedName("revelationType")
        private String revelationType;

        @SerializedName("numberOfAyahs")
        private int numberOfAyahs;

        @SerializedName("ayahs")
        private List<Ayat> ayahs;

        // Getters dan setters
        public int getNumber() {
            return number;
        }

        public void setNumber(int number) {
            this.number = number;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getEnglishName() {
            return englishName;
        }

        public void setEnglishName(String englishName) {
            this.englishName = englishName;
        }

        public String getEnglishNameTranslation() {
            return englishNameTranslation;
        }

        public void setEnglishNameTranslation(String englishNameTranslation) {
            this.englishNameTranslation = englishNameTranslation;
        }

        public String getRevelationType() {
            return revelationType;
        }

        public void setRevelationType(String revelationType) {
            this.revelationType = revelationType;
        }

        public int getNumberOfAyahs() {
            return numberOfAyahs;
        }

        public void setNumberOfAyahs(int numberOfAyahs) {
            this.numberOfAyahs = numberOfAyahs;
        }

        public List<Ayat> getAyahs() {
            return ayahs;
        }

        public void setAyahs(List<Ayat> ayahs) {
            this.ayahs = ayahs;
        }
    }
}
