/*
 * Copyright © 2018 mrAppleXZ
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package ru.pearx.multigradle.util

import org.gradle.api.Project
import ru.pearx.multigradle.util.common.PlatformCommon
import ru.pearx.multigradle.util.js.PlatformJs
import ru.pearx.multigradle.util.jvm.PlatformJvm

//todo native
val PLATFORM_COMMON = PlatformCommon()
val PLATFORM_JS = PlatformJs()
val PLATFORM_JVM = PlatformJvm()

val PLATFORMS = listOf(PLATFORM_COMMON, PLATFORM_JS, PLATFORM_JVM)

fun platformOf(name: String): Platform<out MultiGradleExtension> = PLATFORMS.first { it.name == name } ?: throw IllegalArgumentException("The platform of name '$name' doesn't exist!")

fun platformOf(project: Project) = platformOf(project.projectDir.name)