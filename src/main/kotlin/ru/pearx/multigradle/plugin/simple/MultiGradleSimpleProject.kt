/*
 *  Copyright © 2018 mrAppleXZ
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this file,
 *  You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package ru.pearx.multigradle.plugin.simple

import org.gradle.api.Plugin
import org.gradle.api.Project
import ru.pearx.multigradle.util.MULTIGRADLE_EXTENSION_NAME
import ru.pearx.multigradle.util.MultiGradleExtension
import ru.pearx.multigradle.util.platform.Platform


/*
 * Created by mrAppleXZ on 06.09.18.
 */
class MultiGradleSimpleProject : Plugin<Project>
{
    override fun apply(target: Project)
    {
        with(target) {
            for (platform in subprojects)
            {
                with(platform) {
                    extensions.add(MULTIGRADLE_EXTENSION_NAME, MultiGradleExtension().load(this))
                    Platform.valueOfCodeName(name).apply(this, target, target)
                }
            }
        }
    }
}