/*
 *  Copyright © 2018 mrAppleXZ
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this file,
 *  You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package ru.pearx.multigradle.util

import org.gradle.api.Project
import org.gradle.api.artifacts.Dependency
import org.gradle.api.artifacts.ProjectDependency

const val MULTIGRADLE_EXTENSION_NAME = "multigradle"

fun Project.subplatforms(init: Project.(platform: Platform) -> Unit)
{
    subprojects {
        beforeEvaluate {
            init(Platform.valueOfCodeName(name))
        }
    }
}

fun Project.mpdep(notation: String, unnamedPlatform: Platform?): Dependency
{
    val dep = dependencies.create(notation)
    val platform = Platform.valueOfCodeName(name)
    return dependencies.create(mapOf("group" to dep.group, "name" to (dep.name + if (platform == unnamedPlatform) "" else "-${platform.codeName}"), "version" to dep.version))
}

fun Project.mpdep(module: ProjectDependency): Project = mpdep(module.dependencyProject)

fun Project.mpdep(module: Project): Project = module.project(name)