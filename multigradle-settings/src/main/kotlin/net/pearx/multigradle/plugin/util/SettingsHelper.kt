/*
 * Copyright © 2019-2020, PearX Team
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package net.pearx.multigradle.plugin.util

import org.gradle.api.initialization.Settings

internal fun Settings.setupRepos() {
    pluginManagement {
        repositories {
            gradlePluginPortal()
            google()
        }
    }
}