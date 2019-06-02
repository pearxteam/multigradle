/*
 * Copyright © 2019 mrAppleXZ
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at https://mozilla.org/MPL/2.0/.
 */

@file:JvmMultifileClass
@file:JvmName("InitializersKt")

package net.pearx.multigradle.util.initializers

import net.pearx.multigradle.util.MultiGradleExtension
import net.pearx.multigradle.util.configureDokka
import net.pearx.multigradle.util.kotlinMpp
import org.gradle.api.Project
import org.gradle.api.plugins.BasePlugin
import org.gradle.api.publish.plugins.PublishingPlugin
import org.gradle.api.tasks.Sync
import org.gradle.api.tasks.bundling.Jar
import org.gradle.api.tasks.testing.Test
import org.gradle.kotlin.dsl.*
import org.jetbrains.dokka.gradle.DokkaPlugin
import org.jetbrains.dokka.gradle.DokkaTask
import org.jetbrains.kotlin.gradle.plugin.KotlinMultiplatformPluginWrapper

fun Project.preInit() {
    apply<KotlinMultiplatformPluginWrapper>()
    apply<DokkaPlugin>()
    apply<BasePlugin>()
    apply<PublishingPlugin>()

    repositories {
        jcenter()
    }

    tasks {
        register<Jar>("emptyJavadoc") {
            archiveClassifier.set("javadoc")
        }
    }
}

fun Project.postInit() {
    for (target in kotlinMpp.targets.filterNot { it.name == "jvm" }) {
        target.mavenPublication {
            artifact(tasks["emptyJavadoc"])
        }
    }

    tasks {
        for (target in kotlinMpp.targets.filterNot { it.name == "metadata" }) {
            val testTask = named<Test>("${target.name}Test") {
                finalizedBy("${name}Prefix")
            }

            create<Sync>("${testTask.name}Prefix") {
                onlyIf { project.the<MultiGradleExtension>().createPrefixedTestResults }
                val sourceSetName = target.compilations["test"].defaultSourceSet.name
                from("$buildDir/test-results/$sourceSetName")
                into("$buildDir/test-results-prefixed/$sourceSetName")
                include("**/*.xml")
                // todo: make filtering not just string replacing
                filter { line ->
                    line.replace(Regex("testsuite name=\"(.+?)\""), "testsuite name=\"${target.name} $1\"")
                    line.replace(Regex("classname=\"(.+?)\""), "classname=\"${target.name} $1\"")
                }
            }
        }

        named<DokkaTask>("dokka") {
            configureDokka(this, "html", "html", "Common", "JVM", "JS")
        }
    }
}