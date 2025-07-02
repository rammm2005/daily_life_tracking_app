plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.example.gym_app"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.gym_app"
        minSdk = 25
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
        isCoreLibraryDesugaringEnabled = true
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
    }

    packaging {
        resources {
            // Ignore duplicate native-image.properties
            excludes += "META-INF/native-image/**"
        }
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.10"
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    testImplementation(libs.junit)
    implementation(libs.androidx.material3)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    // Kotlin coroutine dependency
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
    // MongoDB Kotlin driver dependency
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.0.4")
    implementation("org.mongodb:mongodb-driver-sync:4.10.1")
    implementation("org.mongodb:bson:4.11.1")
    implementation ("androidx.compose.material:material-icons-extended:1.5.0")


    // Bycrpyt Password
    implementation ("org.mindrot:jbcrypt:0.4")

//    Skeleton Loading
    implementation("com.google.accompanist:accompanist-placeholder-material:0.32.0")

    // YouTube Player
    implementation ("com.pierfrancescosoffritti.androidyoutubeplayer:core:12.1.0")

    // Image Slider
    implementation ("com.google.accompanist:accompanist-pager:0.34.0")
    implementation ("com.google.accompanist:accompanist-pager-indicators:0.34.0")


    // Text Editor Library
    implementation("com.halilibo.compose-richtext:richtext-commonmark:1.0.0-alpha02")
    implementation("com.halilibo.compose-richtext:richtext-ui-material3:1.0.0-alpha02")



    // API Fetcher
    implementation ("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.9.3")
    implementation ("com.squareup.retrofit2:converter-gson:2.9.0")

    // Data Store
    implementation ("androidx.datastore:datastore-preferences:1.0.0")

    // Foundation



    // Show dynamic Imgae
    implementation("io.coil-kt:coil-compose:2.4.0")



    implementation("androidx.navigation:navigation-compose:2.9.0")
    implementation("androidx.compose.runtime:runtime-livedata:1.8.2")
    implementation("androidx.compose.runtime:runtime-saveable:1.8.2")
    implementation("androidx.compose.material3:material3:1.3.2")
    implementation("androidx.constraintlayout:constraintlayout-compose:1.1.1")
    implementation("androidx.compose.foundation:foundation:1.8.2")
}