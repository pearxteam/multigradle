/*
 *  Copyright © 2018 mrAppleXZ
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this file,
 *  You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package ru.pearx.multigradle.util

import org.gradle.api.Project
import org.gradle.kotlin.dsl.the

const val MULTIGRADLE_EXTENSION_NAME = "multigradle"

fun Project.multiplatformDependencies(init: MultiplatformDependenciesScope.() -> Unit) =
        MultiplatformDependenciesScope(this).apply { init() }

class MultiplatformDependenciesScope(private val project: Project)
{
    operator fun String.invoke(projectDependency: Project)
    {
        with(project) {
            for (platform in Platform.values())
            {
                project(platform.codeName).dependencies.add(this@invoke, projectDependency.project(platform.codeName))
            }
        }
    }

    operator fun String.invoke(notation: String, versionProperty: String, unnamedPlatform: Platform?)
    {
        with(project) {
            for (platform in Platform.values())
            {
                with(project(platform.codeName).dependencies) {
                    //todo Do the name swapping using mutable dependencies (if they exist at all, of course)?
                    val dep = create(notation)
                    add(this@invoke, "${dep.group}:${dep.name}${if (platform == unnamedPlatform) "" else "-${platform.codeName}"}:${properties[versionProperty]}")
                }
            }
        }
    }
}