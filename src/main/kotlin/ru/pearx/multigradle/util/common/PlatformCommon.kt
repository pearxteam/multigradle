/*
 * Copyright © 2019 mrAppleXZ
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package ru.pearx.multigradle.util.common

import org.gradle.api.Project
import org.gradle.kotlin.dsl.*
import org.jetbrains.kotlin.gradle.plugin.KotlinPlatformCommonPlugin
import org.jetbrains.kotlin.js.translate.context.Namer.kotlin
import ru.pearx.multigradle.util.MultiGradleExtension
import ru.pearx.multigradle.util.Platform

class PlatformCommon : Platform<MultiGradleExtension>
{
    override val name: String
        get() = "common"

    override fun createExtension(): MultiGradleExtension = MultiGradleExtension()

    override fun Project.getExtension(): MultiGradleExtension = the()

    override fun Project.configureBefore(module: Project, root: Project)
    {
        apply<KotlinPlatformCommonPlugin>()
    }

    override fun Project.configureAfter(module: Project, root: Project, extension: MultiGradleExtension)
    {
        dependencies {
            "compile"(kotlin("stdlib-common"))

            "testCompile"(kotlin("test-common"))
            "testCompile"(kotlin("test-annotations-common"))
        }
    }
}