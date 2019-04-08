@file:JvmMultifileClass
@file:JvmName("InitializersKt")

package ru.pearx.multigradle.util.initializers

import org.gradle.api.Project
import org.gradle.kotlin.dsl.*
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import ru.pearx.multigradle.util.MultiGradleExtension

internal fun Project.metadataInitializer(extension: MultiGradleExtension) {
    beforeEvaluate {
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
}