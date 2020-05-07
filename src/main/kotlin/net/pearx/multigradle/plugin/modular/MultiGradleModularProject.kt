/*
 * Copyright © 2019-2020, PearX Team
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package net.pearx.multigradle.plugin.modular

import net.pearx.multigradle.plugin.initializeMultiGradle
import org.gradle.api.Plugin
import org.gradle.api.Project

/*
 * Created by mrAppleXZ on 01.09.18.
 */
class MultiGradleModularProject : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            for (module in project("modules").subprojects) {
                module.initializeMultiGradle()
            }
        }
    }
}