plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.example.focuswellbeing"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        applicationId = "com.example.focuswellbeing"
        minSdk = 24
        targetSdk = 36
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
}

dependencies {
    testImplementation("junit:junit:4.13.2")
    implementation("androidx.datastore:datastore-preferences:1.0.0")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.10.2")
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.lifecycle.process)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.compose.ui.text)
    implementation(libs.androidx.compose.material.icons.extended)
    implementation(libs.processor.annotations.jvm)
    implementation(libs.androidx.compose.foundation.layout)
    implementation(libs.androidx.navigation.compose)
    implementation(project(":music"))
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    implementation(kotlin("test"))
}

// Keep Lifecycle/Navigation on 2.9.x — newer lines need AGP 9.1 + compileSdk 37.
configurations.configureEach {
    resolutionStrategy {
        eachDependency {
            if (requested.group == "androidx.lifecycle") {
                useVersion(libs.versions.lifecycle.get())
                because("Lifecycle 2.11 requires AGP 9.1 / compileSdk 37")
            }
            if (requested.group == "androidx.navigation") {
                useVersion(libs.versions.navigationCompose.get())
                because("Navigation 2.10 requires AGP 9.1 / compileSdk 37")
            }
        }
    }
}
