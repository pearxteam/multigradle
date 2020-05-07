package net.pearx.multigradle.util.platform

import com.android.build.gradle.LibraryExtension
import com.android.build.gradle.LibraryPlugin
import com.android.build.gradle.tasks.GenerateBuildConfig
import net.pearx.multigradle.util.alias
import net.pearx.multigradle.util.kotlinMpp
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.kotlin.dsl.*

class AndroidPlatformConfig(project: Project) : PlatformConfig(project) {
    var compileSdkVersion: String by project.the<LibraryExtension>().alias(LibraryExtension::getCompileSdkVersion, LibraryExtension::setCompileSdkVersion)
    var buildToolsVersion: String by project.the<LibraryExtension>().alias(LibraryExtension::getBuildToolsVersion, LibraryExtension::setBuildToolsVersion)
    lateinit var junitVersion: String
}

val AndroidPlatform = platform("android", listOf("testReleaseUnitTest", "testDebugUnitTest"), { AndroidPlatformConfig(it) }) { ext ->
    apply<LibraryPlugin>()

    val manifestPath = file("$buildDir/AndroidManifest.xml")

    repositories {
        google()
    }

    kotlinMpp {
        sourceSets {
            afterEvaluate {
                named("androidMain") {
                    dependencies {
                        implementation(kotlin("stdlib"))
                    }
                }
                named("androidTest") {
                    dependencies {
                        implementation(kotlin("test-annotations-common"))
                        implementation(kotlin("test-junit"))
                        implementation("junit:junit:${ext().junitVersion}")
                    }
                }
            }
        }
        android {
            publishLibraryVariants("release", "debug")
        }
    }

    configure<LibraryExtension> {
        defaultConfig {

        }
        buildTypes.configureEach {
            sourceSets.configureEach {
                manifest.srcFile(manifestPath)
            }
        }
    }


    tasks {
        val generateAndroidManifest by registering(Task::class) {
            with(inputs) {
                property("group", project.group)
                property("name", project.name)
            }
            outputs.file(manifestPath)
            doLast {
                val pkg = mutableListOf<String>().also {
                    it += project.group.toString()
                    it += project.name.split('-').drop(1)
                }.joinToString(".") // todo a bit hacky :(
                manifestPath.writeText("""
                <?xml version="1.0" encoding="utf-8"?>
                <manifest xmlns:android="http://schemas.android.com/apk/res/android"
                          package="$pkg"/>
                """.trimIndent())
            }
        }

        withType<GenerateBuildConfig> {
            dependsOn(generateAndroidManifest)
        }
    }
}