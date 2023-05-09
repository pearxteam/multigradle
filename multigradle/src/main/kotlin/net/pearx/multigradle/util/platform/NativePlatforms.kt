/*
 * Copyright © 2019-2020, PearX Team
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package net.pearx.multigradle.util.platform

import net.pearx.multigradle.util.ideaActive
import net.pearx.multigradle.util.kotlinMpp
import org.gradle.kotlin.dsl.*
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet
import org.jetbrains.kotlin.konan.target.HostManager

private val nativeSourceSetRelations = mapOf(
    "linux" to "posix",
    "mingw" to "posix",
    "macos" to "posix",
    "tvos" to "appleMobile",
    "ios" to "appleMobile",
    "watchos" to "appleMobile"
)

val LinuxX64Platform = nativePlatform("linuxX64", "linux", test = true)
val LinuxArm64Platform = nativePlatform("linuxArm64", "linux")
val AndroidNativeX86Platform = nativePlatform("androidNativeX86", "androidNative")
val AndroidNativeX64Platform = nativePlatform("androidNativeX64", "androidNative")
val AndroidNativeArm32Platform = nativePlatform("androidNativeArm32", "androidNative")
val AndroidNativeArm64Platform = nativePlatform("androidNativeArm64", "androidNative")
val TvOsArm64Platform = nativePlatform("tvosArm64", "tvos")
val TvOsSimulatorArm64Platform = nativePlatform("tvosSimulatorArm64", "tvos", test = true)
val TvOsX64Platform = nativePlatform("tvosX64", "tvos", test = true)
val IOsArm64Platform = nativePlatform("iosArm64", "ios")
val IOsX64Platform = nativePlatform("iosX64", "ios", test = true)
val IOsSimulatorArm64 = nativePlatform("iosSimulatorArm64", "ios", test = true)
val WatchOsArm32Platform = nativePlatform("watchosArm32", "watchos")
val WatchOsArm64Platform = nativePlatform("watchosArm64", "watchos")
val WatchOsX64Platform = nativePlatform("watchosX64", "watchos")
val WatchOsSimulatorArm64Platform = nativePlatform("watchosSimulatorArm64", "watchos")
val WatchOsDeviceArm64Platform = nativePlatform("watchosDeviceArm64", "watchos")
val MacOsX64Platform = nativePlatform("macosX64", "macos", test = true)
val MacOsArm64Platform = nativePlatform("macosArm64", "macos", test = true)
val MingwX64Platform = nativePlatform("mingwX64", "mingw", test = true)

private fun KotlinMultiplatformExtension.createConfiguration(name: String): KotlinSourceSet {
    return if (ideaActive) { // hack. idea doesn't go well with common native source sets as good as gradle does.
        targetFromPreset(presets[when {
            HostManager.hostIsLinux -> "linuxX64"
            HostManager.hostIsMac -> "macosX64"
            HostManager.hostIsMingw -> "mingwX64"
            else -> throw IllegalStateException("The host platform differs from Linux/macOS/Windows")
        }], name).compilations["main"].defaultSourceSet
    }
    else {
        sourceSets.create("${name}Test")
        sourceSets.create("${name}Main")
    }
}

private fun KotlinMultiplatformExtension.setupConfiguration(name: String): Pair<KotlinSourceSet, KotlinSourceSet> {
    var shouldSetup = false
    val mainSet = sourceSets.findByName("${name}Main") ?: createConfiguration(name).also { shouldSetup = true }
    val testSet = sourceSets["${name}Test"]
    if(shouldSetup && name != "native") {
        val dep = nativeSourceSetRelations[name] ?: "native"
        val (depMain, depTest) = setupConfiguration(dep)
        mainSet.dependsOn(depMain)
        testSet.dependsOn(depTest)
    }
    return mainSet to testSet
}

fun nativePlatform(name: String, dependsOn: String, test: Boolean = false) = platform(name, if (test) listOf("${name}Test") else listOf(), { PlatformConfig(it) }) {
    kotlinMpp {
        targetFromPreset(presets[name])
        val (mainSet, testSet) = setupConfiguration(dependsOn)
        sourceSets["${name}Main"].dependsOn(mainSet)
        sourceSets["${name}Test"].dependsOn(testSet)
    }
}