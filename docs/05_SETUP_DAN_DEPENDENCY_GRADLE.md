# PANDUAN DEPENDENSI GRADLE & KONFIGURASI SUPABASE

Dokumen ini memuat konfigurasi dependensi Android, version catalog (`libs.versions.toml`), `build.gradle.kts`, izin aplikasi (`AndroidManifest.xml`), dan inisialisasi Supabase Client untuk aplikasi **StudentLife**.

---

## 1. Konfigurasi `gradle/libs.versions.toml`

Tambahkan versi dan library berikut ke dalam file `gradle/libs.versions.toml` proyek Anda:

```toml
[versions]
# Version yang sudah ada di proyek
agp = "9.3.3"
coreKtx = "1.10.1"
junit = "4.13.2"
junitVersion = "1.1.5"
espressoCore = "3.5.1"
lifecycleRuntimeKtx = "2.8.7"
activityCompose = "1.9.3"
kotlin = "2.2.10"
composeBom = "2026.02.01"

# Tambahan untuk Arsitektur Tim StudentLife
navigationCompose = "2.8.5"
lifecycleViewModelCompose = "2.8.7"
kotlinxSerialization = "1.7.3"
supabase = "3.0.3"
ktor = "3.0.3"
coil = "2.7.0"
materialIconsExtended = "1.7.6"

[libraries]
# Libraries default
androidx-core-ktx = { group = "androidx.core", name = "core-ktx", version.ref = "coreKtx" }
junit = { group = "junit", name = "junit", version.ref = "junit" }
androidx-junit = { group = "androidx.test.ext", name = "junit", version.ref = "junitVersion" }
androidx-espresso-core = { group = "androidx.test.espresso", name = "espresso-core", version.ref = "espressoCore" }
androidx-lifecycle-runtime-ktx = { group = "androidx.lifecycle", name = "lifecycle-runtime-ktx", version.ref = "lifecycleRuntimeKtx" }
androidx-activity-compose = { group = "androidx.activity", name = "activity-compose", version.ref = "activityCompose" }
androidx-compose-bom = { group = "androidx.compose", name = "compose-bom", version.ref = "composeBom" }
androidx-compose-ui = { group = "androidx.compose.ui", name = "ui" }
androidx-compose-ui-graphics = { group = "androidx.compose.ui", name = "ui-graphics" }
androidx-compose-ui-tooling = { group = "androidx.compose.ui", name = "ui-tooling" }
androidx-compose-ui-tooling-preview = { group = "androidx.compose.ui", name = "ui-tooling-preview" }
androidx-compose-ui-test-manifest = { group = "androidx.compose.ui", name = "ui-test-manifest" }
androidx-compose-ui-test-junit4 = { group = "androidx.compose.ui", name = "ui-test-junit4" }
androidx-compose-material3 = { group = "androidx.compose.material3", name = "material3" }

# Navigasi & ViewModel Compose
androidx-navigation-compose = { group = "androidx.navigation", name = "navigation-compose", version.ref = "navigationCompose" }
androidx-lifecycle-viewmodel-compose = { group = "androidx.lifecycle", name = "lifecycle-viewmodel-compose", version.ref = "lifecycleViewModelCompose" }
androidx-lifecycle-runtime-compose = { group = "androidx.lifecycle", name = "lifecycle-runtime-compose", version.ref = "lifecycleViewModelCompose" }
androidx-compose-material-icons-extended = { group = "androidx.compose.material", name = "material-icons-extended", version.ref = "materialIconsExtended" }

# Serialization JSON
kotlinx-serialization-json = { group = "org.jetbrains.kotlinx", name = "kotlinx-serialization-json", version.ref = "kotlinxSerialization" }

# Supabase SDK (BOM & Modules)
supabase-bom = { group = "io.github.jan-tennert.supabase", name = "bom", version.ref = "supabase" }
supabase-postgrest = { group = "io.github.jan-tennert.supabase", name = "postgrest-kt" }
supabase-auth = { group = "io.github.jan-tennert.supabase", name = "auth-kt" }
supabase-storage = { group = "io.github.jan-tennert.supabase", name = "storage-kt" }

# Ktor HTTP Client (Engine untuk Supabase)
ktor-client-android = { group = "io.ktor", name = "ktor-client-android", version.ref = "ktor" }

# Coil untuk Muat Gambar (Fitur Catatan)
coil-compose = { group = "io.coil-kt", name = "coil-compose", version.ref = "coil" }

[plugins]
android-application = { id = "com.android.application", version.ref = "agp" }
kotlin-compose = { id = "org.jetbrains.kotlin.plugin.compose", version.ref = "kotlin" }
kotlin-serialization = { id = "org.jetbrains.kotlin.plugin.serialization", version.ref = "kotlin" }
```

---

## 2. Konfigurasi `app/build.gradle.kts`

Tambahkan plugin serialisasi dan dependensi di `app/build.gradle.kts`:

```kotlin
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization) // Tambahkan plugin serialisasi JSON
}

android {
    namespace = "com.example.student_life_app"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.student_life_app"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.compose.material.icons.extended)

    // Navigation & ViewModel
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.lifecycle.runtime.compose)

    // JSON Serialization
    implementation(libs.kotlinx.serialization.json)

    // Supabase
    implementation(platform(libs.supabase.bom))
    implementation(libs.supabase.postgrest)
    implementation(libs.supabase.auth)
    implementation(libs.supabase.storage)
    implementation(libs.ktor.client.android)

    // Image Loading (Fitur 3 Catatan Belajar)
    implementation(libs.coil.compose)

    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    debugImplementation(libs.androidx.compose.ui.tooling)
}
```

---

## 3. Konfigurasi `AndroidManifest.xml` (Izin Internet)

Tambahkan izin akses internet di `app/src/main/AndroidManifest.xml` agar aplikasi dapat terhubung ke Supabase Cloud dan memuat gambar:

```xml
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android">

    <!-- Izin Akses Jaringan Internet -->
    <uses-permission android:name="android.permission.INTERNET" />
    <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />

    <application
        android:name=".StudentLifeApp"
        android:allowBackup="true"
        android:icon="@mipmap/ic_launcher"
        android:label="@string/app_name"
        android:roundIcon="@mipmap/ic_launcher_round"
        android:supportsRtl="true"
        android:theme="@style/Theme.Studentlifeapp">
        <activity
            android:name=".MainActivity"
            android:exported="true"
            android:theme="@style/Theme.Studentlifeapp">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />
                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>
    </application>

</manifest>
```

---

## 4. Inisialisasi Supabase Client (`SupabaseClient.kt`)

Letakkan berkas ini pada `core/database/SupabaseClient.kt`:

```kotlin
package com.example.student_life_app.core.database

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.storage.Storage

object SupabaseManager {
    // Masukkan kredensial proyek Supabase Anda
    private const val SUPABASE_URL = "https://your-project-id.supabase.co"
    private const val SUPABASE_ANON_KEY = "your-anon-key-here"

    val client: SupabaseClient by lazy {
        createSupabaseClient(
            supabaseUrl = SUPABASE_URL,
            supabaseKey = SUPABASE_ANON_KEY
        ) {
            install(Auth)
            install(Postgrest)
            install(Storage)
        }
    }
}
```
