import helpers.*

plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.dokka")
}

val catalogs = extensions.getByType<VersionCatalogsExtension>()

android {
    compileSdk = catalogs.targetSdk
    defaultConfig {
        minSdk = catalogs.minSdk
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }
    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    publishing {
        singleVariant("release") {
            withSourcesJar()
            withJavadocJar()
        }
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
    explicitApi()
}
