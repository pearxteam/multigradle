@file:JvmMultifileClass
@file:JvmName("InitializersKt")

package ru.pearx.multigradle.util.initializers

import org.gradle.api.Project
import org.gradle.api.plugins.BasePlugin
import org.gradle.api.tasks.Sync
import org.gradle.api.tasks.testing.Test
import org.gradle.kotlin.dsl.*
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinMultiplatformPluginWrapper
import ru.pearx.multigradle.util.MultiGradleExtension

fun Project.parentInitializer() {
    apply<KotlinMultiplatformPluginWrapper>()
    apply<BasePlugin>()

    repositories {
        jcenter()
    }

    tasks {
        for (target in the<KotlinMultiplatformExtension>().targets.filterNot { it.name == "metadata" }) {
            val testTask = named<Test>("${target.name}Test") {
                finalizedBy("${name}Prefix")
            }

            create<Sync>("${testTask.name}Prefix") {
                onlyIf { project.the<MultiGradleExtension>().createPrefixedTestResults }
                val sourceSetName = target.compilations["test"].defaultSourceSet
                from("$buildDir/test-results/$sourceSetName")
                into("$buildDir/test-results-prefixed/$sourceSetName")
                include("**/*.xml")
                // todo: make filtering not just string replacing
                filter { line ->
                    line.replace(Regex("testsuite name=\"(.+?)\""), "testsuite name=\"${target.name} $1\"")
                    line.replace(Regex("classname=\"(.+?)\""), "classname=\"${target.name} $1\"")
                }
            }
        }
    }
}