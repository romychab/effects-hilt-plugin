plugins {
    alias(libs.plugins.custom.library)
    alias(libs.plugins.custom.maven.publish)
}

publishConfig {
    artifactId = "effects2-hilt-annotations"
    description = "Effects Hilt Plugin (annotations)"
}
