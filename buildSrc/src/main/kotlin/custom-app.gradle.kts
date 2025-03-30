import helpers.*

plugins {
    id("com.android.application")
    id("kotlin-android")
    id("org.jetbrains.kotlin.plugin.compose")
    id("org.jetbrains.kotlin.plugin.serialization")
}

val catalogs = extensions.getByType<VersionCatalogsExtension>()

android {
    compileSdk = catalogs.targetSdk

    defaultConfig {
        minSdk = catalogs.minSdk
        targetSdk = catalogs.targetSdk
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    buildFeatures {
        compose = true
    }

    compileOptions {
        sourceCompatibility = catalogs.minJavaVersion
        targetCompatibility = catalogs.minJavaVersion
    }
    kotlinOptions {
        jvmTarget = catalogs.minJvmString
    }
}

kotlin {
    jvmToolchain(catalogs.toolchainJvm)
}
