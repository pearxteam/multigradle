/*
 * Copyright © 2019-2020, PearX Team
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package net.pearx.multigradle.util.platform

import net.pearx.multigradle.util.MultiGradleExtension
import org.gradle.api.Project
import org.gradle.kotlin.dsl.*

interface Platform<T : PlatformConfig> {
    val name: String
    val testTasks: List<String>
    fun initialize(project: Project)
    fun createConfig(project: Project): T
}

open class PlatformConfig(val project: Project)

inline fun <T : PlatformConfig> platform(name: String, testTasks: List<String>, crossinline config: (Project) -> T, crossinline initializer: Project.(() -> T) -> Unit): Platform<T> = object : Platform<T> {
    override val name: String = name

    override val testTasks: List<String> = testTasks

    override fun initialize(project: Project) {
        project.initializer { project.the<MultiGradleExtension>().platform(this) }
    }

    override fun createConfig(project: Project): T {
        return config(project)
    }

    override fun equals(other: Any?): Boolean {
        return (other as? Platform<*>)?.name?.equals(name) ?: false
    }

    override fun hashCode(): Int {
        return name.hashCode()
    }

    override fun toString(): String {
        return "Platform($name)"
    }
}

val PLATFORMS = setOf(
    JsPlatform,
    JvmPlatform,
    AndroidPlatform,
    LinuxX64Platform,
    LinuxArm64Platform,
    AndroidNativeArm32Platform,
    AndroidNativeArm64Platform,
    AndroidNativeX86Platform,
    AndroidNativeX64Platform,
    TvOsArm64Platform,
    TvOsSimulatorArm64Platform,
    TvOsX64Platform,
    IOsArm64Platform,
    IOsX64Platform,
    IOsSimulatorArm64,
    WatchOsArm32Platform,
    WatchOsArm64Platform,
    WatchOsX64Platform,
    WatchOsSimulatorArm64Platform,
    WatchOsDeviceArm64Platform,
    MacOsArm64Platform,
    MacOsX64Platform,
    MingwX64Platform,
    WasmPlatform
).associateBy { p -> p.name }