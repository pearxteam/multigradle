plugins {
    id("com.gradle.plugin-publish")
    `java-gradle-plugin`
    `kotlin-dsl`
}

group = "ru.pearx.multigradle"
version = properties["multigradleVersion"]!!

repositories {
    jcenter()
    gradlePluginPortal()
}

dependencies {
    "compile"(gradleApi())
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

pluginBundle {
    website = "https://github.com/pearxteam/multigradle"
    vcsUrl = "https://github.com/pearxteam/multigradle"
    tags = listOf("kotlin", "multiplatform", "modular", "kotlin-multiplatform")
    mavenCoordinates {
        artifactId = "gradle-multigradle-plugin"
        groupId = "ru.pearx.multigradle"
    }
}