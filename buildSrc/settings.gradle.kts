dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
    versionCatalogs {
        create("buildSrcLibs") {
            from(files("../gradle/buildSrcLibs.versions.toml"))
        }
    }
}

rootProject.name = "buildSrc"
