// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version "8.2.2" apply false
    id("com.google.gms.google-services") version "4.3.15" apply false // Firebase eklentisi
}

// allprojects bloğunu kaldırıyoruz çünkü settings.gradle.kts zaten repositories tanımlarını içeriyor.

tasks.register<Delete>("clean") {
    delete(rootProject.buildDir)
}
