/*
 * Copyright © 2018 mrAppleXZ
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package ru.pearx.multigradle.util.js

import com.moowork.gradle.node.NodeExtension
import com.moowork.gradle.node.NodePlugin
import com.moowork.gradle.node.npm.NpmTask
import com.moowork.gradle.node.task.NodeTask
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.api.tasks.Sync
import org.gradle.api.tasks.testing.Test
import org.gradle.kotlin.dsl.*
import org.jetbrains.kotlin.gradle.plugin.KotlinPlatformJsPlugin
import org.jetbrains.kotlin.gradle.tasks.Kotlin2JsCompile
import ru.pearx.multigradle.util.PLATFORM_COMMON
import ru.pearx.multigradle.util.Platform
import java.nio.file.Files

class PlatformJs : Platform<MultiGradleJsExtension>
{
    override val name: String
        get() = "js"

    override fun createExtension(): MultiGradleJsExtension = MultiGradleJsExtension()

    override fun Project.getExtension(): MultiGradleJsExtension = the()

    override fun Project.configureBefore(module: Project, root: Project)
    {
        apply<KotlinPlatformJsPlugin>()
        apply<NodePlugin>()
    }

    override fun Project.configureAfter(module: Project, root: Project, extension: MultiGradleJsExtension)
    {
        configure<NodeExtension> {
            version = extension.nodejsVersion
            npmVersion = extension.npmVersion
            download = true

            val cacheDir = file("$rootDir/.gradle/node")
            workDir = file("$cacheDir/nodejs")
            npmWorkDir = file("$cacheDir/npm")
            yarnWorkDir = file("$cacheDir/yarn")
            nodeModulesDir = file("$cacheDir/node_modules")
        }

        dependencies {
            "expectedBy"(module.project(PLATFORM_COMMON.name))

            "compile"(kotlin("stdlib-js"))

            "testCompile"(kotlin("test-js"))
        }

        tasks {
            withType<Kotlin2JsCompile> {
                kotlinOptions.moduleKind = "umd"
            }

            create<Sync>("syncNodeModules") {
                dependsOn("compileKotlin2Js", "compileTestKotlin2Js")
                from(this@configureAfter.the<SourceSetContainer>()["main"].output)
                doFirst {
                    configurations["testCompile"].forEach { from(zipTree(it)) }
                }
                include { it.path.endsWith(".js", true) }
                into("$buildDir/node_modules")
            }

            create<NpmTask>("installModules") {
                val lst = mutableListOf("install", "mocha@${extension.mochaVersion}")
                for (nm in extension.nodeModules)
                    lst.add(nm.toString())
                setArgs(lst)
            }

            create<NodeTask>("runMocha") {
                dependsOn("installModules", "syncNodeModules", "compileTestKotlin2Js")
                setScript(file("$rootDir/.gradle/node/node_modules/mocha/bin/mocha"))
                setArgs(listOf(getByName<Kotlin2JsCompile>("compileTestKotlin2Js").destinationDir))
            }

            named<Test>("test") {
                dependsOn("runMocha")
            }

            for (dep in listOf("runMocha", "npmSetup", "nodeSetup", "installModules", "syncNodeModules"))
            {
                getByName<Task>(dep).onlyIf {
                    val path = getByName<Kotlin2JsCompile>("compileTestKotlin2Js").destinationDir.toPath()
                    if (Files.exists(path))
                        Files.newDirectoryStream(path).use { f -> f.iterator().hasNext() }
                    else
                        false
                }
            }
        }
    }
}