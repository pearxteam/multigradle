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
open class MultiGradleExtension
{
    lateinit var projectVersion: String
    var kotlinExperimentalFeatures = mutableListOf<String>()
    var createPrefixedTestResults = true

    fun load(project: Project): MultiGradleExtension
    {
        val stringType = String::class.createType()
        for (property in this::class.memberProperties)
        {
            for ((key, value) in project.properties)
            {
                if (property.name == key && property.isLateinit && property is KMutableProperty<*> && property.returnType == stringType)
                {
                    property.isAccessible = true
                    @Suppress("UNCHECKED_CAST")
                    (property as KMutableProperty<String>).setter.call(this, value)
                }
            }
        }
        return this
    }
}