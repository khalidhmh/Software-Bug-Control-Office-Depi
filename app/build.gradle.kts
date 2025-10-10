plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)// Compose Compiler // هيلت plugin (نفس الاسم الموجود في toml)
}



android {
    namespace = "com.example.mda"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.mda"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"



        val tmdbKey: String? = project.findProperty("TMDB_API_KEY") as? String
        buildConfigField(
            "String",
            "TMDB_API_KEY",
            "\"${tmdbKey}\""
        )


        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
}

dependencies {
    implementation("androidx.navigation:navigation-compose:2.8.8")

    implementation("io.coil-kt:coil-compose:2.7.0")

    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.6")


    implementation(platform("androidx.compose:compose-bom:2024.10.00"))

// UI
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")

// Material
    implementation("androidx.compose.material3:material3")

// Activity Compose
    implementation("androidx.activity:activity-compose:1.9.2")


    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.6")

    implementation("androidx.compose.material:material-icons-core")
    implementation("androidx.compose.material:material-icons-extended")

    // Retrofit + Gson
    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.retrofit2:converter-gson:2.11.0")

// OkHttp + logging
    implementation("com.squareup.okhttp3:okhttp:5.1.0")
    implementation("com.squareup.okhttp3:logging-interceptor:5.1.0")
// Lifecycle
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.9.4")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.9.4")
// Image loader (Coil)
    implementation("io.coil-kt:coil:2.7.0")
    implementation(libs.volley)
    implementation("io.coil-kt:coil-compose:2.7.0")

    // Coil for Jetpack Compose
    implementation("io.coil-kt:coil-compose:2.7.0")
    implementation(libs.ui)


// (اختياري لاختبارات) MockWebServer
    testImplementation("com.squareup.okhttp3:mockwebserver:5.1.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.11.0")

    implementation("androidx.navigation:navigation-compose:2.9.5")
    implementation("androidx.compose.material:material-icons-extended:1.7.8")
    implementation("com.google.accompanist:accompanist-swiperefresh:0.32.0")

    // Lifecycle (for ViewModel)
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.6")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.6")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.6")

    implementation("androidx.compose.material:material-icons-core:1.7.2")

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)


    implementation("io.coil-kt:coil-compose:2.7.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.9.4")

        implementation("androidx.compose.material:material-icons-extended")
    


}