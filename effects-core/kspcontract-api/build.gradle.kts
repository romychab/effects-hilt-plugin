plugins {
    alias(libs.plugins.custom.library)
    alias(libs.plugins.custom.maven.publish)
}

publishConfig {
    artifactId = "effects2-core-kspcontract-api"
    description = "Effects Core Library - APIs used both by internal KSP contract and by other core effect libraries"
}

dependencies {
    api(libs.coroutines.core)
}
