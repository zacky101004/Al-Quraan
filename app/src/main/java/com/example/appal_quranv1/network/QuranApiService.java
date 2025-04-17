package com.example.appal_quranv1.network;

import com.example.appal_quranv1.model.JuzResponse;
import com.example.appal_quranv1.model.SurahDetailResponse;
import com.example.appal_quranv1.model.SurahListResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface QuranApiService {

    @GET("surah")
    Call<SurahListResponse> getAllSurahs();

    @GET("surah/{surahId}/editions/quran-uthmani,ar.alafasy")
    Call<SurahDetailResponse> getSurahDetailWithAudio(@Path("surahId") int surahId);

    @GET("juz/{juzId}/quran-uthmani")
    Call<JuzResponse> getJuz(@Path("juzId") int juzId);

    @GET("surah/{surahId}/editions/id.indonesian")
    Call<SurahDetailResponse> getSurahTranslation(@Path("surahId") int surahId);

    @GET("juz/{juzId}/ar.alafasy")
    Call<JuzResponse> getJuzAudio(@Path("juzId") int juzId);

    @GET("juz/{juzId}/id.indonesian")
    Call<JuzResponse> getJuzTranslation(@Path("juzId") int juzId);
}