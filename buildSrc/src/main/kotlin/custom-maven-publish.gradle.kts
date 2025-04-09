import helpers.*

plugins {
    id("org.jetbrains.dokka")
    `maven-publish`
    signing
}

val catalogs = extensions.getByType<VersionCatalogsExtension>()

interface CustomMavenPublishExtension {
    val groupId: Property<String>
    val artifactId: Property<String>
    val description: Property<String>
}

private val customMavenPublishExtension = project.extensions.create<CustomMavenPublishExtension>("publishConfig")

val isAndroidLib = project.plugins.findPlugin("com.android.library") != null
if (!isAndroidLib) {
    val librarySourcesJar = tasks.create("librarySourcesJar", Jar::class) {
        archiveClassifier = "sources"
        from(project.layout.projectDirectory.dir("src/main/java"))
        from(project.layout.projectDirectory.dir("src/main/kotlin"))
    }
    val javadocJar = tasks.create("javadocJar", Jar::class) {
        dependsOn(tasks.dokkaHtml)
        archiveClassifier = "javadoc"
        from(File("${buildDir}/dokka/html"))
    }
    artifacts {
        archives(librarySourcesJar)
        archives(javadocJar)
    }
}

afterEvaluate {
    publishing {
        repositories {
            maven {
                name = "build"
                url = uri(rootProject.layout.buildDirectory.dir("maven"))
            }
        }
        publications {
            create<MavenPublication>("release") {
                groupId = customMavenPublishExtension.groupId.get()
                artifactId = customMavenPublishExtension.artifactId.get()
                version = catalogs.effectLibraryVersion
                if (isAndroidLib) {
                    from(components["release"])
                } else {
                    from(components["java"])
                    artifact(tasks.getByName("librarySourcesJar"))
                    artifact(tasks.getByName("javadocJar"))
                }

                pom {
                    name = customMavenPublishExtension.artifactId.get()
                    description = customMavenPublishExtension.description.get()
                    url = "https://github.com/romychab/effects-hilt-plugin"
                    licenses {
                        license {
                            name = "Apache License 2.0"
                            url = "https://github.com/romychab/effects-hilt-plugin/blob/main/LICENSE"
                        }
                    }
                    developers {
                        developer {
                            id = "romychab"
                            name = "Roman Andrushchenko"
                            email = "rom.andrushchenko@gmail.com"
                        }
                    }
                    scm {
                        connection = "scm:git:github.com/romychab/effects-hilt-plugin.git"
                        developerConnection = "scm:git:ssh://github.com/romychab/effects-hilt-plugin.git"
                        url = "https://github.com/romychab/effects-hilt-plugin/tree/main"
                    }
                }
            }
        }
    }
}

signing {
    with(loadLocalProperties(rootProject)) {
        useInMemoryPgpKeys(signingKeyId, signingKey, signingPassword)
    }
    sign(publishing.publications)
}
