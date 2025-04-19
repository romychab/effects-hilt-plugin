plugins {
    alias(libs.plugins.custom.library)
    alias(libs.plugins.ksp)
}

dependencies {
    api(libs.ksp.testing)
    api(libs.junit)
}
