plugins {
    id("com.android.application")
}

android {
    namespace = "com.example.appal_quranv1"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.appal_quranv1"
        minSdk = 21
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        // ✅ Gunakan BASE_URL dari https://alquran.cloud/api
        buildConfigField("String", "BASE_URL", "\"https://api.alquran.cloud/v1/\"")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    buildFeatures {
        buildConfig = true // ✅ WAJIB untuk gunakan buildConfigField
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

dependencies {
    // ✅ AppCompat untuk AppCompatActivity
    implementation("androidx.appcompat:appcompat:1.6.1")

    // ✅ Retrofit & Gson untuk koneksi API
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")

    // ✅ RecyclerView untuk daftar Surah
    implementation("androidx.recyclerview:recyclerview:1.3.2")

    // ✅ Material Design untuk komponen UI modern
    implementation("com.google.android.material:material:1.11.0")

    // ✅ WorkManager untuk notifikasi harian
    implementation("androidx.work:work-runtime:2.9.0")

    // ✅ Room Database (untuk fitur bookmark)
    implementation("androidx.room:room-runtime:2.6.1")
    annotationProcessor("androidx.room:room-compiler:2.6.1")

    // ✅ ViewPager2 untuk navigasi tab (Surah, Juz, Bookmark)
    implementation("androidx.viewpager2:viewpager2:1.0.0")

    // ✅ CardView untuk item Juz
    implementation("androidx.cardview:cardview:1.0.0")
}