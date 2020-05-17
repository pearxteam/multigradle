/*
 * Copyright © 2019-2020, PearX Team
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package net.pearx.multigradle.util.platform

import net.pearx.multigradle.util.ideaActive
import net.pearx.multigradle.util.kotlinMpp
import org.gradle.api.Project
import org.gradle.kotlin.dsl.*
import org.jetbrains.kotlin.konan.target.HostManager

val LinuxX64Platform = nativePlatform("linuxX64", true)
val LinuxArm64Platform = nativePlatform("linuxArm64")
val LinuxArm32HfpPlatform = nativePlatform("linuxArm32Hfp")
val LinuxMips32Platform = nativePlatform("linuxMips32")
val LinuxMipsel32Platform = nativePlatform("linuxMipsel32")
val AndroidNativeArm32Platform = nativePlatform("androidNativeArm32")
val AndroidNativeArm64Platform = nativePlatform("androidNativeArm64")
val TvOsArm32Platform = nativePlatform("tvosArm64")
val TvOsX64Platform = nativePlatform("tvosX64", true)
val IOsArm32Platform = nativePlatform("iosArm32")
val IOsArm64Platform = nativePlatform("iosArm64")
val IOsX64Platform = nativePlatform("iosX64", true)
val WatchOsArm32Platform = nativePlatform("watchosArm32")
val WatchOsArm64Platform = nativePlatform("watchosArm64")
val WatchOsX86Platform = nativePlatform("watchosX86", true)
val MacOsX64Platform = nativePlatform("macosX64", true)
val MingwX64Platform = nativePlatform("mingwX64", true)
val MingwX86Platform = nativePlatform("mingwX86")
val Wasm32Platform = nativePlatform("wasm32")

fun Project.setupNative() {
    if (!extra.has("mgnative")) {
        extra["mgnative"] = true

        kotlinMpp {
            if (ideaActive) { // hack. idea doesn't go well with common native source sets as good as gradle does.
                targetFromPreset(presets[when {
                    HostManager.hostIsLinux -> "linuxX64"
                    HostManager.hostIsMac -> "macosX64"
                    HostManager.hostIsMingw -> "mingwX64"
                    else -> throw IllegalStateException("The host platform differs from Linux/macOS/Windows")
                }], "native")
            }
            else {
                sourceSets {
                    create("nativeMain")
                    create("nativeTest")
                }
            }
        }
    }
}

fun nativePlatform(name: String, test: Boolean = false) = platform(name, if (test) listOf("${name}Test") else listOf(), { PlatformConfig(it) }) {
    kotlinMpp {
        targetFromPreset(presets[name])
        setupNative()
        sourceSets["${name}Main"].dependsOn(sourceSets["nativeMain"])
        sourceSets["${name}Test"].dependsOn(sourceSets["nativeTest"])
    }
}