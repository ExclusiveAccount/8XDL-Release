plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.dagger.hilt.android")
    id("org.jetbrains.kotlin.plugin.compose")
    id("com.google.devtools.ksp")
    id("kotlin-kapt")
    id("kotlin-parcelize")
}

android {
    namespace = "com.codex.torrentx"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.codex.torrentx"
        minSdk = 26
        targetSdk = 34
        versionCode = 2
        versionName = "1.0.1"

        buildConfigField("boolean", "FULL_VERSION", "true")

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }

        ndk {
            abiFilters += listOf("armeabi-v7a", "arm64-v8a")
        }
    }

    signingConfigs {
        create("release") {
            storeFile = file("../8xdl.jks")
            storePassword = "a8ix@5625"
            keyAlias = "key0"
            keyPassword = "a8ix@5625"
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            // Use the official release keystore generated via keytool
            signingConfig = signingConfigs.getByName("release")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }



    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
        jniLibs {
            useLegacyPackaging = true
            pickFirsts += "lib/x86/libffmpeg.so"
            pickFirsts += "lib/x86_64/libffmpeg.so"
            pickFirsts += "lib/armeabi-v7a/libffmpeg.so"
            pickFirsts += "lib/arm64-v8a/libffmpeg.so"
        }
    }
}

dependencies {
    // Core Android
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.robolectric:robolectric:4.11.1")
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")
    implementation("androidx.lifecycle:lifecycle-service:2.7.0")
    implementation("androidx.activity:activity-compose:1.8.2")
    implementation("androidx.activity:activity-ktx:1.8.2")
    implementation("androidx.fragment:fragment-ktx:1.6.2")

    // Compose BOM
    implementation(platform("androidx.compose:compose-bom:2024.02.02"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-extended")
    implementation("androidx.compose.animation:animation")
    implementation("androidx.compose.animation:animation-core")
    implementation("androidx.compose.foundation:foundation")
    debugImplementation("androidx.compose.ui:ui-tooling")

    // Navigation
    implementation("androidx.navigation:navigation-compose:2.7.6")

    // Hilt
    // Hilt
    implementation("com.google.dagger:hilt-android:2.55")
    ksp("com.google.dagger:hilt-android-compiler:2.55")
    implementation("androidx.hilt:hilt-navigation-compose:1.1.0")
    implementation("androidx.hilt:hilt-work:1.1.0")
    ksp("androidx.hilt:hilt-compiler:1.1.0")

    // Room Database
    implementation("androidx.room:room-runtime:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")
    ksp("androidx.room:room-compiler:2.6.1")
    implementation("androidx.room:room-rxjava2:2.6.1")

    // Security & Biometric (VaultX)
    implementation("androidx.biometric:biometric:1.2.0-alpha05")
    implementation("androidx.security:security-crypto:1.1.0-alpha06")

    // libtorrent4j - Torrent Engine
    implementation("org.libtorrent4j:libtorrent4j:2.1.0-38")
    implementation("org.libtorrent4j:libtorrent4j-android-arm:2.1.0-38")
    implementation("org.libtorrent4j:libtorrent4j-android-arm64:2.1.0-38")
    implementation("org.libtorrent4j:libtorrent4j-android-x86:2.1.0-38")
    implementation("org.libtorrent4j:libtorrent4j-android-x86_64:2.1.0-38")

    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
    
    // OkHttp for network requests
    implementation("com.squareup.okhttp3:okhttp:4.12.0")

    // Jsoup for HTML parsing (Instagram/Pinterest scraper)
    implementation("org.jsoup:jsoup:1.17.2")

    // DataStore for settings
    implementation("androidx.datastore:datastore-preferences:1.0.0")

    // Coil for any image loading
    implementation("io.coil-kt:coil-compose:2.5.0")
    implementation("io.coil-kt:coil-video:2.5.0") // For video thumbnails

    // Gson for serialization
    implementation("com.google.code.gson:gson:2.10.1")

    // Work Manager for background tasks
    implementation("androidx.work:work-runtime-ktx:2.9.0")

    // DocumentFile
    implementation("androidx.documentfile:documentfile:1.0.1")

    // Fulguris Browser Module Integration
    implementation(project(":browserx"))

    // Fetch2 (GitHub variant via Jitpack)
    // implementation("com.github.tonyofrancis.Fetch:fetch2:3.4.1")
    // implementation("com.github.tonyofrancis.Fetch:fetch2okhttp:3.4.1")

    // YoutubeDL-Android
    implementation("com.github.yausername.youtubedl-android:library:0.14.+")
    implementation("com.github.yausername.youtubedl-android:ffmpeg:0.14.+")

    // FFmpeg (RxFFmpeg - Jitpack)
    implementation("com.github.microshow:RxFFmpeg:4.9.0")
    // implementation("com.arthenica:ffmpeg-kit-full:4.4") // Failed to resolve, using RxFFmpeg instead
    // implementation("com.antonkarpenko:ffmpeg-kit-full-gpl:2.1.0") // Commented out to avoid conflicts if any

    // Media3 (ExoPlayer + Session)
    // Media3 (ExoPlayer + Session)
    // Media3 (ExoPlayer + Session)
    val media3Version = "1.8.0"
    implementation("androidx.media3:media3-exoplayer:$media3Version")
    implementation("androidx.media3:media3-exoplayer-dash:$media3Version")
    implementation("androidx.media3:media3-exoplayer-hls:$media3Version")
    implementation("androidx.media3:media3-exoplayer-rtsp:$media3Version")
    implementation("androidx.media3:media3-ui:$media3Version")
    implementation("androidx.media3:media3-session:$media3Version")
    implementation("androidx.media3:media3-common:$media3Version")
    implementation("androidx.media3:media3-cast:$media3Version") // Cast extension
    implementation("androidx.media3:media3-datasource-okhttp:$media3Version") // If needed for networking

    // Google Cast Framework
    implementation("com.google.android.gms:play-services-cast-framework:21.4.0")




    // Jellyfin Media3 FFmpeg Decoder
    implementation("org.jellyfin.media3:media3-ffmpeg-decoder:1.8.0+1")

    // CameraX — REMOVED (barcode scanner removed to save ~17MB)
    // MLKit Barcode Scanning — REMOVED
    // Accompanist Permissions — REMOVED (was only used by QR scanner)

    // --- Core Dependencies (restored) ---
    implementation("androidx.recyclerview:recyclerview:1.3.2")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.cardview:cardview:1.0.0")


    configurations.all {
        resolutionStrategy {
            force("androidx.core:core-ktx:1.13.1")
            force("androidx.core:core:1.13.1")
        }
    }
}

kapt {
    correctErrorTypes = true
}

// Exclude Hilt annotation processor from KAPT since KSP handles it
configurations.all {
    if (name.startsWith("kapt")) {
        exclude(group = "com.google.dagger", module = "hilt-compiler")
    }
}
