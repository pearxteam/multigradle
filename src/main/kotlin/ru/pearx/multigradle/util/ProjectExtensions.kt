/*
 * Copyright © 2018 mrAppleXZ
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package ru.pearx.multigradle.util

import org.gradle.api.Project
import org.gradle.api.artifacts.dsl.RepositoryHandler
import java.net.URI

fun Project.propertyString(name: String) = properties[name].toString()

inline fun Project.subplatforms(crossinline init: Project.(platform: Platform<*>) -> Unit)
{
    subprojects {
        for (platform in PLATFORMS)
            if (platform.name == name)
                init(platform)
    }
}

inline fun Project.subplatforms(platform: Platform<*>, crossinline init: Project.() -> Unit)
{
    subprojects {
        ifPlatform(platform, init)
    }
}

inline fun Project.ifPlatform(platform: Platform<*>, init: Project.() -> Unit)
{
    if(name == platform.name)
        init()
}

fun RepositoryHandler.kotlinDev()
{
    maven { url = URI("https://dl.bintray.com/kotlin/kotlin-dev/") }
}

fun RepositoryHandler.kotlinEap()
{
    maven { url = URI("https://dl.bintray.com/kotlin/kotlin-eap/") }
}