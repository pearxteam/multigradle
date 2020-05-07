/*
 * Copyright © 2019 mrAppleXZ
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package net.pearx.multigradle.plugin

import net.pearx.multigradle.util.MultiGradleExtension
import net.pearx.multigradle.util.configureDokka
import net.pearx.multigradle.util.kotlinMpp
import net.pearx.multigradle.util.platform.PLATFORMS
import org.gradle.api.Project
import org.gradle.api.plugins.BasePlugin
import org.gradle.api.publish.plugins.PublishingPlugin
import org.gradle.api.tasks.Sync
import org.gradle.api.tasks.bundling.Jar
import org.gradle.kotlin.dsl.*
import org.jetbrains.dokka.gradle.DokkaPlugin
import org.jetbrains.dokka.gradle.DokkaTask
import org.jetbrains.kotlin.gradle.plugin.KotlinMultiplatformPluginWrapper

const val MULTIGRADLE_EXTENSION_NAME = "multigradle"

internal fun Project.enabledPlatforms() = (properties["enabledPlatforms"]?.toString()?.split(',')?.toSet() ?: PLATFORMS.keys).map { PLATFORMS[it] ?: throw NoSuchElementException("No such platform '$name'") }

internal fun Project.initializeMultiGradle() {
    preInit()
    val ext = MultiGradleExtension(this)
    extensions.add(MULTIGRADLE_EXTENSION_NAME, ext)
    for (platform in enabledPlatforms()) {
        platform.initialize(this)
        ext.initPlatform(platform)
    }
    postInit()
    ext.setupFromProperties()
}

private fun Project.preInit() {
    apply<KotlinMultiplatformPluginWrapper>()
    apply<DokkaPlugin>()
    apply<BasePlugin>()
    apply<PublishingPlugin>()

    extra.set("kotlin.tests.individualTaskReports", "false") // hack until https://youtrack.jetbrains.com/issue/KT-35202 is fixed

    repositories {
        jcenter()
    }

    tasks {
        register<Jar>("emptyJavadoc") {
            archiveClassifier.set("javadoc")
        }
    }

    kotlinMpp {
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

private fun Project.postInit() {
    for (target in kotlinMpp.targets.filterNot { it.name == "jvm" }) {
        target.mavenPublication {
            artifact(tasks["emptyJavadoc"])
        }
    }

    tasks {
        afterEvaluate {
            for (testTaskName in enabledPlatforms().flatMap { it.testTasks }) {
                val testTask = named(testTaskName) {
                    finalizedBy("${name}Prefix")
                }

                create<Sync>("${testTask.name}Prefix") {
                    onlyIf { project.the<MultiGradleExtension>().createPrefixedTestResults }
                    from("$buildDir/test-results/$testTaskName")
                    into("$buildDir/test-results-prefixed/$testTaskName")
                    include("**/*.xml")
                    // todo: make filtering not just string replacing
                    filter { line ->
                        line.replace(Regex("testsuite name=\"(.+?)\""), "testsuite name=\"$testTaskName $1\"")
                        line.replace(Regex("classname=\"(.+?)\""), "classname=\"$testTaskName $1\"")
                    }
                }
            }
        }

        named<DokkaTask>("dokka") {
            configureDokka(this, "html", "html", "Common", "JVM", "JS")
        }
    }
}