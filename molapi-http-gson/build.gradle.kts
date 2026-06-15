import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidMultiplatformLibrary)
}

kotlin {
    explicitApi()

    androidLibrary {
        namespace = "dag.khinkal.molapi.http.gson"
        compileSdk = libs.versions.android.compileSdk.get().toInt()
        minSdk = libs.versions.android.minSdk.get().toInt()

        compilerOptions {
            jvmTarget = JvmTarget.JVM_11
        }
        withHostTest {}
    }

    sourceSets {
        androidMain.dependencies {
            api(project(":molapi-http"))
            api(libs.gson)
        }
        named("androidHostTest").dependencies {
            implementation(libs.kotlin.test)
        }
    }
}
