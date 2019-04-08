@file:JvmMultifileClass
@file:JvmName("InitializersKt")

package ru.pearx.multigradle.util.initializers

import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginConvention
import org.gradle.kotlin.dsl.*
import org.gradle.testing.jacoco.plugins.JacocoPlugin
import org.gradle.testing.jacoco.plugins.JacocoPluginExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import ru.pearx.multigradle.util.MultiGradleExtension
import ru.pearx.multigradle.util.invoke

internal fun Project.jvmInitializer(extension: MultiGradleExtension) {
    apply<JacocoPlugin>()

    beforeEvaluate {
        configure<JavaPluginConvention> {
            sourceCompatibility = JavaVersion.values().first { it.toString() == extension.javaVersionFull }
        }

        configure<JacocoPluginExtension> {
            toolVersion = extension.jacocoVersion
        }

        configure<KotlinMultiplatformExtension> {
            jvm {
                compilations.configureEach {
                    kotlinOptions {
                        jvmTarget = extension.javaVersionFull
                    }
                }

                compilations["main"] {
                    dependencies {
                        implementation(kotlin("stdlib-jdk${extension.javaVersion}"))
                    }
                }

                compilations["test"] {
                    dependencies {
                        implementation(kotlin("test-annotations-common"))
                        implementation(kotlin("test-junit5"))
                        implementation("org.junit.jupiter:junit-jupiter-api:${extension.junitJupiterVersion}")
                        runtimeOnly("org.junit.jupiter:junit-jupiter-engine:${extension.junitJupiterVersion}")
                    }
                }
            }
        }
    }
}