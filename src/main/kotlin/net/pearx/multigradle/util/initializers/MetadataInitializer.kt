/*
 * Copyright © 2019 mrAppleXZ
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at https://mozilla.org/MPL/2.0/.
 */

@file:JvmMultifileClass
@file:JvmName("InitializersKt")

package net.pearx.multigradle.util.initializers

import org.gradle.api.Project
import org.gradle.kotlin.dsl.*
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

internal fun Project.metadataInitializer() {
    configure<KotlinMultiplatformExtension> {
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