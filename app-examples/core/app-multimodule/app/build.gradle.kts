plugins {
    alias(libs.plugins.custom.application)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.uandcode.example.core.multimodule.app"
    defaultConfig {
        applicationId = "com.uandcode.example.core.multimodule.app"
        versionCode = 1
        versionName = "1.0"
    }
}

dependencies {
    // feature modules
    implementation(projects.appExamples.core.appMultimodule.featureList)
    implementation(projects.appExamples.core.appMultimodule.featureDetails)

    // effects located in other modules
    // (just an example, you don't need to do this :)
    implementation(projects.appExamples.core.appMultimodule.effectImplToasts)
    implementation(projects.appExamples.core.appMultimodule.effectImplDialogsCompose)
    implementation(projects.appExamples.core.appMultimodule.effectImplDialogsAndroid)

    // effects library
    implementation(projects.effectsCore.compose)
    ksp(projects.effectsCore.compiler)

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
