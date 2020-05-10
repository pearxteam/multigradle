rootProject.name = "multigradle"

pluginManagement {
    val kotlinVersion: String by settings
    val pluginPublishVersion: String by settings
    val githubReleaseVersion: String by settings
    plugins {
        kotlin("jvm") version kotlinVersion
        id("com.gradle.plugin-publish") version pluginPublishVersion
        id("com.github.breadmoirai.github-release") version githubReleaseVersion
    }
}

include("multigradle", "multigradle-settings")