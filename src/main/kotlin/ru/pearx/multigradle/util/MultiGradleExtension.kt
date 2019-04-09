/*
 * Copyright © 2019 mrAppleXZ
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package ru.pearx.multigradle.util

import com.moowork.gradle.node.NodeExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginConvention
import org.gradle.kotlin.dsl.*
import org.gradle.testing.jacoco.plugins.JacocoPluginExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import kotlin.reflect.KMutableProperty
import kotlin.reflect.full.createType
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.isAccessible


/*
 * Created by mrAppleXZ on 01.09.18.
 */
open class MultiGradleExtension(private val project: Project) {
    //region COMMON
    var projectVersion: String by project.alias({ version.toString() }, Project::setVersion)
    var createPrefixedTestResults = true
    //endregion

    //region JVM
    lateinit var junitJupiterVersion: String

    var jacocoVersion: String by project.the<JacocoPluginExtension>().alias(JacocoPluginExtension::getToolVersion, JacocoPluginExtension::setToolVersion)

    private lateinit var _javaVersion: String
    var javaVersion: String
        get() = _javaVersion
        set(value) {
            _javaVersion = value
            project.the<KotlinMultiplatformExtension>().jvm {
                compilations.configureEach { kotlinOptions.jvmTarget = "1.$value" }
            }
            project.the<JavaPluginConvention>().sourceCompatibility = JavaVersion.toVersion(value)
        }
    //endregion

    //region JS
    var nodeJsVersion: String by project.the<NodeExtension>().alias(NodeExtension::getVersion, NodeExtension::setVersion)
    var npmVersion: String by project.the<NodeExtension>().alias(NodeExtension::getNpmVersion, NodeExtension::setNpmVersion)
    lateinit var mochaVersion: String
    lateinit var mochaJunitReporterVersion: String
    var npmPackages = mutableMapOf<String, String>()

    inline fun npmPackages(init: MutableMap<String, String>.() -> Unit) {
        init(npmPackages)
    }
    //endregion

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