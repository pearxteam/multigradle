/*
 * Copyright © 2019 mrAppleXZ
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at https://mozilla.org/MPL/2.0/.
 */

@file:JvmMultifileClass
@file:JvmName("InitializersKt")

package net.pearx.multigradle.util.initializers

import com.moowork.gradle.node.NodeExtension
import com.moowork.gradle.node.NodePlugin
import com.moowork.gradle.node.npm.NpmTask
import com.moowork.gradle.node.task.NodeTask
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.tasks.Sync
import org.gradle.api.tasks.TaskProvider
import org.gradle.api.tasks.testing.Test
import org.gradle.kotlin.dsl.*
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import net.pearx.multigradle.util.MultiGradleExtension
import net.pearx.multigradle.util.invoke
import java.nio.file.Files

internal fun Project.jsInitializer() {
    apply<NodePlugin>()

    configure<NodeExtension> {
        download = true

        val cacheDir = file("$rootDir/.gradle/node")
        workDir = file("$cacheDir/nodejs")
        npmWorkDir = file("$cacheDir/npm")
        yarnWorkDir = file("$cacheDir/yarn")
        // todo move to $buildDir/node_modules when https://github.com/srs/gradle-node-plugin/issues/300 will be resolved
        nodeModulesDir = file("$projectDir/node_modules")
    }

    configure<KotlinMultiplatformExtension> {
        js {
            compilations.configureEach {
                kotlinOptions {
                    moduleKind = "umd"
                    sourceMap = true
                }
            }
            compilations["main"] {
                dependencies {
                    implementation(kotlin("stdlib-js"))
                }
            }

            compilations["test"] {
                dependencies {
                    implementation(kotlin("test-js"))
                }
            }
        }
    }

    tasks {
        val jsMainCompilation = the<KotlinMultiplatformExtension>().js().compilations["main"]
        val jsTestCompilation = the<KotlinMultiplatformExtension>().js().compilations["test"]

        val jsTestSyncNodeModules by registering(Sync::class) {
            dependsOn(jsTestCompilation.compileKotlinTask, jsMainCompilation.compileKotlinTask)
            from(jsTestCompilation.runtimeDependencyFiles.map { if (it.extension == "jar") zipTree(it) else it })
            include { it.path.endsWith(".js", true) }
            into("$buildDir/kotlinjs")
        }

        val jsTestInstallPackages by registering(NpmTask::class) {
            doFirst {
                setArgs(mutableListOf(
                    "install",
                    "mocha@${project.the<MultiGradleExtension>().mochaVersion}",
                    "mocha-jenkins-reporter@${project.the<MultiGradleExtension>().mochaJunitReporterVersion}")
                    .apply {
                        for ((name, version) in project.the<MultiGradleExtension>().npmPackages)
                            add("$name@$version")
                    })
            }
        }

        val jsTestRunMocha by registering(NodeTask::class) {
            dependsOn(jsTestInstallPackages, jsTestSyncNodeModules, jsTestCompilation.compileAllTaskName)
            setScript(file("${project.the<NodeExtension>().nodeModulesDir}/mocha/bin/mocha"))
            setEnvironment(mapOf("NODE_PATH" to "$buildDir/kotlinjs", "JUNIT_REPORT_PATH" to "$buildDir/test-results/jsTest/mocha.xml"))
            setArgs(listOf(jsTestCompilation.output.classesDirs.first().toString(), "--reporter", "mocha-jenkins-reporter"))
        }

        named<Test>("jsTest") {
            dependsOn(jsTestRunMocha)
        }

        listOf<TaskProvider<out Task>>(jsTestRunMocha, jsTestSyncNodeModules, jsTestInstallPackages, named("npmSetup"), named("nodeSetup")).forEach { task ->
            task.configure {
                onlyIf {
                    try {
                        Files.newDirectoryStream(jsTestCompilation.output.classesDirs.first().toPath()).use { f -> f.iterator().hasNext() }
                    }
                    catch(e: Exception) {
                        false
                    }
                }
            }
        }
    }
}