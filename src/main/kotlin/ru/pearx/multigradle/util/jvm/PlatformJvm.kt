/*
 * Copyright © 2018 mrAppleXZ
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package ru.pearx.multigradle.util.jvm

import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginConvention
import org.gradle.api.tasks.testing.Test
import org.gradle.kotlin.dsl.*
import org.gradle.testing.jacoco.plugins.JacocoPluginExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinPlatformJvmPlugin
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import ru.pearx.multigradle.util.PLATFORM_COMMON
import ru.pearx.multigradle.util.Platform

class PlatformJvm : Platform<MultiGradleJvmExtension>
{
    override val name: String
        get() = "jvm"


    override fun createExtension(): MultiGradleJvmExtension = MultiGradleJvmExtension()

    override fun Project.getExtension(): MultiGradleJvmExtension = the()

    override fun Project.configureBefore(module: Project, root: Project)
    {
        apply<KotlinPlatformJvmPlugin>()
        apply(plugin = "jacoco")
    }

    override fun Project.configureAfter(module: Project, root: Project, extension: MultiGradleJvmExtension)
    {
        configure<JavaPluginConvention> {
            sourceCompatibility = JavaVersion.values().first { it.toString() == extension.javaVersionFull }
        }

        configure<JacocoPluginExtension> {
            toolVersion = extension.jacocoVersion
        }

        dependencies {
            "expectedBy"(module.project(PLATFORM_COMMON.name))

            "compile"(kotlin("stdlib-jdk${extension.javaVersion}"))

            "testCompile"(kotlin("test-annotations-common"))
            "testCompile"(kotlin("test-junit5"))
            "testCompile"("org.junit.jupiter:junit-jupiter-api:${extension.junitJupiterVersion}")
            "testRuntime"("org.junit.jupiter:junit-jupiter-engine:${extension.junitJupiterVersion}")
        }

        tasks {
            withType<KotlinCompile> {
                kotlinOptions.jvmTarget = extension.javaVersionFull
            }
            named<Test>("test") {
                useJUnitPlatform()
            }
        }
    }
}