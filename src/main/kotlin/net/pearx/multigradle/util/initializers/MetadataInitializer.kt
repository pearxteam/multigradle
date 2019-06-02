/*
 * Copyright © 2019 mrAppleXZ
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at https://mozilla.org/MPL/2.0/.
 */

@file:JvmMultifileClass
@file:JvmName("InitializersKt")

package net.pearx.multigradle.util.initializers

import net.pearx.multigradle.util.kotlinMpp
import org.gradle.api.Project
import org.gradle.kotlin.dsl.*

internal fun Project.metadataInitializer() {
    kotlinMpp {
        sourceSets {
            named("commonMain") {
                dependencies {
                    implementation(kotlin("stdlib-common"))
                }
            }

            named("commonTest") {
                dependencies {
                    implementation(kotlin("test-common"))
                    implementation(kotlin("test-annotations-common"))
                }
            }
        }
    }
}