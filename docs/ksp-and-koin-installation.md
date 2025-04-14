# How to install Koin 

Official page of [Insert Koin DI Framework](https://insert-koin.io/).

Originally, Koin DI framework does not require compile-time KSP annotation processing, so
it works great even without pre-installed KSP out of the box. 

The Effects library in its turn mostly relies on KSP. But you have an option to install it
even [without KSP](no-ksp-installation.md), with a bit of additional configuration.

## Install pure Koin

1. Origin documentation is [here](https://insert-koin.io/docs/setup/koin/).
2. Add `koin-bom` and `koin-core` dependencies:

   ```kotlin
   
   dependencies {
       implementation(platform("io.insert-koin:koin-bom:4.0.3"))
       implementation("io.insert-koin:koin-core")
   }
   ```

3. Koin is a Multiplatform library, but it has Android specific dependencies as well,
   enabling additional features like working with Activity Lifecycle, ViewModels, etc:

   ```kotlin
   implementation("io.insert-koin:koin-android")
   implementation("io.insert-koin:koin-androidx-compose:4.0.3")
   ```

## Install KSP

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
       // KSP annotation processors should be added using the 'ksp' configuration
       // Example: adding Effect's KSP compiler
       // ksp("com.uandcode:effects2-koin-compiler:2.0.0-alpha01")
   }
   ```
