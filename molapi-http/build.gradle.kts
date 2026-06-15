import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidMultiplatformLibrary)
}

kotlin {
    explicitApi()

    listOf(
        iosArm64(),
        iosSimulatorArm64()
    )

    androidLibrary {
        namespace = "dag.khinkal.molapi.http"
        compileSdk = libs.versions.android.compileSdk.get().toInt()
        minSdk = libs.versions.android.minSdk.get().toInt()

        compilerOptions {
            jvmTarget = JvmTarget.JVM_11
        }
        withHostTest {}
    }

    sourceSets {
        commonMain.dependencies {
            api(project(":molapi-core"))
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}
