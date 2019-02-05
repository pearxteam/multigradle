/*
 * Copyright © 2019 mrAppleXZ
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package ru.pearx.multigradle.plugin.simple

import org.gradle.api.Plugin
import org.gradle.api.initialization.Settings
import ru.pearx.multigradle.util.PLATFORMS
import java.io.File


/*
 * Created by mrAppleXZ on 06.09.18.
 */
class MultiGradleSimpleSettings : Plugin<Settings>
{
    override fun apply(target: Settings)
    {
        with(target) {
            for (platform in PLATFORMS)
            {
                val proj = "${rootProject.name}-${platform.name}"
                include(proj)
                val projDir = File(rootDir, platform.name)
                project(proj).projectDir = projDir
                projDir.mkdirs()
            }
        }
    }
}