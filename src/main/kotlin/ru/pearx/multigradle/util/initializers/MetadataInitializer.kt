@file:JvmMultifileClass
@file:JvmName("InitializersKt")

package ru.pearx.multigradle.util.initializers

import org.gradle.api.Project
import org.gradle.kotlin.dsl.*
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

internal fun Project.metadataInitializer() {
    configure<KotlinMultiplatformExtension> {
        sourceSets {
            named("commonMain") {
                dependencies {
                    implementation(kotlin("stdlib-common"))
                }
            }

            named("commonTest") {
                dependencies {
                    implementation(kotlin("test-common"))
                    implementation(kotlin("test-annotations-common"))
                }
            }
        }
    }
}