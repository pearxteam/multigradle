package net.pearx.multigradle.util.platform

import net.pearx.multigradle.util.*
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginConvention
import org.gradle.api.tasks.bundling.Jar
import org.gradle.api.tasks.testing.Test
import org.gradle.kotlin.dsl.*
import org.gradle.testing.jacoco.plugins.JacocoPlugin
import org.gradle.testing.jacoco.plugins.JacocoPluginExtension
import org.jetbrains.dokka.gradle.DokkaTask

class JvmPlatformConfig(project: Project) : PlatformConfig(project) {
    lateinit var junitJupiterVersion: String

    var jacocoVersion: String by project.the<JacocoPluginExtension>().alias(JacocoPluginExtension::getToolVersion, JacocoPluginExtension::setToolVersion)

    private lateinit var _javaVersion: String
    var javaVersion: String
        get() = _javaVersion
        set(value) {
            _javaVersion = value
            project.kotlinMpp.jvm {
                compilations.configureEach { kotlinOptions.jvmTarget = "1.$value" }
            }
            project.the<JavaPluginConvention>().sourceCompatibility = JavaVersion.toVersion(value)
        }
}

val JvmPlatform = platform("jvm", listOf("jvmTest"), { JvmPlatformConfig(it) }) { ext ->
    apply<JacocoPlugin>()

    val dokkaJavadoc by tasks.registering(DokkaTask::class) {
        configureDokka(this, "javadoc", "javadoc", "JVM")
    }
    val javadocJar by tasks.registering(Jar::class) {
        dependsOn(dokkaJavadoc)
        archiveClassifier.set("javadoc")
        archiveAppendix.set("jvm")
        from(dokkaJavadoc.get().outputDirectory)
    }

    kotlinMpp {
        jvm {
            afterEvaluate {
                compilations["main"] {
                    mavenPublication {
                        artifact(javadocJar.get())
                    }
                    dependencies {
                        implementation(kotlin("stdlib-jdk${ext().javaVersion}"))
                    }
                }

                compilations["test"] {
                    dependencies {
                        implementation(kotlin("test-annotations-common"))
                        implementation(kotlin("test-junit5"))
                        implementation("org.junit.jupiter:junit-jupiter-api:${ext().junitJupiterVersion}")
                        runtimeOnly("org.junit.jupiter:junit-jupiter-engine:${ext().junitJupiterVersion}")
                    }
                }
            }
        }
    }

    tasks {
        named<Test>("jvmTest") {
            reports.junitXml.isEnabled = true
            useJUnitPlatform()
        }
    }
}