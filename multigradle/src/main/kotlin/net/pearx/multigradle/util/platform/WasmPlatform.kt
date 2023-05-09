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

class WasmPlatformConfig(project: Project) : PlatformConfig(project)

val WasmPlatform = platform("wasm", listOf("wasmTest"), { WasmPlatformConfig(it) }) {
    kotlinMpp {
        wasm {
            binaries.library()
            d8 {
                testTask {
                    useMocha()
                }
            }

            compilations["main"] {
                dependencies {
                    implementation(kotlin("stdlib"))
                }
            }

            compilations["test"] {
                dependencies {
                    implementation(kotlin("test"))
                }
            }
        }
    }
}
