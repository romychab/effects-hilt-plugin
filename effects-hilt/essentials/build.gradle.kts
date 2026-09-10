import java.util.zip.ZipFile
import org.gradle.api.publish.maven.tasks.AbstractPublishToMaven

plugins {
    alias(libs.plugins.custom.android.library)
    alias(libs.plugins.custom.maven.publish)
}

android {
    namespace = "com.uandcode.effects.hilt"
}

kotlin {
    compilerOptions {
        optIn.add("com.uandcode.effects.hilt.InternalEffectsHiltApi")
    }
}

publishConfig {
    artifactId = "effects2-hilt"
    description = "Effects Hilt Plugin for simplifying the implementation of one-off events."
}

dependencies {
    api(projects.effectsCore.essentials)
    api(projects.effectsHilt.annotations)
    implementation(projects.effectsCore.lifecycle)

    compileOnly(libs.hilt.android)
}
