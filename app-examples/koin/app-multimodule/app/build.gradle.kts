plugins {
    alias(libs.plugins.custom.application)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.uandcode.example.koin.multimodule.app"
    defaultConfig {
        applicationId = "com.uandcode.example.koin.multimodule.app"
        versionCode = 1
        versionName = "1.0"
    }
}

dependencies {
    // feature modules
    implementation(projects.appExamples.koin.appMultimodule.featureList)
    implementation(projects.appExamples.koin.appMultimodule.featureDetails)

    // effects located in other modules
    // (just an example, you don't need to do this :)
    implementation(projects.appExamples.koin.appMultimodule.effectImplToasts)
    implementation(projects.appExamples.koin.appMultimodule.effectImplDialogsCompose)
    implementation(projects.appExamples.koin.appMultimodule.effectImplDialogsAndroid)

    // effects library
    implementation(projects.effectsKoin.compose)
    ksp(projects.effectsKoin.compiler)

    // Koin (required)
    implementation(platform(libs.koin.bom))
    implementation(libs.koin.android.compose)

    // other dependencies
    implementation(platform(libs.compose.bom))
    implementation(libs.compose.ui)
    implementation(libs.compose.material3)
    implementation(libs.compose.ui.tooling.preview)
    implementation(libs.compose.activity)
    implementation(libs.navigation.compose)
    implementation(libs.java.faker)
    implementation(libs.container)
    implementation(libs.coroutines.core)
    implementation(libs.kotlin.serialization.core)
    debugImplementation(libs.compose.ui.tooling)
    debugImplementation(libs.leak.canary)
}
