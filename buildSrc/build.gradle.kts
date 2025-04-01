plugins {
    `kotlin-dsl`
}

dependencies {
    implementation(buildSrcLibs.libplugin.nexusPublish)
    implementation(buildSrcLibs.libplugin.dokka)
    implementation(buildSrcLibs.libplugin.kotlinJvm)
    implementation(buildSrcLibs.libplugin.androidApp)
    implementation(buildSrcLibs.libplugin.androidLibrary)
    implementation(buildSrcLibs.libplugin.kotlinAndroid)
    implementation(buildSrcLibs.libplugin.kotlinSerialization)
    implementation(buildSrcLibs.libplugin.kotlinCompose)
    implementation(buildSrcLibs.libplugin.hilt)
    implementation(buildSrcLibs.libplugin.ksp)
}
