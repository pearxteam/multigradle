/*
 *  Copyright © 2018 mrAppleXZ
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this file,
 *  You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package ru.pearx.multigradle.plugin.modular

import org.gradle.api.Plugin
import org.gradle.api.initialization.Settings
import ru.pearx.multigradle.util.Platform
import java.io.File
import java.nio.file.Files

/*
 * Created by mrAppleXZ on 31.08.18.
 */
class ModularSettingsPlugin : Plugin<Settings>
{
    override fun apply(target: Settings)
    {
        with(target) {
            val root = rootDir.toPath().normalize()
            for(subPath in Files.newDirectoryStream(root.resolve("modules")))
            {
                if(Files.isDirectory(subPath))
                {
                    for(platform in Platform.values())
                    {
                        include(root.relativize(subPath.resolve(platform.codeName)).toString().replace(File.separatorChar, ':'))
                    }
                }
            }
        }
    }
}