rootProject.name = "MolApi"
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    repositories {
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositories {
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        mavenCentral()
    }
}

include(":sampleAndroidApp")
include(":sample")
include(":molapi-core")
project(":molapi-core").projectDir = file("molapi-core")
include(":molapi-http")
project(":molapi-http").projectDir = file("molapi-http")
include(":molapi-http-gson")
project(":molapi-http-gson").projectDir = file("molapi-http-gson")
include(":molapi-http-serialization")
project(":molapi-http-serialization").projectDir = file("molapi-http-serialization")
include(":molapi-http-editor")
project(":molapi-http-editor").projectDir = file("molapi-http-editor")
include(":molapi-http-ktor")
project(":molapi-http-ktor").projectDir = file("molapi-http-ktor")
include(":molapi-http-retrofit")
project(":molapi-http-retrofit").projectDir = file("molapi-http-retrofit")
include(":molapi-room")
project(":molapi-room").projectDir = file("molapi-room")
