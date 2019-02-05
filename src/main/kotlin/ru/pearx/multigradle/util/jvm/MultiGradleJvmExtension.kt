/*
 * Copyright © 2019 mrAppleXZ
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package ru.pearx.multigradle.util.jvm

import ru.pearx.multigradle.util.MultiGradleExtension

class MultiGradleJvmExtension : MultiGradleExtension()
{
    lateinit var junitJupiterVersion: String
    lateinit var jacocoVersion: String
    lateinit var javaVersion: String

    val javaVersionFull
        get() = "1.$javaVersion"
}