plugins {
    alias(libs.plugins.custom.library)
    alias(libs.plugins.custom.maven.publish)
}

publishConfig {
    groupId = "com.uandcode"
    artifactId = "effects2-stub-api"
    description = "Effects Core Library - APIs used both by internal KSP contract and by other core effect libraries"
}

dependencies {
    api(libs.coroutines.core)
}
