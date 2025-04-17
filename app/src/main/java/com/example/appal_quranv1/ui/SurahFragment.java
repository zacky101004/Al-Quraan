package com.example.appal_quranv1.ui;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.appal_quranv1.R;
import com.example.appal_quranv1.network.ApiClient;
import com.example.appal_quranv1.network.QuranApiService;
import com.example.appal_quranv1.model.SurahListResponse;
import com.example.appal_quranv1.model.Surah;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SurahFragment extends Fragment {

    private RecyclerView rvSurah;
    private SurahAdapter adapter;
    private List<Surah> surahList = new ArrayList<>();
    private TextView tvError;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_surah, container, false);
        rvSurah = view.findViewById(R.id.rvSurah);
        tvError = view.findViewById(R.id.tvError);
        rvSurah.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new SurahAdapter(surahList);
        rvSurah.setAdapter(adapter);
        loadSurahs();
        return view;
    }

    private boolean isNetworkAvailable() {
        if (getContext() == null) return false;
        ConnectivityManager connectivityManager = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            return activeNetworkInfo != null && activeNetworkInfo.isConnected();
        }
        return false;
    }

    private void loadSurahs() {
        if (!isNetworkAvailable()) {
            showError("Tidak ada koneksi internet. Silakan periksa jaringan Anda.");
            return;
        }

        rvSurah.setVisibility(View.GONE);
        tvError.setVisibility(View.GONE);

        QuranApiService service = ApiClient.getClient().create(QuranApiService.class);
        service.getAllSurahs().enqueue(new Callback<SurahListResponse>() {
            @Override
            public void onResponse(Call<SurahListResponse> call, Response<SurahListResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    surahList.clear();
                    surahList.addAll(response.body().getData());
                    adapter.notifyDataSetChanged();
                    rvSurah.setVisibility(View.VISIBLE);
                } else {
                    showError("Gagal mengambil data surah: " + (response != null ? response.message() : "Unknown error"));
                }
            }

            @Override
            public void onFailure(Call<SurahListResponse> call, Throwable t) {
                showError("Gagal mengambil data surah: " + t.getMessage());
                Log.e("SurahFragment", "Error fetching surahs: ", t);
            }
        });
    }

    private void showError(String message) {
        rvSurah.setVisibility(View.GONE);
        tvError.setVisibility(View.VISIBLE);
        tvError.setText(message);
    }
}