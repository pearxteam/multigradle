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
import net.pearx.multigradle.util.invoke
import net.pearx.multigradle.util.kotlinMpp
import org.gradle.api.Project
import org.gradle.api.tasks.bundling.Jar
import org.gradle.api.tasks.testing.Test
import org.gradle.kotlin.dsl.*
import org.gradle.testing.jacoco.plugins.JacocoPlugin
import org.jetbrains.dokka.gradle.DokkaTask

internal fun Project.jvmInitializer() {
    apply<JacocoPlugin>()

    val dokkaJavadoc by tasks.registering(DokkaTask::class) {
        configureDokka(this, "javadoc", "javadoc", "JVM")
    }
    val javadocJar by tasks.registering(Jar::class) {
        dependsOn(dokkaJavadoc)
        archiveClassifier.set("javadoc")
        archiveAppendix.set("jvm")
        from(dokkaJavadoc.get().outputDirectory)
    }

    kotlinMpp {
        jvm {
            afterEvaluate {
                val extension = project.the<MultiGradleExtension>()

                compilations["main"] {
                    mavenPublication {
                        artifact(javadocJar.get())
                    }
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
            reports.junitXml.isEnabled = true
            useJUnitPlatform()
        }
    }
}