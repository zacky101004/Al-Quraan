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
import com.example.appal_quranv1.model.Juz;
import com.example.appal_quranv1.model.JuzResponse;
import com.example.appal_quranv1.model.SurahDetailResponse;
import com.example.appal_quranv1.network.ApiClient;
import com.example.appal_quranv1.network.QuranApiService;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class JuzDetailActivity extends AppCompatActivity implements AyatAdapter.AyatListener {

    private RecyclerView rvAyat;
    private AyatAdapter adapter;
    private List<Ayat> ayatList = new ArrayList<>();
    private int juzNumber;
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
        setContentView(R.layout.activity_juz_detail);

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

        juzNumber = getIntent().getIntExtra("juz_number", 1);
        toolbar.setTitle("Juz " + juzNumber);

        adapter = new AyatAdapter(ayatList, "Juz " + juzNumber, juzNumber);
        adapter.setAyatListener(this);
        rvAyat.setAdapter(adapter);

        btnPlayAll.setOnClickListener(v -> startPlayingAll());
        btnPauseResume.setOnClickListener(v -> togglePauseResume());
        btnStop.setOnClickListener(v -> stopPlayingAll());

        loadJuzDetail();
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            return activeNetworkInfo != null && activeNetworkInfo.isConnected();
        }
        return false;
    }

    private void loadJuzDetail() {
        if (!isNetworkAvailable()) {
            showError("Tidak ada koneksi internet. Silakan periksa jaringan Anda dan coba lagi.");
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        tvError.setVisibility(View.GONE);
        rvAyat.setVisibility(View.GONE);

        QuranApiService service = ApiClient.getClient().create(QuranApiService.class);
        Log.d("JuzDetailActivity", "Mengambil data untuk Juz: " + juzNumber);
        service.getJuz(juzNumber).enqueue(new Callback<JuzResponse>() {
            @Override
            public void onResponse(Call<JuzResponse> call, Response<JuzResponse> response) {
                if (!isFinishing()) {
                    progressBar.setVisibility(View.GONE);
                    if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                        Juz juz = response.body().getData();
                        List<Ayat> arabicAyahs = juz.getAyahs();
                        if (arabicAyahs != null && !arabicAyahs.isEmpty()) {
                            ayatList.clear();
                            ayatList.addAll(arabicAyahs);
                            rvAyat.setVisibility(View.VISIBLE);
                            adapter.notifyDataSetChanged();
                            loadAudioForJuz();
                            loadTranslationForJuz();
                        } else {
                            showError("Tidak ada ayat yang ditemukan untuk Juz ini. Silakan coba lagi.");
                        }
                    } else {
                        String errorMessage = response != null ? response.message() : "Unknown error";
                        showError("Gagal mengambil data Juz: " + errorMessage + ". Silakan coba lagi.");
                        Log.e("JuzDetailActivity", "Response gagal: " + errorMessage);
                        if (response != null && response.errorBody() != null) {
                            try {
                                Log.e("JuzDetailActivity", "Error body: " + response.errorBody().string());
                            } catch (IOException e) {
                                Log.e("JuzDetailActivity", "Error reading error body: ", e);
                            }
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<JuzResponse> call, Throwable t) {
                if (!isFinishing()) {
                    progressBar.setVisibility(View.GONE);
                    showError("Gagal mengambil data Juz: " + t.getMessage() + ". Silakan coba lagi.");
                    Log.e("JuzDetailActivity", "Error fetching Juz: ", t);
                }
            }
        });
    }

    private void loadAudioForJuz() {
        QuranApiService service = ApiClient.getClient().create(QuranApiService.class);
        service.getJuzAudio(juzNumber).enqueue(new Callback<JuzResponse>() {
            @Override
            public void onResponse(Call<JuzResponse> call, Response<JuzResponse> response) {
                if (!isFinishing() && response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    Juz juz = response.body().getData();
                    List<Ayat> audioAyahs = juz.getAyahs();
                    Log.d("JuzDetailActivity", "Jumlah ayat yang diterima (audio): " + (audioAyahs != null ? audioAyahs.size() : 0));
                    if (audioAyahs != null && audioAyahs.size() == ayatList.size()) {
                        for (int i = 0; i < ayatList.size(); i++) {
                            if (audioAyahs.get(i).getAudio() != null) {
                                ayatList.get(i).setAudio(audioAyahs.get(i).getAudio());
                            } else {
                                Log.w("JuzDetailActivity", "Audio null untuk ayat " + (i + 1));
                            }
                        }
                        adapter.notifyDataSetChanged();
                    } else {
                        Log.w("JuzDetailActivity", "Jumlah ayat audio tidak sesuai: " +
                                (audioAyahs != null ? audioAyahs.size() : 0) + " vs " + ayatList.size());
                        showError("Gagal mengambil audio untuk Juz ini: Data tidak sesuai. Silakan coba lagi.");
                    }
                } else {
                    String errorMessage = response != null ? response.message() : "Unknown error";
                    Log.e("JuzDetailActivity", "Gagal mengambil audio Juz: " + errorMessage);
                    showError("Gagal mengambil audio untuk Juz ini: " + errorMessage + ". Silakan coba lagi.");
                }
            }

            @Override
            public void onFailure(Call<JuzResponse> call, Throwable t) {
                if (!isFinishing()) {
                    Log.e("JuzDetailActivity", "Error fetching audio for Juz: ", t);
                    showError("Gagal mengambil audio untuk Juz ini: " + t.getMessage() + ". Silakan coba lagi.");
                }
            }
        });
    }

    private void loadTranslationForJuz() {
        Set<Integer> surahNumbers = new HashSet<>();
        for (Ayat ayat : ayatList) {
            if (ayat.getSurah() != null) {
                surahNumbers.add(ayat.getSurah().getNumber());
            }
        }

        if (surahNumbers.isEmpty()) {
            Log.w("JuzDetailActivity", "Tidak ada Surah yang ditemukan untuk Juz ini.");
            showError("Tidak ada Surah yang ditemukan untuk Juz ini. Silakan coba lagi.");
            return;
        }

        QuranApiService service = ApiClient.getClient().create(QuranApiService.class);
        for (int surahNumber : surahNumbers) {
            Log.d("JuzDetailActivity", "Mengambil terjemahan untuk Surah: " + surahNumber);
            service.getSurahTranslation(surahNumber).enqueue(new Callback<SurahDetailResponse>() {
                @Override
                public void onResponse(Call<SurahDetailResponse> call, Response<SurahDetailResponse> response) {
                    if (!isFinishing()) {
                        if (response.isSuccessful() && response.body() != null && response.body().getEditions() != null) {
                            List<SurahDetailResponse.SurahEdition> editions = response.body().getEditions();
                            if (!editions.isEmpty()) {
                                List<Ayat> translationAyahs = editions.get(0).getAyahs();
                                Log.d("JuzDetailActivity", "Jumlah ayat terjemahan untuk Surah " + surahNumber + ": " +
                                        (translationAyahs != null ? translationAyahs.size() : 0));
                                if (translationAyahs != null) {
                                    for (Ayat ayat : ayatList) {
                                        if (ayat.getSurah() != null && ayat.getSurah().getNumber() == surahNumber) {
                                            for (Ayat translationAyat : translationAyahs) {
                                                if (translationAyat.getNumberInSurah() == ayat.getNumberInSurah()) {
                                                    ayat.setTranslation(translationAyat.getText());
                                                    break;
                                                }
                                            }
                                        }
                                    }
                                    adapter.notifyDataSetChanged();
                                } else {
                                    Log.w("JuzDetailActivity", "Tidak ada terjemahan untuk Surah " + surahNumber);
                                }
                            } else {
                                Log.w("JuzDetailActivity", "Tidak ada edisi terjemahan untuk Surah " + surahNumber);
                            }
                        } else {
                            String errorMessage = response != null ? response.message() : "Unknown error";
                            Log.e("JuzDetailActivity", "Gagal mengambil terjemahan untuk Surah " + surahNumber + ": " + errorMessage);
                            showError("Gagal mengambil terjemahan untuk Surah " + surahNumber + ": " + errorMessage + ". Silakan coba lagi.");
                        }
                    }
                }

                @Override
                public void onFailure(Call<SurahDetailResponse> call, Throwable t) {
                    if (!isFinishing()) {
                        Log.e("JuzDetailActivity", "Error fetching translation for Surah " + surahNumber + ": ", t);
                        showError("Gagal mengambil terjemahan untuk Surah " + surahNumber + ": " + t.getMessage() + ". Silakan coba lagi.");
                    }
                }
            });
        }
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
            Log.w("JuzDetailActivity", "Audio tidak tersedia untuk ayat " + currentAyat.getNumberInSurah());
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
            Log.e("JuzDetailActivity", "Gagal memutar audio ayat " + currentAyat.getNumberInSurah() + ": ", e);
            showError("Gagal memutar audio ayat " + currentAyat.getNumberInSurah() + ": " + e.getMessage());
            currentAyatIndex++;
            playCurrentAyat();
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
        tvError.setOnClickListener(v -> {
            tvError.setVisibility(View.GONE);
            loadJuzDetail();
        });
    }

    @Override
    public void saveLastRead(Ayat ayat) {
        if (ayat == null) return;

        executorService.execute(() -> {
            try {
                AppDatabase db = AppDatabase.getInstance(JuzDetailActivity.this);
                db.bookmarkDao().clearLastRead();
                Bookmark lastRead = new Bookmark(
                        ayat.getSurah() != null ? ayat.getSurah().getNumber() : 0,
                        ayat.getSurah() != null ? ayat.getSurah().getName() : "Unknown",
                        ayat.getNumberInSurah(),
                        ayat.getText() != null ? ayat.getText() : "",
                        ayat.getTranslation() != null ? ayat.getTranslation() : "Terjemahan tidak tersedia"
                );
                lastRead.setLastRead(true);
                lastRead.setFolder("LastRead");
                db.bookmarkDao().insert(lastRead);
            } catch (Exception e) {
                Log.e("JuzDetailActivity", "Error saving last read: ", e);
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