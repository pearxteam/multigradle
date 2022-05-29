/*
 * Copyright © 2019-2020, PearX Team
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package net.pearx.multigradle.util

import org.gradle.api.Project
import org.gradle.api.file.FileCollection
import org.gradle.kotlin.dsl.*
import org.jetbrains.dokka.gradle.DokkaTask
import org.jetbrains.kotlin.gradle.dsl.KotlinProjectExtension

internal fun Project.findSourceDirectories(endsWith: String): FileCollection {
    return files(the<KotlinProjectExtension>().sourceSets.filter { it.name.endsWith(endsWith) }.map { it.kotlin.sourceDirectories }).filter { it.isDirectory }
}