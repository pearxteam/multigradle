/*
 *  Copyright © 2018 mrAppleXZ
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this file,
 *  You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package ru.pearx.multigradle.plugin.simple

import org.gradle.api.Project
import org.gradle.kotlin.dsl.the
import ru.pearx.multigradle.plugin.MultiGradleProject
import ru.pearx.multigradle.util.Platform


/*
 * Created by mrAppleXZ on 06.09.18.
 */
class MultiGradleSimpleProject : MultiGradleProject()
{
    override fun apply(target: Project)
    {
        super.apply(target)
        with(target) {
            for (platform in subprojects)
            {
                platform.beforeEvaluate {
                    val root = parent!!
                    Platform.valueOfCodeName(name).apply(this, root, root, root.the())
                }
            }
        }
    }
}