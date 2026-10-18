plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.kapt)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.hilt.android)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.capstone.tala"
    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "com.capstone.tala"
        minSdk = libs.versions.minSdk.get().toInt()
        targetSdk = libs.versions.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        debug {
            isDebuggable = true
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("debug")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.4"
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

    lint {
        abortOnError = false
    }
}

dependencies {
    // Core Android
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)

    // Jetpack Compose
    implementation(platform(libs.compose.bom))
    implementation(libs.compose.ui)
    implementation(libs.compose.ui.tooling.preview)
    implementation(libs.compose.material3)
    implementation(libs.compose.navigation)
    implementation(libs.compose.material.icons.extended)
    implementation(libs.material3.window.size)

            // Data persistence
            implementation(libs.room.runtime)
            implementation(libs.room.ktx)
            ksp(libs.room.compiler)

            implementation(libs.datastore.preferences)

            // Dependency Injection
            implementation(libs.hilt.android)
            ksp(libs.hilt.compiler)
            implementation(libs.hilt.navigation.compose)

            // Coroutines
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.kotlinx.coroutines.android)
            implementation(libs.kotlinx.serialization.json)

            // Testing
            testImplementation(libs.junit)
            testImplementation(libs.androidx.core.ktx)
            testImplementation(libs.turbine)
            testImplementation(libs.room.testing)
            testImplementation(libs.kotlinx.coroutines.test)
            testImplementation(libs.robolectric)
            testImplementation(libs.mockk)
            testImplementation("androidx.test:core:1.5.0")

            androidTestImplementation(platform(libs.compose.bom))
            androidTestImplementation(libs.junit)
            androidTestImplementation(libs.androidx.junit)
            androidTestImplementation(libs.androidx.espresso.core)
            androidTestImplementation(libs.compose.ui.tooling)
            androidTestImplementation(libs.compose.ui.test.junit4)
            androidTestImplementation(libs.mockk.android)
            androidTestImplementation(libs.hilt.android.testing)
            androidTestImplementation(libs.kotlinx.coroutines.test)
            kspAndroidTest(libs.hilt.compiler)
}

// Allow references to generated code
ksp {
    arg("room.expandProjection", "true")
}
