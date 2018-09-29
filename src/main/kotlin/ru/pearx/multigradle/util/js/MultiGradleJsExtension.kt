/*
 * Copyright © 2018 mrAppleXZ
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package ru.pearx.multigradle.util.js

import ru.pearx.multigradle.util.MultiGradleExtension

class MultiGradleJsExtension : MultiGradleExtension()
{
    lateinit var nodejsVersion: String
    lateinit var npmVersion: String
    lateinit var mochaVersion: String
    var nodeModules = mutableListOf<NodeModule>()
}