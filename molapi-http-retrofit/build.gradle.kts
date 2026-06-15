import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidMultiplatformLibrary)
}

kotlin {
    explicitApi()

    androidLibrary {
        namespace = "dag.khinkal.molapi.http.retrofit"
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
            api(libs.okhttp)
            api(libs.retrofit)
        }
        named("androidHostTest").dependencies {
            implementation(project(":molapi-http-gson"))
            implementation(libs.kotlin.test)
            implementation(libs.okhttp.mockwebserver)
            implementation(libs.retrofit.converter.gson)
        }
    }
}
