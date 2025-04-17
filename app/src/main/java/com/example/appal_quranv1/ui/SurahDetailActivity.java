package com.example.appal_quranv1.ui;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.appal_quranv1.R;
import com.example.appal_quranv1.data.AppDatabase;
import com.example.appal_quranv1.model.Ayat;
import com.example.appal_quranv1.model.Bookmark;
import com.example.appal_quranv1.model.SurahDetailResponse;
import com.example.appal_quranv1.network.ApiClient;
import com.example.appal_quranv1.network.QuranApiService;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SurahDetailActivity extends AppCompatActivity implements AyatAdapter.AyatListener {

    private RecyclerView rvAyat;
    private AyatAdapter adapter;
    private List<Ayat> ayatList = new ArrayList<>();
    private String surahName;
    private int surahId;
    private ProgressBar progressBar;
    private TextView tvError;
    private ImageButton btnPlayAll, btnPauseResume, btnStop;
    private TextView tvPlayingStatus;
    private MediaPlayer mediaPlayer;
    private int currentAyatIndex = -1;
    private boolean isPlayingAll = false;
    private final ExecutorService executorService = Executors.newFixedThreadPool(2);
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_surah_detail);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        progressBar = findViewById(R.id.progressBar);
        tvError = findViewById(R.id.tvError);
        rvAyat = findViewById(R.id.rvAyat);
        btnPlayAll = findViewById(R.id.btnPlayAll);
        btnPauseResume = findViewById(R.id.btnPauseResume);
        btnStop = findViewById(R.id.btnStop);
        tvPlayingStatus = findViewById(R.id.tvPlayingStatus);
        rvAyat.setLayoutManager(new LinearLayoutManager(this));

        mediaPlayer = new MediaPlayer();

        surahId = getIntent().getIntExtra("surah_id", 1);
        surahName = getIntent().getStringExtra("surah_name");
        toolbar.setTitle(surahName != null ? surahName : "Surah");

        adapter = new AyatAdapter(ayatList, surahName, surahId);
        adapter.setAyatListener(this);
        rvAyat.setAdapter(adapter);

        btnPlayAll.setOnClickListener(v -> startPlayingAll());
        btnPauseResume.setOnClickListener(v -> togglePauseResume());
        btnStop.setOnClickListener(v -> stopPlayingAll());

        loadAyatAndAudio();
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            return activeNetworkInfo != null && activeNetworkInfo.isConnected();
        }
        return false;
    }

    private void loadAyatAndAudio() {
        if (!isNetworkAvailable()) {
            showError("Tidak ada koneksi internet. Silakan periksa jaringan Anda.");
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        tvError.setVisibility(View.GONE);
        rvAyat.setVisibility(View.GONE);

        QuranApiService service = ApiClient.getClient().create(QuranApiService.class);

        // Menggunakan edisi quran-uthmani untuk teks Arab dan en.asad untuk terjemahan, serta ar.alafasy untuk audio
        Log.d("SurahDetailActivity", "Mengambil data untuk Surah ID: " + surahId);
        service.getSurahDetailWithAudio(surahId).enqueue(new Callback<SurahDetailResponse>() {
            @Override
            public void onResponse(Call<SurahDetailResponse> call, Response<SurahDetailResponse> response) {
                if (!isFinishing()) {
                    progressBar.setVisibility(View.GONE);
                    if (response.isSuccessful() && response.body() != null && response.body().getEditions() != null) {
                        List<SurahDetailResponse.SurahEdition> editions = response.body().getEditions();
                        Log.d("SurahDetailActivity", "Jumlah edisi yang diterima: " + editions.size());

                        ayatList.clear();
                        if (editions.size() >= 2) {
                            // Edisi pertama: quran-uthmani (teks Arab)
                            List<Ayat> arabicAyahs = editions.get(0).getAyahs();
                            // Edisi kedua: ar.alafasy (audio)
                            List<Ayat> audioAyahs = editions.get(1).getAyahs();

                            if (arabicAyahs != null && audioAyahs != null && arabicAyahs.size() == audioAyahs.size()) {
                                for (int i = 0; i < arabicAyahs.size(); i++) {
                                    Ayat arabicAyat = arabicAyahs.get(i);
                                    Ayat audioAyat = audioAyahs.get(i);

                                    Ayat combinedAyat = new Ayat();
                                    combinedAyat.setNumberInSurah(arabicAyat.getNumberInSurah());
                                    combinedAyat.setText(arabicAyat.getText());
                                    combinedAyat.setAudio(audioAyat.getAudio());
                                    combinedAyat.setSurah(arabicAyat.getSurah());
                                    // Untuk terjemahan, kita akan tambahkan edisi lain (en.asad) di langkah berikutnya
                                    ayatList.add(combinedAyat);
                                }

                                // Load terjemahan secara terpisah
                                loadTranslation();
                            } else {
                                showError("Data ayat tidak lengkap dari API.");
                            }
                        } else {
                            showError("Edisi data tidak lengkap dari API.");
                        }
                    } else {
                        showError("Gagal mengambil data ayat: " + (response != null ? response.message() : "Unknown error"));
                        Log.e("SurahDetailActivity", "Response gagal: " + (response != null ? response.message() : "Unknown error"));
                    }
                }
            }

            @Override
            public void onFailure(Call<SurahDetailResponse> call, Throwable t) {
                if (!isFinishing()) {
                    progressBar.setVisibility(View.GONE);
                    showError("Gagal mengambil data ayat: " + t.getMessage());
                    Log.e("SurahDetailActivity", "Error fetching ayat: ", t);
                }
            }
        });
    }

    private void loadTranslation() {
        QuranApiService service = ApiClient.getClient().create(QuranApiService.class);
        service.getSurahTranslation(surahId).enqueue(new Callback<SurahDetailResponse>() {
            @Override
            public void onResponse(Call<SurahDetailResponse> call, Response<SurahDetailResponse> response) {
                if (!isFinishing()) {
                    if (response.isSuccessful() && response.body() != null && response.body().getEditions() != null) {
                        List<SurahDetailResponse.SurahEdition> editions = response.body().getEditions();
                        if (!editions.isEmpty()) {
                            List<Ayat> translationAyahs = editions.get(0).getAyahs();
                            if (translationAyahs != null && translationAyahs.size() == ayatList.size()) {
                                for (int i = 0; i < ayatList.size(); i++) {
                                    ayatList.get(i).setTranslation(translationAyahs.get(i).getText());
                                }
                            }
                            rvAyat.setVisibility(View.VISIBLE);
                            adapter.notifyDataSetChanged();
                        } else {
                            showError("Tidak ada terjemahan yang ditemukan.");
                        }
                    } else {
                        showError("Gagal mengambil terjemahan: " + (response != null ? response.message() : "Unknown error"));
                    }
                }
            }

            @Override
            public void onFailure(Call<SurahDetailResponse> call, Throwable t) {
                if (!isFinishing()) {
                    showError("Gagal mengambil terjemahan: " + t.getMessage());
                }
            }
        });
    }

    private void startPlayingAll() {
        if (ayatList.isEmpty()) {
            showError("Tidak ada ayat untuk diputar.");
            return;
        }

        stopPlayingAll();

        currentAyatIndex = 0;
        isPlayingAll = true;
        playCurrentAyat();
        updateAudioControls(true);

        saveLastRead(ayatList.get(0));
    }

    private void playCurrentAyat() {
        if (currentAyatIndex < 0 || currentAyatIndex >= ayatList.size()) {
            stopPlayingAll();
            return;
        }

        Ayat currentAyat = ayatList.get(currentAyatIndex);
        if (currentAyat.getAudio() == null) {
            currentAyatIndex++;
            playCurrentAyat();
            return;
        }

        try {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(currentAyat.getAudio());
            mediaPlayer.prepare();
            mediaPlayer.start();
            tvPlayingStatus.setText("Memutar Ayat " + currentAyat.getNumberInSurah());
            mediaPlayer.setOnCompletionListener(mp -> {
                currentAyatIndex++;
                playCurrentAyat();
                if (currentAyatIndex < ayatList.size()) {
                    saveLastRead(ayatList.get(currentAyatIndex));
                }
            });
        } catch (IOException e) {
            showError("Gagal memutar audio ayat " + currentAyat.getNumberInSurah() + ": " + e.getMessage());
            stopPlayingAll();
        }
    }

    private void togglePauseResume() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            btnPauseResume.setImageResource(R.drawable.ic_play);
            tvPlayingStatus.setText("Dijeda pada Ayat " + (currentAyatIndex + 1));
        } else {
            mediaPlayer.start();
            btnPauseResume.setImageResource(R.drawable.ic_pause);
            tvPlayingStatus.setText("Memutar Ayat " + (currentAyatIndex + 1));
        }
    }

    public void stopPlayingAll() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
        }
        mediaPlayer.reset();
        currentAyatIndex = -1;
        isPlayingAll = false;
        tvPlayingStatus.setText("Tidak ada audio yang diputar");
        updateAudioControls(false);
    }

    private void updateAudioControls(boolean isPlaying) {
        if (isPlaying) {
            btnPlayAll.setVisibility(View.GONE);
            btnPauseResume.setVisibility(View.VISIBLE);
            btnStop.setVisibility(View.VISIBLE);
            btnPauseResume.setImageResource(R.drawable.ic_pause);
        } else {
            btnPlayAll.setVisibility(View.VISIBLE);
            btnPauseResume.setVisibility(View.GONE);
            btnStop.setVisibility(View.GONE);
        }
    }

    private void showError(String message) {
        progressBar.setVisibility(View.GONE);
        tvError.setVisibility(View.VISIBLE);
        tvError.setText(message);
    }

    @Override
    public void saveLastRead(Ayat ayat) {
        if (ayat == null) return;

        executorService.execute(() -> {
            try {
                AppDatabase db = AppDatabase.getInstance(SurahDetailActivity.this);
                db.bookmarkDao().clearLastRead();
                Bookmark lastRead = new Bookmark(
                        surahId,
                        surahName,
                        ayat.getNumberInSurah(),
                        ayat.getText(),
                        ayat.getTranslation() != null ? ayat.getTranslation() : "Terjemahan tidak tersedia"
                );
                lastRead.setLastRead(true);
                lastRead.setFolder("LastRead");
                db.bookmarkDao().insert(lastRead);
            } catch (Exception e) {
                Log.e("SurahDetailActivity", "Error saving last read: ", e);
                mainHandler.post(() -> showError("Gagal menyimpan ayat terakhir: " + e.getMessage()));
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
            mediaPlayer.release();
            mediaPlayer = null;
        }
        executorService.shutdown();
    }
}
