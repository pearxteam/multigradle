/*
 * Copyright © 2019-2020, PearX Team
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package net.pearx.multigradle.util.platform

import net.pearx.multigradle.util.invoke
import net.pearx.multigradle.util.kotlinMpp
import org.gradle.api.Project
import org.gradle.kotlin.dsl.*

class JsPlatformConfig(project: Project) : PlatformConfig(project)

val JsPlatform = platform("js", listOf("jsTest"), { JsPlatformConfig(it) }) {
    kotlinMpp {
        js {
            nodejs {
                testTask {
                    useMocha()
                }
            }
            compilations.configureEach {
                kotlinOptions {
                    moduleKind = "umd"
                    sourceMap = true
                }
            }
            compilations["main"] {
                dependencies {
                    implementation(kotlin("stdlib-js"))
                }
            }

            compilations["test"] {
                dependencies {
                    implementation(kotlin("test-js"))
                }
            }
        }
    }
}
