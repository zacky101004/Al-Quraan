package com.example.appal_quranv1.ui;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.viewpager2.widget.ViewPager2;
import androidx.fragment.app.Fragment;
import com.example.appal_quranv1.R;
import com.example.appal_quranv1.notification.DailyReminderWorker;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ViewPager2 viewPager;
    private TabLayout tabLayout;
    private static final int NOTIFICATION_PERMISSION_REQUEST_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Setup Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Al-Quran App");
        }

        // Setup Tabs
        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);

        // Inisialisasi List Fragment
        List<Fragment> fragments = new ArrayList<>();
        fragments.add(new SurahFragment());
        fragments.add(new JuzFragment());
        fragments.add(new BookmarkFragment());

        // Setup Adapter untuk ViewPager
        ViewPagerAdapter adapter = new ViewPagerAdapter(this, fragments);
        viewPager.setAdapter(adapter);

        // Hubungkan TabLayout dengan ViewPager menggunakan TabLayoutMediator
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            switch (position) {
                case 0:
                    tab.setText("Surah");
                    tab.setIcon(R.drawable.ic_surah);
                    break;
                case 1:
                    tab.setText("Juz");
                    tab.setIcon(R.drawable.ic_juz);
                    break;
                case 2:
                    tab.setText("Bookmark");
                    tab.setIcon(R.drawable.ic_bookmark);
                    break;
            }
        }).attach();

        // Atur agar ViewPager tidak bisa digeser secara manual (opsional)
        viewPager.setUserInputEnabled(true);

        // Izin Notifikasi untuk Android 13 (Tiramisu) ke atas
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, NOTIFICATION_PERMISSION_REQUEST_CODE);
            } else {
                // Jika izin sudah diberikan, langsung jadwalkan notifikasi
                scheduleDailyReminder();
            }
        } else {
            // Untuk versi Android di bawah Tiramisu, langsung jadwalkan notifikasi
            scheduleDailyReminder();
        }
    }

    // Menangani hasil dari permintaan izin
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == NOTIFICATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Izin diberikan, jadwalkan notifikasi
                scheduleDailyReminder();
                Toast.makeText(this, "Izin notifikasi diberikan", Toast.LENGTH_SHORT).show();
            } else {
                // Izin ditolak, beri tahu pengguna
                Toast.makeText(this, "Izin notifikasi ditolak. Notifikasi harian tidak akan berfungsi.", Toast.LENGTH_LONG).show();
            }
        }
    }

    // Method untuk menjadwalkan notifikasi harian
    private void scheduleDailyReminder() {
        DailyReminderWorker.scheduleDailyReminder(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Bersihkan resource jika diperlukan
        viewPager.setAdapter(null);
    }
}