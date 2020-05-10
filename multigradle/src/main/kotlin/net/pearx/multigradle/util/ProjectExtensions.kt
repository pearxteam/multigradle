/*
 * Copyright © 2019-2020, PearX Team
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package net.pearx.multigradle.util

import org.gradle.api.Project
import org.gradle.api.artifacts.dsl.RepositoryHandler
import org.gradle.kotlin.dsl.*
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilation
import java.net.URI

fun RepositoryHandler.kotlinDev() {
    maven { url = URI("https://dl.bintray.com/kotlin/kotlin-dev/") }
}

fun RepositoryHandler.kotlinEap() {
    maven { url = URI("https://dl.bintray.com/kotlin/kotlin-eap/") }
}

inline operator fun <T : KotlinCompilation<*>> T.invoke(block: T.() -> Unit) = block()

val Project.kotlinMpp
    get() = the<KotlinMultiplatformExtension>()

fun Project.kotlinMpp(block: KotlinMultiplatformExtension.() -> Unit) {
    configure<KotlinMultiplatformExtension> { block() }
}