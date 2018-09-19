/*
 *  Copyright © 2018 mrAppleXZ
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this file,
 *  You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package ru.pearx.multigradle.plugin.simple

import org.gradle.api.Project
import ru.pearx.multigradle.plugin.common.CommonProjectPlugin
import ru.pearx.multigradle.util.Platform
import ru.pearx.multigradle.util.MultiGradleExtension


/*
 * Created by mrAppleXZ on 06.09.18.
 */
class SimpleProjectPlugin : CommonProjectPlugin()
{
    override fun apply(target: Project, extension: MultiGradleExtension)
    {
        with(target) {
            for (platform in subprojects)
            {
                Platform.valueOfCodeName(platform.name).applyProject(platform, this, this, extension)
            }
        }
    }
}