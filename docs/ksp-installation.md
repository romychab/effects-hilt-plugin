# How to install KSP

1. Use Kotlin 2.0 or above. Install the latest version of Android Studio.
2. KSP plugin versions follow the pattern `{KotlinVersion}-{KSPVersion}`.
   For example, if you're using Kotlin `2.1.10`, then the corresponding KSP plugin should be `2.1.10-{KSPVersion}`,
   e.g. `2.1.10-1.0.31`. You can find the list of available KSP versions [here](https://mvnrepository.com/artifact/com.google.devtools.ksp/com.google.devtools.ksp.gradle.plugin?repo=central).
3. Add the KSP plugin to your root `build.gradle.kts`:

   ```kotlin
   plugins {
       id("com.google.devtools.ksp") version ("2.1.10-1.0.31") apply false
   }
   ```

4. Apply the plugin in your module-level `build.gradle.kts`, e.g. `app/build.gradle.kts`:

   ```kotlin
   plugins {
       id("com.google.devtools.ksp")
   }
   
   ksp {
       // Additional KSP configuration can go here, if needed
   }
   
   dependencies {
       // KSP annotation processors should be added using the 'ksp(dependencyNotation)'
       // Example: adding Hilt's KSP compiler
       // ksp("com.google.dagger:hilt-android-compiler:2.55")
   }
   ```
