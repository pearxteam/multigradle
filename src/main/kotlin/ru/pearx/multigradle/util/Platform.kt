/*
 * Copyright © 2018 mrAppleXZ
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package ru.pearx.multigradle.util

import org.gradle.api.Project
import org.gradle.api.plugins.BasePlugin
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.invoke
import org.gradle.kotlin.dsl.repositories
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.dsl.KotlinCommonOptions
import org.jetbrains.kotlin.gradle.dsl.KotlinCompile


/*
 * Created by mrAppleXZ on 01.09.18.
 */
interface Platform<T : MultiGradleExtension>
{
    val name: String

    fun createExtension(): T

    fun Project.getExtension(): T

    fun Project.configureBefore(module: Project, root: Project)

    fun Project.configureAfter(module: Project, root: Project, extension: T)

    fun apply(platform: Project, module: Project, root: Project)
    {
        with(platform) {
            //AFTER
            afterEvaluate {
                val extension = getExtension()
                repositories {
                    jcenter()
                }

                version = extension.projectVersion

                tasks {
                    withType<KotlinCompile<KotlinCommonOptions>> {
                        if (!extension.kotlinExperimentalFeatures.isEmpty())
                        {
                            kotlinOptions.freeCompilerArgs = kotlinOptions.freeCompilerArgs.toMutableList().apply {
                                for (feature in extension.kotlinExperimentalFeatures)
                                    add("-Xuse-experimental=$feature")
                            }
                        }
                    }
                }
                configureAfter(module, root, extension)
            }

            //BEFORE
            extensions.add(MULTIGRADLE_EXTENSION_NAME, createExtension().load(this))
            apply<BasePlugin>()
            configureBefore(module, root)
        }
    }
}
