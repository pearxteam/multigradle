/*
 *  Copyright © 2018 mrAppleXZ
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this file,
 *  You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package ru.pearx.multigradle.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project
import ru.pearx.multigradle.util.MULTIGRADLE_EXTENSION_NAME
import ru.pearx.multigradle.util.MultiGradleExtension

abstract class MultiGradleProject : Plugin<Project>
{
    override fun apply(target: Project)
    {
        with(target) {
            extensions.add(MULTIGRADLE_EXTENSION_NAME, MultiGradleExtension().load(this))
        }
    }
}