@file:JvmMultifileClass
@file:JvmName("InitializersKt")

package ru.pearx.multigradle.util.initializers

import com.moowork.gradle.node.NodeExtension
import com.moowork.gradle.node.NodePlugin
import com.moowork.gradle.node.npm.NpmTask
import com.moowork.gradle.node.task.NodeTask
import org.gradle.api.Project
import org.gradle.api.tasks.Sync
import org.gradle.api.tasks.testing.Test
import org.gradle.kotlin.dsl.*
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import ru.pearx.multigradle.util.MultiGradleExtension
import ru.pearx.multigradle.util.invoke

internal fun Project.jsInitializer(extension: MultiGradleExtension) {
    apply<NodePlugin>()

    afterEvaluate {
        println("AE")
        configure<NodeExtension> {
            version = extension.nodeJsVersion
            npmVersion = extension.npmVersion
            download = true

            val cacheDir = file("$rootDir/.gradle/node")
            workDir = file("$cacheDir/nodejs")
            npmWorkDir = file("$cacheDir/npm")
            yarnWorkDir = file("$cacheDir/yarn")
            nodeModulesDir = buildDir
        }

        configure<KotlinMultiplatformExtension> {
            js {
                compilations.configureEach {
                    kotlinOptions {
                        moduleKind = "umd"
                        sourceMap = true
                    }
                }
                compilations["main"] {
                    dependencies {
                        implementation(kotlin("stdlib-js"))
                    }
                }

                compilations["test"] {
                    dependencies {
                        implementation(kotlin("test-js"))
                    }
                }
            }
        }

        tasks {
            val jsMainCompilation = the<KotlinMultiplatformExtension>().js().compilations["main"]
            val jsTestCompilation = the<KotlinMultiplatformExtension>().js().compilations["test"]

            val jsTestSyncNodeModules by registering(Sync::class) {
                dependsOn(jsTestCompilation.compileKotlinTask, jsMainCompilation.compileKotlinTask)
                from(jsTestCompilation.runtimeDependencyFiles.map { if (it.extension == "jar") zipTree(it) else it })
                jsTestCompilation.runtimeDependencyFiles.forEach { println(it) }
                include { it.path.endsWith(".js", true) }
                into("$buildDir/kotlinjs")
            }

            val jsTestInstallPackages by registering(NpmTask::class) {
                val lst = mutableListOf("install", "mocha@${extension.mochaVersion}", "mocha-jenkins-reporter@${extension.mochaJunitReporterVersion}")
                for ((name, version) in extension.npmPackages)
                    lst.add("$name@$version")
                setArgs(lst)
            }

            val jsTestRunMocha by registering(NodeTask::class) {
                dependsOn(jsTestInstallPackages, jsTestSyncNodeModules, jsTestCompilation.compileAllTaskName)
                setScript(file("$buildDir/node_modules/mocha/bin/mocha"))
                setEnvironment(mapOf("NODE_PATH" to "$buildDir/kotlinjs", "JUNIT_REPORT_PATH" to "$buildDir/test-results/jsTest/mocha.xml"))
                setArgs(listOf(jsTestCompilation.output.classesDirs.first().toString(), "--reporter", "mocha-jenkins-reporter"))
            }

            named<Test>("jsTest") {
                dependsOn(jsTestRunMocha)
            }
        }
    }
}