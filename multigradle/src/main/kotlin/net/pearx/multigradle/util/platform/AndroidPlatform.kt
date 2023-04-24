/*
 * Copyright © 2019-2020, PearX Team
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package net.pearx.multigradle.util.platform

import com.android.build.gradle.LibraryExtension
import com.android.build.gradle.LibraryPlugin
import net.pearx.multigradle.util.alias
import net.pearx.multigradle.util.kotlinMpp
import org.gradle.api.Project
import org.gradle.kotlin.dsl.*

class AndroidPlatformConfig(project: Project) : PlatformConfig(project) {
    var compileSdkVersion: String by project.the<LibraryExtension>().alias({ compileSdkVersion!! }, { compileSdkVersion = it })
    var buildToolsVersion: String by project.the<LibraryExtension>().alias({ buildToolsVersion }, { buildToolsVersion = it })
    lateinit var junitVersion: String
    private lateinit var _javaVersion: String
    var javaVersion: String
        get() = _javaVersion
        set(value) {
            _javaVersion = value
            project.the<LibraryExtension>().compileOptions.targetCompatibility("1.$value")
            project.the<LibraryExtension>().compileOptions.sourceCompatibility("1.$value")
            project.kotlinMpp.android {
                compilations.configureEach { kotlinOptions.jvmTarget = "1.$value" }
            }
        }
}

val Project.androidPackageName
    get() = mutableListOf<String>().also {
        it += project.group.toString()
        it += project.name.split('-').drop(1)
    }.joinToString(".") // todo a bit hacky :(

val AndroidPlatform = platform("android", listOf("testReleaseUnitTest", "testDebugUnitTest"), { AndroidPlatformConfig(it) }) { ext ->
    apply<LibraryPlugin>()

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
                named("androidUnitTest") {
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
        namespace = project.androidPackageName
    }
}