plugins {
    `kotlin-dsl`
}

dependencies {
    implementation(libs.libplugin.nexusPublish)
    implementation(libs.libplugin.dokka)
    implementation(libs.libplugin.kotlinJvm)
    implementation(libs.libplugin.androidApp)
    implementation(libs.libplugin.androidLibrary)
    implementation(libs.libplugin.kotlinAndroid)
    implementation(libs.libplugin.kotlinSerialization)
    implementation(libs.libplugin.kotlinCompose)
    implementation(libs.libplugin.hilt)
    implementation(libs.libplugin.ksp)
}
