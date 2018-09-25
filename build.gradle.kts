plugins {
    id("com.gradle.plugin-publish")
    `java-gradle-plugin`
    `kotlin-dsl`
}

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
        val multigradleVersion: String by project

        create("multigradle-modular-settings") {
            id = "ru.pearx.multigradle.modular.settings"
            displayName = "MultiGradle Modular [Settings]"
            description = "A plugin that simplifies the creation of modular multiplatform Kotlin projects."
            version = multigradleVersion
            implementationClass = "ru.pearx.multigradle.plugin.modular.MultiGradleModularSettings"
        }

        create("multigradle-modular-project") {
            id = "ru.pearx.multigradle.modular.project"
            displayName = "MultiGradle Modular [Project]"
            description = "A plugin that simplifies the creation of modular multiplatform Kotlin projects."
            version = multigradleVersion
            implementationClass = "ru.pearx.multigradle.plugin.modular.MultiGradleModularProject"
        }

        create("multigradle-simple-settings") {
            id = "ru.pearx.multigradle.simple.settings"
            displayName = "MultiGradle Simple [Settings]"
            description = "A plugin that simplifies the creation of multiplatform Kotlin projects."
            version = multigradleVersion
            implementationClass = "ru.pearx.multigradle.plugin.simple.MultiGradleSimpleSettings"
        }

        create("multigradle-simple-project") {
            id = "ru.pearx.multigradle.simple.project"
            displayName = "MultiGradle Simple [Project]"
            description = "A plugin that simplifies the creation of multiplatform Kotlin projects."
            version = multigradleVersion
            implementationClass = "ru.pearx.multigradle.plugin.simple.MultiGradleSimpleProject"
        }
    }
}

pluginBundle {
    website = "https://github.com/pearxteam/multigradle"
    vcsUrl = "https://github.com/pearxteam/multigradle"
    tags = listOf("kotlin", "multiplatform", "modular", "kotlin-multiplatform")
}