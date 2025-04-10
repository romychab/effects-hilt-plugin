plugins {
    alias(libs.plugins.custom.application)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
}

android {
    namespace = "com.elveum.effects.example.multimodule.app"
    defaultConfig {
        applicationId = "com.elveum.effects.example.multimodule.app"
        versionCode = 1
        versionName = "1.0"
    }
}

dependencies {
    // feature modules
    implementation(projects.appExamples.app2Multimodule.featureList)
    implementation(projects.appExamples.app2Multimodule.featureDetails)

    // effects located in other modules
    // (just an example, you don't need to do this :)
    implementation(projects.appExamples.app2Multimodule.effectImplToasts)
    implementation(projects.appExamples.app2Multimodule.effectImplDialogsCompose)
    implementation(projects.appExamples.app2Multimodule.effectImplDialogsAndroid)

    // effects library
    implementation("com.elveum:effects-compose:1.0.3")
    ksp("com.elveum:effects-processor:1.0.3")

    // hilt (required)
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)

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
    implementation(libs.hilt.navigation.compose)
    debugImplementation(libs.compose.ui.tooling)
    debugImplementation(libs.leak.canary)
}
