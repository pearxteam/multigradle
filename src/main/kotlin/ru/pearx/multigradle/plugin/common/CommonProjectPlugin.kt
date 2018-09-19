/*
 *  Copyright © 2018 mrAppleXZ
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this file,
 *  You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package ru.pearx.multigradle.plugin.common

import org.gradle.api.Plugin
import org.gradle.api.Project
import ru.pearx.multigradle.util.MULTIGRADLE_EXTENSION_NAME
import ru.pearx.multigradle.util.MultiGradleExtension

abstract class CommonProjectPlugin : Plugin<Project>
{
    override fun apply(target: Project)
    {
        with(target) {
            val extension = MultiGradleExtension().load(this)
            extensions.add(MULTIGRADLE_EXTENSION_NAME, extension)
            apply(this, extension)
        }
    }

    abstract fun apply(target: Project, extension: MultiGradleExtension)
}