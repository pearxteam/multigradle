/*
 * Copyright © 2019-2020, PearX Team
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package net.pearx.multigradle.util

import net.pearx.multigradle.util.platform.Platform
import net.pearx.multigradle.util.platform.PlatformConfig
import org.gradle.api.Project
import org.gradle.kotlin.dsl.*
import kotlin.reflect.KMutableProperty
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.isAccessible
import kotlin.reflect.typeOf


/*
 * Created by mrAppleXZ on 01.09.18.
 */
open class MultiGradleExtension(private val project: Project) {
    var projectVersion: String by project.alias<Project, String>({ version.toString() }, Project::setVersion)
    var createPrefixedTestResults = true

    private val platformConfigs = hashMapOf<Platform<*>, PlatformConfig>()

    inline fun <reified T : PlatformConfig> platform(platform: Platform<T>, block: T.() -> Unit) {
        platform(platform).apply(block)
    }

    fun <T : PlatformConfig> platform(platform: Platform<T>): T {
        @Suppress("UNCHECKED_CAST")
        return platformConfigs[platform] as T
    }

    internal fun <T : PlatformConfig> initPlatform(platform: Platform<T>) {
        platformConfigs[platform] = platform.createConfig(project)
    }

    private fun Any.setupFromProperties(prefix: String) {
        val props = this::class.memberProperties.filter { it is KMutableProperty<*> && it.returnType == typeOf<String>() && (project.hasProperty("$prefix${it.name}") || project.hasProperty(it.name)) }
        for(prop in props) {
            prop.isAccessible = true
            val value = if(project.hasProperty("$prefix${prop.name}")) project.properties["$prefix${prop.name}"] else project.properties[prop.name]
            (prop as KMutableProperty<*>).setter.call(this, value)
        }
    }

    internal fun setupFromProperties() {
        setupFromProperties("multigradle/")
        for((platform, config) in platformConfigs) {
            config.setupFromProperties("${platform.name}/")
        }
    }
}