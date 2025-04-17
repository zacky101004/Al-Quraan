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
import com.example.appal_quranv1.model.Juz;
import com.example.appal_quranv1.model.JuzResponse;
import com.example.appal_quranv1.network.ApiClient;
import com.example.appal_quranv1.network.QuranApiService;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class JuzFragment extends Fragment {

    private RecyclerView rvJuz;
    private JuzAdapter adapter;
    private List<Juz> juzList = new ArrayList<>();
    private TextView tvError;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_juz, container, false);
        rvJuz = view.findViewById(R.id.rvJuz);
        tvError = view.findViewById(R.id.tvError);
        rvJuz.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new JuzAdapter(juzList);
        rvJuz.setAdapter(adapter);
        loadJuz();
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

    private void loadJuz() {
        if (!isNetworkAvailable()) {
            showError("Tidak ada koneksi internet. Silakan periksa jaringan Anda.");
            return;
        }

        rvJuz.setVisibility(View.GONE);
        tvError.setVisibility(View.GONE);

        QuranApiService service = ApiClient.getClient().create(QuranApiService.class);

        for (int juzNumber = 1; juzNumber <= 30; juzNumber++) {
            Log.d("JuzFragment", "Mengambil data untuk Juz: " + juzNumber);
            service.getJuz(juzNumber).enqueue(new Callback<JuzResponse>() {
                @Override
                public void onResponse(Call<JuzResponse> call, Response<JuzResponse> response) {
                    if (isAdded() && getActivity() != null) {
                        if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                            Juz juz = response.body().getData();
                            if (juz != null) {
                                juzList.add(juz);
                                Collections.sort(juzList, (j1, j2) -> Integer.compare(j1.getNumber(), j2.getNumber()));
                                adapter.notifyDataSetChanged();
                                rvJuz.setVisibility(View.VISIBLE);
                            } else {
                                showError("Data Juz tidak ditemukan.");
                            }
                        } else {
                            showError("Gagal mengambil data Juz: " + (response != null ? response.message() : "Unknown error"));
                            Log.e("JuzFragment", "Response gagal: " + (response != null ? response.message() : "Unknown error"));
                        }
                    }
                }

                @Override
                public void onFailure(Call<JuzResponse> call, Throwable t) {
                    if (isAdded() && getActivity() != null) {
                        showError("Gagal mengambil data Juz: " + t.getMessage());
                        Log.e("JuzFragment", "Error fetching Juz: ", t);
                    }
                }
            });
        }
    }

    private void showError(String message) {
        if (isAdded() && getActivity() != null) {
            rvJuz.setVisibility(View.GONE);
            tvError.setVisibility(View.VISIBLE);
            tvError.setText(message);
        }
    }
}