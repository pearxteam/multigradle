/*
 * Copyright © 2019-2020, PearX Team
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package net.pearx.multigradle.util

import org.gradle.api.Project
import org.gradle.kotlin.dsl.*
import org.jetbrains.dokka.gradle.DokkaTask

internal fun Project.configureDokka(dokka: DokkaTask, outputFormat: String, outputName: String, vararg platformList: String) {
    with(dokka) {
        this.outputFormat = outputFormat
        outputDirectory = "$buildDir/dokka/$outputName"
        multiplatform {
            for(platform in platformList) {
                create(platform.toLowerCase()) {
                    targets = listOf("")
                }
            }
        }
    }
}