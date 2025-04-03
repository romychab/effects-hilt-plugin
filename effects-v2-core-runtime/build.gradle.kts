plugins {
    alias(libs.plugins.custom.library)
    alias(libs.plugins.custom.maven.publish)
}

publishConfig {
    groupId = "com.uandcode"
    artifactId = "effects2-core-runtime"
    description = "A library for easier replacing effect components at runtime (e.g. in integration tests)"
}

dependencies {
    api(projects.effectsV2Core)
    implementation(libs.byte.buddy)
    implementation(libs.coroutines.core)
}
