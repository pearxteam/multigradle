/*
 * Copyright © 2019 mrAppleXZ
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at https://mozilla.org/MPL/2.0/.
 */

import com.github.breadmoirai.githubreleaseplugin.GithubReleaseExtension

plugins {
    `java-gradle-plugin`
    `kotlin-dsl`
    `maven-publish`
    id("com.gradle.plugin-publish")
    id("com.github.breadmoirai.github-release")
}

val projectVersion: String by project
val projectDescription: String by project
val projectChangelog: String by project
val kotlinVersion: String by project
val nodeVersion: String by project
val dokkaVersion: String by project

val devBuildNumber: String? by project
val pearxRepoUsername: String? by project
val pearxRepoPassword: String? by project
val githubAccessToken: String? by project

group = "net.pearx.multigradle"
version = if (devBuildNumber != null) "$projectVersion-dev-$devBuildNumber" else projectVersion
description = projectDescription.replace("%type%", "modular and simple")

repositories {
    jcenter()
    gradlePluginPortal()
}

dependencies {
    "api"("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion")
    "api"("com.moowork.node:com.moowork.node.gradle.plugin:$nodeVersion")
    "api"("org.jetbrains.dokka:dokka-gradle-plugin:$dokkaVersion")
}

gradlePlugin {
    plugins {
        fun createMultiGradlePlugin(type: String, applicableTo: String) {
            create("multigradle-$type-$applicableTo") {
                id = "net.pearx.multigradle.$type.$applicableTo"
                displayName = "MultiGradle ${type.capitalize()} [${applicableTo.capitalize()}]"
                description = projectDescription.replace("%type%", type)
                implementationClass = "net.pearx.multigradle.plugin.$type.MultiGradle${type.capitalize()}${applicableTo.capitalize()}"
            }
        }

        createMultiGradlePlugin("modular", "settings")
        createMultiGradlePlugin("modular", "project")
        createMultiGradlePlugin("simple", "project")
    }
}

publishing {
    repositories {
        fun AuthenticationSupported.pearxCredentials() {
            credentials {
                username = pearxRepoUsername
                password = pearxRepoPassword
            }
        }
        maven {
            pearxCredentials()
            name = "develop"
            url = uri("https://repo.pearx.net/maven2/develop/")
        }
        maven {
            pearxCredentials()
            name = "release"
            url = uri("https://repo.pearx.net/maven2/release/")
        }
    }
}

pluginBundle {
    website = "https://github.com/pearxteam/multigradle"
    vcsUrl = "https://github.com/pearxteam/multigradle"
    tags = listOf("kotlin", "multiplatform", "modular", "kotlin-multiplatform")
    mavenCoordinates {
        groupId = "net.pearx.multigradle"
    }
}

configure<GithubReleaseExtension> {
    setToken(githubAccessToken)
    setOwner("pearxteam")
    setRepo(name)
    setTargetCommitish("master")
    setBody(projectChangelog)
}

tasks {
    register("publishDevelop") {
        group = "publishing"
        dependsOn(withType<PublishToMavenRepository>().matching { it.repository == publishing.repositories["develop"] })
    }
    register("publishRelease") {
        group = "publishing"
        dependsOn(withType<PublishToMavenRepository>().matching { it.repository == publishing.repositories["release"] })
        dependsOn(named("githubRelease"))
        dependsOn(named("publishPlugins"))
    }
}