import helpers.*

plugins {
    id("org.jetbrains.dokka")
    id("java")
    id("org.jetbrains.kotlin.jvm")
    `maven-publish`
    signing
}

val catalogs = extensions.getByType<VersionCatalogsExtension>()

java {
    sourceCompatibility = catalogs.minJavaVersion
    targetCompatibility = catalogs.minJavaVersion
}

kotlin {
    compilerOptions {
        jvmTarget = catalogs.minJvm
    }
    jvmToolchain(catalogs.toolchainJvm)
    explicitApi()
}
