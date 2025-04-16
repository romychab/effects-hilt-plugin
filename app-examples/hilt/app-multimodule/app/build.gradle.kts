plugins {
    alias(libs.plugins.custom.application)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
}

android {
    namespace = "com.uandcode.example.hilt.multimodule.app"
    defaultConfig {
        applicationId = "com.uandcode.example.hilt.multimodule.app"
        versionCode = 1
        versionName = "1.0"
    }
}

dependencies {
    // feature modules
    implementation(projects.appExamples.hilt.appMultimodule.featureList)
    implementation(projects.appExamples.hilt.appMultimodule.featureDetails)

    // effects located in other modules
    // (just an example, you don't need to do this :)
    implementation(projects.appExamples.hilt.appMultimodule.effectImplToasts)
    implementation(projects.appExamples.hilt.appMultimodule.effectImplDialogsCompose)
    implementation(projects.appExamples.hilt.appMultimodule.effectImplDialogsAndroid)

    // effects library
    implementation(libs.effects.hilt.compose)
    ksp(libs.effects.hilt.compiler)

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
