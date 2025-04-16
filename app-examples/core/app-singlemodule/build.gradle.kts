plugins {
    alias(libs.plugins.custom.application)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.uandcode.example.core.singlemodule"
    defaultConfig {
        applicationId = "com.uandcode.example.core.singlemodule"
        versionCode = 1
        versionName = "1.0"
    }
}

dependencies {
    // Effects library
    implementation(libs.effects.core.compose)
    ksp(libs.effects.core.compiler)

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