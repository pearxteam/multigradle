/*
 * Copyright © 2019 mrAppleXZ
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at https://mozilla.org/MPL/2.0/.
 */

@file:JvmMultifileClass
@file:JvmName("InitializersKt")

package net.pearx.multigradle.util.initializers

import net.pearx.multigradle.plugin.MULTIGRADLE_EXTENSION_NAME
import net.pearx.multigradle.util.MultiGradleExtension
import org.gradle.api.Project

internal typealias Initializer = Project.() -> Unit

internal val initializers: Map<String, Initializer> = mapOf(
    "js" to Project::jsInitializer,
    "jvm" to Project::jvmInitializer,
    "metadata" to Project::metadataInitializer
)

internal fun Project.initializeMultiGradle() {
    preInit()
    for ((_, initializer) in initializers) {
        initializer()
    }
    postInit()
    val extension = MultiGradleExtension(this).load(this)
    extensions.add(MULTIGRADLE_EXTENSION_NAME, extension)
}