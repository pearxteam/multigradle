/*
 *  Copyright © 2018 mrAppleXZ
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this file,
 *  You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package ru.pearx.multigradle.util.platform

import com.moowork.gradle.node.NodeExtension
import com.moowork.gradle.node.NodePlugin
import com.moowork.gradle.node.npm.NpmTask
import com.moowork.gradle.node.task.NodeTask
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.internal.AbstractTask
import org.gradle.api.plugins.BasePlugin
import org.gradle.api.plugins.BasePluginConvention
import org.gradle.api.plugins.JavaPluginConvention
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.api.tasks.Sync
import org.gradle.api.tasks.testing.Test
import org.gradle.kotlin.dsl.*
import org.gradle.testing.jacoco.plugins.JacocoPluginExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinCommonOptions
import org.jetbrains.kotlin.gradle.plugin.KotlinPlatformCommonPlugin
import org.jetbrains.kotlin.gradle.plugin.KotlinPlatformJsPlugin
import org.jetbrains.kotlin.gradle.plugin.KotlinPlatformJvmPlugin
import org.jetbrains.kotlin.gradle.tasks.Kotlin2JsCompile
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import ru.pearx.multigradle.util.MultiGradleExtension
import java.nio.file.Files


/*
 * Created by mrAppleXZ on 01.09.18.
 */
enum class Platform(val codeName: String)
{
    //todo Native support
    COMMON("common")
    {
        override fun configureBefore(platform: Project, module: Project, root: Project)
        {
            super.configureBefore(platform, module, root)

            with(platform) {
                apply<KotlinPlatformCommonPlugin>()
            }
        }

        override fun configureAfter(platform: Project, module: Project, root: Project, extension: MultiGradleExtension)
        {
            super.configureAfter(platform, module, root, extension)

            with(platform) {
                dependencies {
                    "compile"(kotlin("stdlib-common"))

                    "testCompile"(kotlin("test-common"))
                    "testCompile"(kotlin("test-annotations-common"))
                }
            }
        }
    },
    JS("js")
    {
        override fun configureBefore(platform: Project, module: Project, root: Project)
        {
            super.configureBefore(platform, module, root)

            with(platform) {
                apply<KotlinPlatformJsPlugin>()
                apply<NodePlugin>()
            }
        }

        override fun configureAfter(platform: Project, module: Project, root: Project, extension: MultiGradleExtension)
        {
            super.configureAfter(platform, module, root, extension)

            with(platform) {
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
                    "expectedBy"(module.project(COMMON.codeName))

                    "compile"(kotlin("stdlib-js"))

                    "testCompile"(kotlin("test-js"))
                }

                tasks {
                    withType<Kotlin2JsCompile> {
                        kotlinOptions.moduleKind = "umd"
                    }

                    create<Sync>("syncNodeModules") {
                        dependsOn("compileKotlin2Js", "compileTestKotlin2Js")
                        from(this@with.the<SourceSetContainer>()["main"].output)
                        doFirst {
                            configurations["testCompile"].forEach { from(zipTree(it)) }
                        }
                        include { it.path.endsWith(".js", true) }
                        into("$buildDir/node_modules")
                    }

                    create<NpmTask>("installModules") {
                        var lst = mutableListOf("install", "mocha@${extension.mochaVersion}")
                        for(nm in extension.nodeModules)
                            lst.add(nm.toString())
                        setArgs(lst)
                    }

                    create<NodeTask>("runMocha") {
                        onlyIf
                        dependsOn("installModules", "syncNodeModules", "compileTestKotlin2Js")
                        setScript(file("$rootDir/.gradle/node/node_modules/mocha/bin/mocha"))
                        setArgs(listOf(getByName<Kotlin2JsCompile>("compileTestKotlin2Js").destinationDir))
                    }

                    named<Test>("test") {
                        dependsOn("runMocha")
                    }

                    for(dep in listOf("runMocha", "npmSetup", "nodeSetup", "installModules", "syncNodeModules"))
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
    },
    JVM("jvm")
    {
        override fun configureBefore(platform: Project, module: Project, root: Project)
        {
            super.configureBefore(platform, module, root)

            with(platform) {
                apply<KotlinPlatformJvmPlugin>()
                apply(plugin = "jacoco")
            }
        }

        override fun configureAfter(platform: Project, module: Project, root: Project, extension: MultiGradleExtension)
        {
            super.configureAfter(platform, module, root, extension)

            with(platform) {
                configure<JavaPluginConvention> {
                    sourceCompatibility = JavaVersion.values().first { it.toString() == extension.javaVersionFull }
                }

                configure<JacocoPluginExtension> {
                    toolVersion = extension.jacocoVersion
                }

                dependencies {
                    "expectedBy"(module.project(COMMON.codeName))

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
    };

    fun apply(platform: Project, module: Project, root: Project)
    {
        configureBefore(platform, module, root)
        platform.afterEvaluate {
            configureAfter(platform, module, root, the())
        }
    }

    open fun configureBefore(platform: Project, module: Project, root: Project)
    {
        with(platform) {
            apply<BasePlugin>()
        }
    }

    open fun configureAfter(platform: Project, module: Project, root: Project, extension: MultiGradleExtension)
    {
        with(platform) {
            repositories {
                jcenter()
                if (extension.kotlinDevRepo)
                    maven { url = uri("https://dl.bintray.com/kotlin/kotlin-dev/") }
            }

            configure<BasePluginConvention> {
                //carbidelin-core-jvm
                archivesBaseName = "${root.name}-${module.name}-$codeName"
                version = extension.projectVersion
            }

            tasks {
                withType<org.jetbrains.kotlin.gradle.dsl.KotlinCompile<KotlinCommonOptions>> {
                    if (!extension.kotlinExperimentalFeatures.isEmpty())
                    {
                        kotlinOptions.freeCompilerArgs = kotlinOptions.freeCompilerArgs.toMutableList().apply {
                            for (feature in extension.kotlinExperimentalFeatures)
                                add("-Xuse-experimental=$feature")
                        }
                    }
                }
            }
        }
    }

    companion object
    {
        fun valueOfCodeName(codeName: String) = values().firstOrNull { it.codeName == codeName }
                ?: throw IllegalArgumentException("The platform of code name '$codeName' doesn't exist!")
    }
}