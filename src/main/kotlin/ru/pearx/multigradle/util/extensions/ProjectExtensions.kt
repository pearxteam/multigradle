/*
 * Copyright © 2018 mrAppleXZ
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package ru.pearx.multigradle.util.extensions

import org.gradle.api.Project
import ru.pearx.multigradle.util.PLATFORMS
import ru.pearx.multigradle.util.Platform

fun Project.subplatforms(init: Project.(platform: Platform<*>) -> Unit)
{
    subprojects {
        for (platform in PLATFORMS)
            if (platform.name == name)
                init(platform)
    }
}

fun Project.subplatforms(platform: Platform<*>, init: Project.() -> Unit)
{
    subprojects {
        if (platform.name == name)
            init()
    }
}