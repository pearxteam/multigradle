/*
 * Copyright © 2019 mrAppleXZ
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at https://mozilla.org/MPL/2.0/.
 */

plugins {
    `java-gradle-plugin`
    `kotlin-dsl`
    `maven-publish`
    id("com.gradle.plugin-publish")
}

group = "ru.pearx.multigradle"
version = properties["multigradleVersion"]!!

repositories {
    jcenter()
    gradlePluginPortal()
}

dependencies {
    "compile"("org.jetbrains.kotlin:kotlin-gradle-plugin:${properties["kotlinVersion"]}")
    "compile"("com.moowork.node:com.moowork.node.gradle.plugin:${properties["nodeVersion"]}")
}

gradlePlugin {
    plugins {
        fun createMultiGradlePlugin(type: String, applicableTo: String)
        {
            create("multigradle-$type-$applicableTo") {
                id = "ru.pearx.multigradle.$type.$applicableTo"
                displayName = "MultiGradle ${type.capitalize()} [${applicableTo.capitalize()}]"
                description = "A plugin that simplifies the creation of $type multiplatform Kotlin projects."
                implementationClass = "ru.pearx.multigradle.plugin.$type.MultiGradle${type.capitalize()}${applicableTo.capitalize()}"
            }
        }

        createMultiGradlePlugin("modular", "settings")
        createMultiGradlePlugin("modular", "project")
        createMultiGradlePlugin("simple", "settings")
        createMultiGradlePlugin("simple", "project")
    }
}

publishing {
    repositories {
        fun AuthenticationSupported.pearxCredentials() {
            credentials {
                username = properties["ru.pearx.repo.usr"].toString()
                password = properties["ru.pearx.repo.psw"].toString()
            }
        }
        maven {
            pearxCredentials()
            name = "develop"
            url = uri("https://repo.pearx.ru/maven2/develop/")
        }
        maven {
            pearxCredentials()
            name = "release"
            url = uri("https://repo.pearx.ru/maven2/release/")
        }
    }
}

pluginBundle {
    website = "https://github.com/pearxteam/multigradle"
    vcsUrl = "https://github.com/pearxteam/multigradle"
    tags = listOf("kotlin", "multiplatform", "modular", "kotlin-multiplatform")
    mavenCoordinates {
        groupId = "ru.pearx.multigradle"
    }
}

tasks {
    register("publishDevelop") {
        group = "publishing"
        dependsOn(withType<PublishToMavenRepository>().matching { it.repository == publishing.repositories["develop"] })
    }
    register("publishRelease") {
        group = "publishing"
        dependsOn(withType<PublishToMavenRepository>().matching { it.repository == publishing.repositories["release"] })
        dependsOn(named("publishPlugins"))
    }
}