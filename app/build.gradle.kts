import org.gradle.internal.impldep.org.joda.time.tz.ZoneInfoLogger.verbose

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("kotlin-kapt") // ✅ ضروري لـ Room
}

kapt {
    useBuildCache = false
    correctErrorTypes = true
}

android {
    namespace = "com.example.mda"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.mda"
        minSdk = 24
        targetSdk = 36
        versionCode = 2
        versionName = "2.0"

        // ✅ قراءة المفتاح من gradle.properties
        val tmdbKey: String? = project.findProperty("TMDB_API_KEY") as? String
        buildConfigField("String", "TMDB_API_KEY", "\"${tmdbKey}\"")

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables.useSupportLibrary = true
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        debug {
            isMinifyEnabled = false
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlin {
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_11)
        }
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.15"
    }

    packaging {
        resources.excludes += "/META-INF/{AL2.0,LGPL2.1}"
    }

    kapt {
        correctErrorTypes = true
    }
}

dependencies {
    implementation("androidx.compose.material3:material3:1.4.0")
    implementation("com.google.accompanist:accompanist-systemuicontroller:0.36.0")
    // =============================================================
    // 🔹 CORE & LIFECYCLE
    // =============================================================
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.9.4")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.9.4")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.9.4")

    // =============================================================
    // 🔹 UI & COMPOSE
    // =============================================================
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation("androidx.compose.material:material-icons-core")
    implementation("androidx.compose.material:material-icons-extended")
    implementation(libs.androidx.compose.ui.graphics)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    // =============================================================
    // 🔹 NAVIGATION & ACTIVITY
    // =============================================================
    implementation(libs.androidx.activity.compose)
    implementation("androidx.navigation:navigation-compose:2.7.7")

    // =============================================================
    // 🔹 ACCOMPANIST
    // =============================================================
    implementation("com.google.accompanist:accompanist-swiperefresh:0.32.0")

    // =============================================================
    // 🔹 NETWORK - Retrofit + Gson + OkHttp
    // =============================================================
    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.retrofit2:converter-gson:2.11.0")
    implementation("com.squareup.okhttp3:okhttp:5.1.0")
    implementation("com.squareup.okhttp3:logging-interceptor:5.1.0")

    // =============================================================
    // 🔹 IMAGE LOADING (COIL)
    // =============================================================
    implementation("io.coil-kt:coil-compose:2.7.0")

    // =============================================================
    // 🔹 ROOM DATABASE
    // =============================================================
    val room_version = "2.7.0"
    implementation("androidx.room:room-runtime:$room_version")
    implementation("androidx.room:room-ktx:$room_version")
    kapt("androidx.room:room-compiler:$room_version")

    // تحديث kotlinx-metadata-jvm لتوافق Room
// نسخة مستقرة ومتوافقة مع Room
    implementation("org.jetbrains.kotlinx:kotlinx-metadata-jvm:0.9.0")

    // =============================================================
    // 🔹 PAGING 3
    // =============================================================
    implementation("androidx.paging:paging-runtime-ktx:3.3.2")
    implementation("androidx.paging:paging-compose:3.3.2")

    // =============================================================
    // 🔹 COROUTINES
    // =============================================================
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.1")

    // =============================================================
    // 🔹 VOLLEY
    // =============================================================
    implementation(libs.volley)

    // =============================================================
    // 🔹 TESTING
    // =============================================================
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
}
