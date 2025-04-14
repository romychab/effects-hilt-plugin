plugins {
    alias(libs.plugins.custom.library)
    alias(libs.plugins.custom.maven.publish)
}

publishConfig {
    artifactId = "effects2-koin-annotations"
    description = "Effects Koin Plugin (annotations)"
}
