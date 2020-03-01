/*
 * Copyright © 2019 mrAppleXZ
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package net.pearx.multigradle.plugin.modular

import org.gradle.api.Plugin
import org.gradle.api.initialization.Settings
import java.nio.file.Files

/*
 * Created by mrAppleXZ on 31.08.18.
 */
class MultiGradleModularSettings : Plugin<Settings> {
    override fun apply(target: Settings) {
        with(target) {
            val root = rootDir.toPath()
            for (modulePath in Files.newDirectoryStream(root.resolve("modules"))) {
                if (Files.isDirectory(modulePath)) {
                    val proj = buildString {
                        append(":modules:")
                        append(rootProject.name)
                        if(modulePath.fileName.toString() != "main") {
                            append('-')
                            append(modulePath.fileName)
                        }
                    }
                    include(proj)
                    val projDir = modulePath.toFile()
                    project(proj).projectDir = projDir
                    projDir.mkdirs()
                }
            }
        }
    }
}