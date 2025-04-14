plugins {
    alias(libs.plugins.custom.application)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.uandcode.example.koin.singlemodule"
    defaultConfig {
        applicationId = "com.uandcode.example.koin.singlemodule"
        versionCode = 1
        versionName = "1.0"
    }
}

dependencies {

    // Koin (required)
    implementation(platform(libs.koin.bom))
    implementation(libs.koin.android.compose)

    // Effects library
    implementation(projects.effectsKoin.compose)
    ksp(projects.effectsKoin.compiler)

    // Other dependencies
    implementation(platform(libs.compose.bom))
    implementation(libs.compose.ui)
    implementation(libs.compose.material3)
    implementation(libs.compose.ui.tooling.preview)
    implementation(libs.compose.activity)
    implementation(libs.navigation.compose)
    implementation(libs.java.faker)
    implementation(libs.coil.compose)
    implementation(libs.container)
    implementation(libs.coroutines.core)
    implementation(libs.kotlin.serialization.core)
    debugImplementation(libs.compose.ui.tooling)
    debugImplementation(libs.leak.canary)
}