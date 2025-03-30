package helpers

import org.gradle.api.JavaVersion
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

private const val catalogName = "libs"

val VersionCatalogsExtension.effectLibraryVersion: String
    get() = version("effectLibraryVersion")

val VersionCatalogsExtension.minSdk: Int
    get() = version("minSdk").toInt()

val VersionCatalogsExtension.targetSdk: Int
    get() = version("targetSdk").toInt()

val VersionCatalogsExtension.toolchainJvm: Int
    get() = version("toolchainJvm").toInt()

val VersionCatalogsExtension.minJvmString: String
    get() = version("minJvm")

val VersionCatalogsExtension.minJavaVersion: JavaVersion
    get() = minJvmString.toJavaVersion()

val VersionCatalogsExtension.minJvm: JvmTarget
    get() = minJvmString.toJvmTarget()

private fun VersionCatalogsExtension.version(name: String): String {
    return named(catalogName).findVersion(name).get().toString()
}

private fun String.toJavaVersion(): JavaVersion {
    val suffix = replace(".", "_")
    return JavaVersion.valueOf("VERSION_$suffix")
}

private fun String.toJvmTarget(): JvmTarget {
    val suffix = replace(".", "_")
    return JvmTarget.valueOf("JVM_$suffix")
}
