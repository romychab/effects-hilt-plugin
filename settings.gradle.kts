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

// Effects Core Library
include(":effects-core:kspcontract")
include(":effects-core:kspcontract-api")
include(":effects-core:essentials")
include(":effects-core:compiler-common")
include(":effects-core:compiler")
include(":effects-core:annotations")
include(":effects-core:lifecycle")
include(":effects-core:compose")
include(":effects-core:runtime")
include(":effects-core:testmocks")

// Effects Hilt Library
include(":effects-hilt:annotations")
include(":effects-hilt:essentials")
include(":effects-hilt:compose")
include(":effects-hilt:compiler")

// Effects Koin Library
include(":effects-koin:annotations")
include(":effects-koin:essentials")
include(":effects-koin:compose")
include(":effects-koin:compiler")
include(":effects-koin:kspcontract")

// Example App (Single-module, Core)
include(":app-examples:core:app-singlemodule")
// Example App (Multi-module, Core)
include(":app-examples:core:app-multimodule:app")
include(":app-examples:core:app-multimodule:compose-components")
include(":app-examples:core:app-multimodule:feature-list")
include(":app-examples:core:app-multimodule:feature-details")
include(":app-examples:core:app-multimodule:effect-impl-dialogs-android")
include(":app-examples:core:app-multimodule:effect-impl-dialogs-compose")
include(":app-examples:core:app-multimodule:effect-impl-toasts")
include(":app-examples:core:app-multimodule:effect-interfaces")

// Example App (Single-module, Hilt)
include(":app-examples:hilt:app-singlemodule")
// Example App (Multi-module, Hilt)
include(":app-examples:hilt:app-multimodule:app")
include(":app-examples:hilt:app-multimodule:compose-components")
include(":app-examples:hilt:app-multimodule:feature-list")
include(":app-examples:hilt:app-multimodule:feature-details")
include(":app-examples:hilt:app-multimodule:effect-impl-dialogs-android")
include(":app-examples:hilt:app-multimodule:effect-impl-dialogs-compose")
include(":app-examples:hilt:app-multimodule:effect-impl-toasts")
include(":app-examples:hilt:app-multimodule:effect-interfaces")

// Example App (Single-module, Koin)
include(":app-examples:koin:app-singlemodule")
// Example App (Multi-module, Koin)
include(":app-examples:koin:app-multimodule:app")
include(":app-examples:koin:app-multimodule:compose-components")
include(":app-examples:koin:app-multimodule:feature-list")
include(":app-examples:koin:app-multimodule:feature-details")
include(":app-examples:koin:app-multimodule:effect-impl-dialogs-android")
include(":app-examples:koin:app-multimodule:effect-impl-dialogs-compose")
include(":app-examples:koin:app-multimodule:effect-impl-toasts")
include(":app-examples:koin:app-multimodule:effect-interfaces")
