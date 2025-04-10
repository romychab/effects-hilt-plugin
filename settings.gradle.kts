pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        mavenLocal()
    }
    versionCatalogs {
        create("buildSrcLibs") {
            from(files("gradle/buildSrcLibs.versions.toml"))
        }
    }
}

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

rootProject.name = "Effects"

// effects plugin
include(":effects-annotations")
include(":effects-processor")
include(":effects-core")
include(":effects-compose")

// example app (single-module)
include(":app-examples:app-1-singlemodule")

// example app (multi-module)
include(":app-examples:app-2-multimodule:app")
include(":app-examples:app-2-multimodule:compose-components")
include(":app-examples:app-2-multimodule:feature-list")
include(":app-examples:app-2-multimodule:feature-details")
include(":app-examples:app-2-multimodule:effect-impl-dialogs-android")
include(":app-examples:app-2-multimodule:effect-impl-dialogs-compose")
include(":app-examples:app-2-multimodule:effect-impl-toasts")
include(":app-examples:app-2-multimodule:effect-interfaces")

include(":effects-v2-stub-api")
include(":effects-v2-stub")
include(":effects-v2-core")
include(":effects-v2-core-annotations")
include(":effects-v2-core-lifecycle")
include(":effects-v2-core-runtime")
include(":effects-v2-core-testmocks")
