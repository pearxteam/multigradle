package net.pearx.multigradle.util.platform

import com.moowork.gradle.node.NodeExtension
import com.moowork.gradle.node.NodePlugin
import com.moowork.gradle.node.npm.NpmTask
import com.moowork.gradle.node.task.NodeTask
import net.pearx.multigradle.util.alias
import net.pearx.multigradle.util.invoke
import net.pearx.multigradle.util.kotlinMpp
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.tasks.Sync
import org.gradle.api.tasks.TaskProvider
import org.gradle.kotlin.dsl.*
import java.nio.file.Files

class JsPlatformConfig(project: Project) : PlatformConfig(project) {
    var nodeJsVersion: String by project.the<NodeExtension>().alias(NodeExtension::getVersion, NodeExtension::setVersion)
    var npmVersion: String by project.the<NodeExtension>().alias(NodeExtension::getNpmVersion, NodeExtension::setNpmVersion)
    lateinit var mochaVersion: String
    lateinit var mochaJunitReporterVersion: String
    var npmPackages = mutableMapOf<String, String>()

    inline fun npmPackages(init: MutableMap<String, String>.() -> Unit) {
        init(npmPackages)
    }
}

val JsPlatform = platform("js", listOf("jsTest"), { JsPlatformConfig(it) }) { ext ->
    apply<NodePlugin>()

    configure<NodeExtension> {
        download = true

        val cacheDir = file("$rootDir/.gradle/node")
        workDir = file("$cacheDir/nodejs")
        npmWorkDir = file("$cacheDir/npm")
        yarnWorkDir = file("$cacheDir/yarn")
        // todo move to $buildDir/node_modules when https://github.com/srs/gradle-node-plugin/issues/300 will be resolved
        nodeModulesDir = file("$projectDir/node_modules")
    }

    kotlinMpp {
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
        val jsMainCompilation = kotlinMpp.js().compilations["main"]
        val jsTestCompilation = kotlinMpp.js().compilations["test"]

        val jsTestSyncNodeModules by registering(Sync::class) {
            dependsOn(jsTestCompilation.compileKotlinTask, jsMainCompilation.compileKotlinTask)
            from(jsTestCompilation.runtimeDependencyFiles.map { if (it.extension == "jar") zipTree(it) else it })
            include { it.path.endsWith(".js", true) }
            into("$buildDir/kotlinjs")
        }

        val jsTestInstallPackages by registering(NpmTask::class) {
            doFirst {
                setArgs(mutableListOf(
                    "install",
                    "mocha@${ext().mochaVersion}",
                    "mocha-jenkins-reporter@${ext().mochaJunitReporterVersion}")
                    .apply {
                        for ((name, version) in ext().npmPackages)
                            add("$name@$version")
                    })
            }
        }

        val jsTestRunMocha by registering(NodeTask::class) {
            dependsOn(jsTestInstallPackages, jsTestSyncNodeModules, jsTestCompilation.compileAllTaskName)
            setScript(file("${project.the<NodeExtension>().nodeModulesDir}/mocha/bin/mocha"))
            setEnvironment(mapOf("NODE_PATH" to "$buildDir/kotlinjs", "JUNIT_REPORT_PATH" to "$buildDir/test-results/jsTest/mocha.xml"))
            setArgs(listOf(jsTestCompilation.output.classesDirs.first().toString(), "--reporter", "mocha-jenkins-reporter"))
        }

        named("jsTest") {
            dependsOn(jsTestRunMocha)
        }


        listOf<TaskProvider<out Task>>(jsTestRunMocha, jsTestSyncNodeModules, jsTestInstallPackages, named("npmSetup"), named("nodeSetup")).forEach { task ->
            task.configure {
                onlyIf {
                    try {
                        Files.newDirectoryStream(jsTestCompilation.output.classesDirs.first().toPath()).use { f -> f.iterator().hasNext() }
                    }
                    catch (e: Exception) {
                        false
                    }
                }
            }
        }
    }
}