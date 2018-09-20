/*
 *  Copyright © 2018 mrAppleXZ
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this file,
 *  You can obtain one at https://mozilla.org/MPL/2.0/.
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
class MultiGradleExtension
{
    lateinit var projectVersion: String
    lateinit var nodejsVersion: String
    lateinit var npmVersion: String
    lateinit var mochaVersion: String
    lateinit var junitJupiterVersion: String
    lateinit var jacocoVersion: String
    lateinit var javaVersion: String

    val javaVersionFull
        get() = "1.$javaVersion"

    fun load(project: Project): MultiGradleExtension
    {
        val classProps = this::class.memberProperties
        val stringType = String::class.createType()
        for ((key, value) in project.properties)
        {
            for (property in classProps)
            {
                if (property.name == key && property is KMutableProperty<*> && property.returnType == stringType)
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