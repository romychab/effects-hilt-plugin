plugins {
    alias(libs.plugins.custom.library)
    alias(libs.plugins.custom.maven.publish)
}

publishConfig {
    groupId = "com.uandcode"
    artifactId = "effects2-core"
    description = "A library (core artifact) for simplifying the implementation of one-off events"
}

dependencies {
    api(projects.effectsV2CoreApi)
    compileOnly(projects.effectsV2Stub)
}
