/*
 * Copyright © 2019-2020, PearX Team
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package net.pearx.multigradle.util.platform

import com.android.build.gradle.LibraryExtension
import com.android.build.gradle.LibraryPlugin
import com.android.build.gradle.tasks.GenerateBuildConfig
import net.pearx.multigradle.util.alias
import net.pearx.multigradle.util.kotlinMpp
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.plugins.ExtensionAware
import org.gradle.kotlin.dsl.*
import org.gradle.plugins.ide.idea.model.IdeaModel
import org.jetbrains.gradle.ext.ProjectSettings
import org.jetbrains.gradle.ext.TaskTriggersConfig

class AndroidPlatformConfig(project: Project) : PlatformConfig(project) {
    var compileSdkVersion: String by project.the<LibraryExtension>().alias({ compileSdkVersion!! }, { compileSdkVersion = it })
    var buildToolsVersion: String by project.the<LibraryExtension>().alias({ buildToolsVersion }, { buildToolsVersion = it })
    lateinit var junitVersion: String
}

val AndroidPlatform = platform("android", listOf("testReleaseUnitTest", "testDebugUnitTest"), { AndroidPlatformConfig(it) }) { ext ->
    apply<LibraryPlugin>()

    val manifestPath = file("$buildDir/AndroidManifest.xml")

    repositories {
        google()
    }

    kotlinMpp {
        sourceSets {
            afterEvaluate {
                named("androidMain") {
                    dependencies {
                        implementation(kotlin("stdlib"))
                    }
                }
                named("androidTest") {
                    dependencies {
                        implementation(kotlin("test-annotations-common"))
                        implementation(kotlin("test-junit"))
                        implementation("junit:junit:${ext().junitVersion}")
                    }
                }
            }
        }
        android {
            publishLibraryVariants("release", "debug")
        }
    }

    configure<LibraryExtension> {
        defaultConfig {

        }
        buildTypes.configureEach {
            sourceSets.configureEach {
                manifest.srcFile(manifestPath)
            }
        }
    }


    tasks {
        val generateAndroidManifest by registering(Task::class) {
            with(inputs) {
                property("group", project.group)
                property("name", project.name)
            }
            outputs.file(manifestPath)
            doLast {
                val pkg = mutableListOf<String>().also {
                    it += project.group.toString()
                    it += project.name.split('-').drop(1)
                }.joinToString(".") // todo a bit hacky :(
                manifestPath.writeText("""
                <?xml version="1.0" encoding="utf-8"?>
                <manifest xmlns:android="http://schemas.android.com/apk/res/android"
                          package="$pkg"/>
                """.trimIndent())
            }
        }

        configure<IdeaModel> {
            (project as ExtensionAware).configure<ProjectSettings> {
                (this as ExtensionAware).configure<TaskTriggersConfig> {
                    beforeSync(generateAndroidManifest)
                }
            }
        }

        withType<GenerateBuildConfig> {
            dependsOn(generateAndroidManifest)
        }
    }
}