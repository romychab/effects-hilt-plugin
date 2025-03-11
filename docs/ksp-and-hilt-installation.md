# How to install KSP and Hilt

1. Add the following plugins to your root `build.gradle`:

   ```kotlin
   plugins {
       id("com.google.dagger.hilt.android") version("2.55") apply false
       id("com.google.devtools.ksp") version ("2.1.10-1.0.31") apply false
   }
   ```

2. Add plugins and dependencies to `app/build.gradle`:

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
   
3. Create a new application class:

   ```kotlin
   @HiltAndroidApp
   class MyApp : Application()
   ```

4. Register the application class in `AndroidManifest.xml` by using `name` attribute:

   ```xml
   <application
       android:name=".MyApp" />
   ```

5. Add `@AndroidEntryPoint` annotation to `MainActivity`:

   ```kotlin
   @AndroidEntryPoint
   class MainActivity : ComponentActivity() {
        // ...
   }
   ```
