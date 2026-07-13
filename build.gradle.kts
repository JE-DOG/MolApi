import com.vanniktech.maven.publish.MavenPublishBaseExtension

plugins {
    // this is necessary to avoid the plugins to be loaded multiple times
    // in each subproject's classloader
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.androidMultiplatformLibrary) apply false
    alias(libs.plugins.composeMultiplatform) apply false
    alias(libs.plugins.composeCompiler) apply false
    alias(libs.plugins.kotlinMultiplatform) apply false
    alias(libs.plugins.kotlinSerialization) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.androidx.room) apply false
    alias(libs.plugins.mavenPublish) apply false
}

val molapiLibraryProjects = setOf(
    ":molapi-core",
    ":molapi-http",
    ":molapi-http-android-assets",
    ":molapi-http-editor",
    ":molapi-http-gson",
    ":molapi-http-ktor",
    ":molapi-http-retrofit",
    ":molapi-http-serialization",
    ":molapi-room",
)

val molapiPublicationDependencies = mapOf(
    ":molapi-core" to emptySet(),
    ":molapi-http" to setOf(":molapi-core"),
    ":molapi-http-android-assets" to setOf(":molapi-http"),
    ":molapi-http-editor" to setOf(":molapi-http"),
    ":molapi-http-gson" to setOf(":molapi-http"),
    ":molapi-http-ktor" to setOf(":molapi-http"),
    ":molapi-http-retrofit" to setOf(":molapi-http"),
    ":molapi-http-serialization" to setOf(":molapi-http"),
    ":molapi-room" to setOf(":molapi-core"),
)

fun mavenVersionEnvironmentVariable(projectPath: String): String {
    val moduleName = projectPath.removePrefix(":molapi-")
    return "MOLAPI_${moduleName.replace('-', '_').uppercase()}_MAVEN_VERSION"
}

fun publicationDependencyClosure(projectPath: String): Set<String> =
    setOf(projectPath) + molapiPublicationDependencies
        .getValue(projectPath)
        .flatMap(::publicationDependencyClosure)

configure(subprojects.filter { it.path in molapiLibraryProjects }) {
    pluginManager.apply("com.vanniktech.maven.publish")

    val projectPath = path
    val moduleName = name.removePrefix("molapi-")
    val versionEnvironmentVariable = mavenVersionEnvironmentVariable(projectPath)
    val requestedVersion = providers.environmentVariable(versionEnvironmentVariable)
    val requiredVersionEnvironmentVariables = publicationDependencyClosure(projectPath)
        .map(::mavenVersionEnvironmentVariable)
    val publicationVersion = requestedVersion.orElse("0.0.0-SNAPSHOT").get()
    val publicationName = "MolApi " + moduleName
        .split('-')
        .joinToString(" ") { it.replaceFirstChar(Char::uppercase) }

    group = providers.environmentVariable("MOLAPI_MAVEN_GROUP")
        .orElse("io.github.je-dog")
        .get()
    version = publicationVersion

    extensions.getByType<PublishingExtension>().publications
        .withType<MavenPublication>()
        .configureEach {
            group = project.group.toString()
            version = publicationVersion

            pom {
                name.set(publicationName)
                description.set("$publicationName module for the Kotlin Multiplatform MolApi library.")
                url.set("https://github.com/JE-DOG/MolApi")

                licenses {
                    license {
                        name.set("Apache License, Version 2.0")
                        url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
                        distribution.set("repo")
                    }
                }

                developers {
                    developer {
                        id.set("je-dog")
                        name.set("JE-DOG")
                        url.set("https://github.com/JE-DOG")
                    }
                }

                scm {
                    connection.set("scm:git:https://github.com/JE-DOG/MolApi.git")
                    developerConnection.set("scm:git:ssh://git@github.com/JE-DOG/MolApi.git")
                    url.set("https://github.com/JE-DOG/MolApi")
                }
            }
        }

    extensions.configure<MavenPublishBaseExtension> {
        publishToMavenCentral()
        signAllPublications()
    }

    tasks.withType<PublishToMavenRepository>().configureEach {
        if (name.endsWith("ToMavenCentralRepository")) {
            doFirst {
                val missingVersionEnvironmentVariables = requiredVersionEnvironmentVariables
                    .filter { providers.environmentVariable(it).orNull.isNullOrBlank() }
                check(missingVersionEnvironmentVariables.isEmpty()) {
                    "Set ${missingVersionEnvironmentVariables.joinToString()} before publishing $projectPath to Maven Central."
                }
            }
        }
    }
}
