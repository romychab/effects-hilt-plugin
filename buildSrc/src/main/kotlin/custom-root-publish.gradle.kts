import io.github.gradlenexus.publishplugin.NexusPublishExtension
import helpers.loadLocalProperties

plugins {
    id("io.github.gradle-nexus.publish-plugin")
}

val localProperties = loadLocalProperties(rootProject)

configure<NexusPublishExtension> {
    repositories {
        sonatype {
            with(localProperties) {
                stagingProfileId = sonatypeStagingProfileId
                username = ossrhTokenUsername
                password = ossrhTokenPassword
                nexusUrl.set(uri("https://s01.oss.sonatype.org/service/local/"))
                snapshotRepositoryUrl.set(uri("https://s01.oss.sonatype.org/content/repositories/snapshots/"))
            }
        }
    }
}
