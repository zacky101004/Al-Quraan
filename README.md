

**📖 Al-Quran App**  
Versi 1.0 – Rilis April 2025

Al-Quran App adalah aplikasi Android yang sederhana dan modern untuk membantu pengguna membaca Al-Quran secara praktis. Aplikasi ini memungkinkan pengguna untuk menjelajahi daftar Surah dan Juz, menyimpan ayat favorit sebagai bookmark, dan mendapatkan pengingat harian untuk membaca Al-Quran. Data Al-Quran diambil secara real-time dari API Al-Quran Cloud.


**🌟 Fitur Utama:**

- **Daftar Surah Lengkap:**  
  Menampilkan seluruh 114 Surah dengan nomor, nama, dan jumlah ayat.

- **Jelajah Juz (1–30):**  
  Menampilkan informasi lengkap berdasarkan pembagian Juz, termasuk ayat awal dan akhir.

- **Bookmark Ayat Favorit:**  
  Simpan ayat pilihan agar bisa diakses kembali secara offline dengan menggunakan Room Database.

- **Navigasi Tab yang Praktis:**  
  Navigasi melalui tab Surah, Juz, dan Bookmark menggunakan ViewPager2.

- **Notifikasi Harian:**  
  Pengingat otomatis untuk membaca Al-Quran setiap hari, diatur menggunakan WorkManager.

- **Splash Screen Animatif:**  
  Tampilan awal dengan animasi fade-in untuk pengalaman pengguna yang lebih baik.

- **Desain Elegan dan Modern:**  
  Menggunakan Material Design dan CardView untuk tampilan yang bersih dan nyaman digunakan.

---

**⚙️ Spesifikasi Teknis:**

- **Min SDK:** 21 (Android 5.0 Lollipop)  
- **Target SDK:** 34 (Android 14)  
- **Bahasa:** Java (versi 17)  
- **IDE:** Android Studio Hedgehog atau versi terbaru

---

**🚀 Langkah Setup Aplikasi:**

1. Clone repository:  
   `git clone https://github.com/zacky101004/Al-Quraan.git`  
   `cd al-quran-app`

2. Buka di Android Studio:  
   - Pilih *Open an Existing Project*  
   - Arahkan ke folder proyek

3. Sinkronkan dengan Gradle:  
   - Klik *Sync Project with Gradle Files*

4. Jalankan Aplikasi:  
   - Gunakan perangkat Android atau emulator  
   - Klik *Run* untuk menjalankan aplikasi

---

**📦 Dependensi yang Digunakan:**

- **AppCompat:** Kompatibilitas aktivitas  
- **Retrofit & Gson:** Koneksi API dan parsing JSON  
- **RecyclerView:** Menampilkan daftar Surah dan Juz  
- **Material Design:** Komponen UI modern  
- **Room Database:** Menyimpan bookmark offline  
- **WorkManager:** Notifikasi harian otomatis  
- **ViewPager2:** Navigasi berbasis tab  
- **CardView:** Tampilan daftar yang elegan

---

**🗂️ Struktur Proyek:**

- `ui/`: Aktivitas & Fragmen (Splash, Main, Surah, Juz, Bookmark)  
- `model/`: Kelas model untuk Surah, Juz, dan Ayat  
- `network/`: Konfigurasi Retrofit dan service API  
- `res/layout/`: Layout XML  
- `res/drawable/`: Aset gambar  
- `res/anim/`: File animasi

---

**🌐 API:**

Menggunakan **Al-Quran Cloud API** dengan base URL:  
`https://api.alquran.cloud/v1/`  
Dikonfigurasi melalui file `build.gradle`:
```gradle
buildConfigField("String", "BASE_URL", "\"https://api.alquran.cloud/v1/\"")
```

---

**🤝 Kontribusi:**

Kami terbuka terhadap kontribusi pengembangan aplikasi ini. Langkah-langkah kontribusi:

1. Fork repository  
2. Buat branch baru:  
   `git checkout -b fitur-baru`  
3. Commit perubahan:  
   `git commit -m "Menambahkan fitur baru"`  
4. Push dan buat Pull Request

---

**📜 Lisensi:**  
Aplikasi ini dirilis di bawah **MIT License**

---

**📬 Kontak:**

- Email: zackymhd2@email.com  
- GitHub: github.com/zacky101004

---

**Terima kasih telah menggunakan Al-Quran App.**  
Semoga bermanfaat dalam menemani aktivitas ibadah dan bacaan harian Anda. 🌙

---

Kalau kamu butuh versi Markdown atau Word juga, tinggal bilang ya!
