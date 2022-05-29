/*
 * Copyright © 2019-2020, PearX Team
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package net.pearx.multigradle.plugin

import net.pearx.multigradle.util.MultiGradleExtension
import net.pearx.multigradle.util.asBoolean
import net.pearx.multigradle.util.kotlinMpp
import net.pearx.multigradle.util.platform.PLATFORMS
import net.pearx.multigradle.util.platform.Platform
import net.pearx.multigradle.util.platform.PlatformConfig
import org.gradle.api.Project
import org.gradle.api.plugins.BasePlugin
import org.gradle.api.publish.maven.tasks.AbstractPublishToMaven
import org.gradle.api.publish.plugins.PublishingPlugin
import org.gradle.api.tasks.Sync
import org.gradle.api.tasks.bundling.Jar
import org.gradle.kotlin.dsl.*
import org.jetbrains.dokka.gradle.DokkaPlugin
import org.jetbrains.gradle.ext.IdeaExtPlugin
import org.jetbrains.kotlin.gradle.plugin.KotlinMultiplatformPluginWrapper
import org.jetbrains.kotlin.konan.target.HostManager
import org.jetbrains.kotlin.konan.target.presetName

const val MULTIGRADLE_EXTENSION_NAME = "multigradle"

private fun createPlatformsRegex(platforms: Set<String>) = Regex(buildString {
    append('^')
    append("(?:")
    platforms.forEachIndexed { index, platform ->
        if (index != 0)
            append('|')
        append("""\Q""")
        for (char in platform) {
            if (char == '*')
                append("""\E.+\Q""")
            else
                append(char)
        }
        append("""\E""")
    }
    append(')')
    append('$')
})

internal fun Project.enabledPlatforms(): Collection<Platform<out PlatformConfig>> {
    val platformNames = properties["enabledPlatforms"]?.toString()?.split(',')?.toSet() ?: return PLATFORMS.values
    val regex = createPlatformsRegex(platformNames)
    return PLATFORMS.values.filter { regex.matches(it.name) }
}

internal fun Project.initializeMultiGradle() {
    preInit()
    val ext = MultiGradleExtension(this)
    extensions.add(MULTIGRADLE_EXTENSION_NAME, ext)
    val enabledPlatforms = enabledPlatforms()
    logger.info("Enabled MultiGradle platforms: $enabledPlatforms")
    for (platform in enabledPlatforms) {
        platform.initialize(this)
        ext.initPlatform(platform)
    }
    postInit()
    ext.setupFromProperties()
}

private fun Project.preInit() {
    apply<KotlinMultiplatformPluginWrapper>()
    apply<DokkaPlugin>()
    apply<BasePlugin>()
    apply<PublishingPlugin>()
    apply<IdeaExtPlugin>()

    extra.set("kotlin.tests.individualTaskReports", "false") // hack until https://youtrack.jetbrains.com/issue/KT-35202 is fixed

    repositories {
        mavenCentral()
    }

    tasks {
        register<Jar>("emptyJavadoc") {
            archiveClassifier.set("javadoc")
        }
    }

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

private val TARGETS_BY_PRESET = HostManager().targets.mapKeys { it.value.presetName }
private fun Project.postInit() {
    for (target in kotlinMpp.targets {
        target.mavenPublication {
            artifact(tasks["emptyJavadoc"])
        }
    }

    if (project.properties["multigradle.publishHostExclusivesOnly"].asBoolean) {
        kotlinMpp.targets.configureEach {
            mavenPublication {
                tasks.withType<AbstractPublishToMaven> {
                    onlyIf {
                        val mgr = HostManager()
                        val target = TARGETS_BY_PRESET[publication.name]
                        target != null && mgr.enabledByHost.filterValues { target in it }.size == 1 && mgr.isEnabled(target)
                    }
                }
            }
        }
    }

    tasks {
        afterEvaluate {
            for (testTaskName in enabledPlatforms().flatMap { it.testTasks }) {
                val testTask = named(testTaskName) {
                    finalizedBy("${name}Prefix")
                }

                create<Sync>("${testTask.name}Prefix") {
                    onlyIf { project.the<MultiGradleExtension>().createPrefixedTestResults }
                    from("$buildDir/test-results/$testTaskName")
                    into("$buildDir/test-results-prefixed/$testTaskName")
                    include("**/*.xml")
                    // todo: make filtering not just string replacing
                    filter { line ->
                        line.replace(Regex("testsuite name=\"(.+?)\""), "testsuite name=\"$testTaskName $1\"")
                        line.replace(Regex("classname=\"(.+?)\""), "classname=\"$testTaskName $1\"")
                    }
                }
            }
        }
    }
}