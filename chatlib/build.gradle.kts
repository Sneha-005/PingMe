plugins {
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.devsneha.chatlib"
    compileSdk = 35

    defaultConfig {
        minSdk = 24

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
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
}

dependencies {
    implementation(libs.androidx.core.ktx.v1120)
    implementation(libs.androidx.lifecycle.runtime.ktx.v270)
    implementation(libs.androidx.activity.compose.v182)
    implementation(libs.androidx.ui.v158)
    implementation(libs.androidx.ui.tooling.preview.v158)
    implementation(libs.androidx.material3.v112)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.lifecycle.runtime.compose.v270)
    implementation(libs.kotlinx.coroutines.android.v190)
    implementation(libs.androidx.navigation.compose.v276)
    implementation(libs.coil.compose.v260)
    debugImplementation(libs.androidx.ui.tooling.v158)
    debugImplementation(libs.androidx.ui.test.manifest.v158)
}