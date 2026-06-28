import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidMultiplatformLibrary)
}

kotlin {
    explicitApi()

    androidLibrary {
        namespace = "dag.khinkal.molapi.http.android.assets"
        compileSdk = libs.versions.android.compileSdk.get().toInt()
        minSdk = libs.versions.android.minSdk.get().toInt()

        compilerOptions {
            jvmTarget = JvmTarget.JVM_11
        }
        androidResources {
            enable = true
        }
        withHostTest {
            isIncludeAndroidResources = true
        }
    }

    sourceSets {
        androidMain.dependencies {
            api(project(":molapi-http"))
        }
        named("androidHostTest").dependencies {
            implementation(libs.kotlin.test)
            implementation(libs.junit)
            implementation(libs.robolectric)
        }
    }
}
