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
    override val sonatypeStagingProfileId: String = properties.getProperty("sonatypeStagingProfileId", "")
    override val ossrhTokenUsername: String = properties.getProperty("ossrhTokenUsername", "")
    override val ossrhTokenPassword: String = properties.getProperty("ossrhTokenPassword", "")
    override val signingKeyId: String = properties.getProperty("signingKeyId", "")
    override val signingKey: String = properties.getProperty("signingKey", "")
    override val signingPassword: String = properties.getProperty("signingPassword", "")
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
