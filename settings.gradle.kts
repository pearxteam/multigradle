/*
 * Copyright © 2019 mrAppleXZ
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at https://mozilla.org/MPL/2.0/.
 */

rootProject.name = "multigradle"

val kotlinVersion: String by settings
val pluginPublishVersion: String by settings

pluginManagement {
    resolutionStrategy {
        eachPlugin {
            if(requested.id.id == "com.gradle.plugin-publish")
                useVersion(pluginPublishVersion)
        }
    }
}