/*
 * Copyright © 2019 mrAppleXZ
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package ru.pearx.multigradle.util

import org.gradle.api.Project
import kotlin.reflect.KMutableProperty
import kotlin.reflect.full.createType
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.isAccessible


/*
 * Created by mrAppleXZ on 01.09.18.
 */
open class MultiGradleExtension() {
    // COMMON
    lateinit var projectVersion: String
    var createPrefixedTestResults = true

    // JVM
    lateinit var junitJupiterVersion: String
    lateinit var jacocoVersion: String

    lateinit var javaVersion: String

    val javaVersionFull
        get() = "1.$javaVersion"

    // JS
    lateinit var nodeJsVersion: String
    lateinit var npmVersion: String
    lateinit var mochaVersion: String
    lateinit var mochaJunitReporterVersion: String
    var npmPackages = mutableMapOf<String, String>()

    inline fun npmPackages(init: MutableMap<String, String>.() -> Unit) {
        init(npmPackages)
    }

    //

    fun load(project: Project): MultiGradleExtension {
        val stringType = String::class.createType()
        for (property in this::class.memberProperties) {
            for ((key, value) in project.properties) {
                if (property.name == key && property is KMutableProperty<*> && property.returnType == stringType) {
                    property.isAccessible = true
                    @Suppress("UNCHECKED_CAST")
                    (property as KMutableProperty<String>).setter.call(this, value)
                }
            }
        }
        return this
    }
}