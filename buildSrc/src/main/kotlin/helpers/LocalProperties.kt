package helpers

import org.gradle.api.Project
import java.io.FileInputStream
import java.util.Properties

interface LocalProperties {
    val sonatypeStagingProfileId: String
    val ossrhTokenUsername: String
    val ossrhTokenPassword: String
    val signingKeyId: String
    val signingKey: String
    val signingPassword: String
}

private class LocalPropertiesImpl(
    properties: Properties,
) : LocalProperties {
    override val sonatypeStagingProfileId: String by properties
    override val ossrhTokenUsername: String by properties
    override val ossrhTokenPassword: String by properties
    override val signingKeyId: String by properties
    override val signingKey: String by properties
    override val signingPassword: String by properties
}

fun loadLocalProperties(project: Project): LocalProperties {
    val localPropertiesFile = project.rootProject.file("local.properties")
    val properties = if (localPropertiesFile.exists()) {
        Properties().apply {
            FileInputStream(localPropertiesFile).use(::load)
        }
    } else {
        throw IllegalStateException("Can't find local.properties")
    }
    return LocalPropertiesImpl(properties)
}
