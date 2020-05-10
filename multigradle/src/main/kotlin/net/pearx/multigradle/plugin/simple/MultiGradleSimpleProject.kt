/*
 * Copyright © 2019-2020, PearX Team
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package net.pearx.multigradle.plugin.simple

import net.pearx.multigradle.plugin.initializeMultiGradle
import org.gradle.api.Plugin
import org.gradle.api.Project

class MultiGradleSimpleProject : Plugin<Project> {
    override fun apply(target: Project) {
        target.initializeMultiGradle()
    }
}