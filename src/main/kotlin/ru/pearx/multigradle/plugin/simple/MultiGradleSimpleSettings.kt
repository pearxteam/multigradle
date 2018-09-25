/*
 *  Copyright © 2018 mrAppleXZ
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this file,
 *  You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package ru.pearx.multigradle.plugin.simple

import org.gradle.api.Plugin
import org.gradle.api.initialization.Settings
import ru.pearx.multigradle.util.Platform


/*
 * Created by mrAppleXZ on 06.09.18.
 */
class MultiGradleSimpleSettings : Plugin<Settings>
{
    override fun apply(target: Settings)
    {
        with(target) {
            for (platform in Platform.values())
                include(platform.codeName)
        }
    }
}