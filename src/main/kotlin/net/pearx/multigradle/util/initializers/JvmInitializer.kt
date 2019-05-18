@file:JvmMultifileClass
@file:JvmName("InitializersKt")

package net.pearx.multigradle.util.initializers

import org.gradle.api.Project
import org.gradle.api.tasks.testing.Test
import org.gradle.kotlin.dsl.*
import org.gradle.testing.jacoco.plugins.JacocoPlugin
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import net.pearx.multigradle.util.MultiGradleExtension
import net.pearx.multigradle.util.invoke

internal fun Project.jvmInitializer() {
    apply<JacocoPlugin>()

    configure<KotlinMultiplatformExtension> {
        jvm {
            afterEvaluate {
                val extension = project.the<MultiGradleExtension>()

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

    tasks {
        named<Test>("jvmTest") {
            @Suppress("UnstableApiUsage")
            useJUnitPlatform()
        }
    }
}