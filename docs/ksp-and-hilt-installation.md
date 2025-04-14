# How to install Hilt 

Official page of [Hilt DI Framework](https://dagger.dev/hilt/).

Hilt supports both KAPT (legacy) and KSP (preferred) as annotation processors.
The Effects library works only with KSP, so both Hilt and KSP must be installed.

1. Make sure you are using Kotlin 2.0 or above.
2. KSP plugin versions follow the pattern `{KotlinVersion}-{KSPVersion}`.
   For example, if you're using Kotlin `2.1.10`, then the corresponding KSP plugin should be `2.1.10-{KSPVersion}`,
   e.g. `2.1.10-1.0.31`.
3. Check out the latest compatible versions of Kotlin, KSP and Hilt.

   - [Actual KSP versions](https://mvnrepository.com/artifact/com.google.devtools.ksp/com.google.devtools.ksp.gradle.plugin?repo=central)
   - [Actual Hilt versions](https://mvnrepository.com/artifact/com.google.dagger/hilt-android-gradle-plugin)

4. Add the plugins to your root `build.gradle.kts`:

   ```kotlin
   plugins {
       id("com.google.dagger.hilt.android") version("2.55") apply false
       id("com.google.devtools.ksp") version ("2.1.10-1.0.31") apply false
   }
   ```

5. Apply plugins and add dependencies in `app/build.gradle`:

   ```kotlin
   plugins {
       id("com.google.dagger.hilt.android")
       id("com.google.devtools.ksp")
   }
   
   dependencies {
       implementation("com.google.dagger:hilt-android:2.55")
       ksp("com.google.dagger:hilt-android-compiler:2.55")
   }
   ```
   
6. Create your custom `Application` class and annotate it with `@HiltAndroidApp`:

   ```kotlin
   @HiltAndroidApp
   class MyApp : Application()
   ```

7. Register the application class in `AndroidManifest.xml`:

   ```xml
   <application
       android:name=".MyApp" />
   ```

8. Annotate your `MainActivity` with `@AndroidEntryPoint` annotation:

   ```kotlin
   @AndroidEntryPoint
   class MainActivity : ComponentActivity() {
        // ...
   }
   ```
