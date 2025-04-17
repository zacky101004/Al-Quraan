package com.example.appal_quranv1.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import androidx.appcompat.app.AppCompatActivity;
import com.example.appal_quranv1.R;

public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_DURATION = 3000; // Durasi splash screen dalam milidetik (3 detik)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Transisi ke MainActivity setelah SPLASH_DURATION
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
            startActivity(intent);
            finish(); // Tutup SplashActivity agar tidak bisa kembali ke sini
        }, SPLASH_DURATION);
    }
}