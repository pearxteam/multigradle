import com.github.breadmoirai.githubreleaseplugin.GithubReleaseExtension
import com.gradle.publish.PluginBundleExtension
import com.gradle.publish.PublishPlugin
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("com.gradle.plugin-publish") apply false
    id("com.github.breadmoirai.github-release")
    `kotlin-dsl` apply false
}

val projectVersion: String by project
val projectDescription: String by project
val projectChangelog: String by project
val kotlinVersion: String by project
val nodeVersion: String by project
val dokkaVersion: String by project
val androidBuildToolsVersion: String by project

val devBuildNumber: String? by project
val pearxRepoUsername: String? by project
val pearxRepoPassword: String? by project
val githubAccessToken: String? by project

fun NamedDomainObjectContainer<PluginDeclaration>.createMultiGradlePlugin(type: String, applicableTo: String) {
    create("multigradle-$type-$applicableTo") {
        id = "net.pearx.multigradle.$type.$applicableTo"
        displayName = "MultiGradle ${type.capitalize()} [${applicableTo.capitalize()}]"
        description = projectDescription.replace("%type%", type)
        implementationClass = "net.pearx.multigradle.plugin.$type.MultiGradle${type.capitalize()}${applicableTo.capitalize()}"
    }
}

subprojects {
    apply<KotlinDslPlugin>()
    apply<MavenPublishPlugin>()
    apply<JavaGradlePluginPlugin>()
    apply<PublishPlugin>()

    group = "net.pearx.multigradle"
    version = if (devBuildNumber != null) "$projectVersion-dev-$devBuildNumber" else projectVersion
    description = projectDescription.replace("%type%", "modular and simple")

    repositories {
        jcenter()
        gradlePluginPortal()
    }

    configure<PublishingExtension> {
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

    configure<PluginBundleExtension> {
        website = "https://github.com/pearxteam/multigradle"
        vcsUrl = "https://github.com/pearxteam/multigradle"
        tags = listOf("kotlin", "multiplatform", "modular", "kotlin-multiplatform")
        mavenCoordinates {
            groupId = "net.pearx.multigradle"
        }
    }

    tasks {
        register("publishDevelop") {
            group = "publishing"
            dependsOn(withType<PublishToMavenRepository>().matching { it.repository == the<PublishingExtension>().repositories["develop"] })
        }
        register("publishRelease") {
            group = "publishing"
            dependsOn(withType<PublishToMavenRepository>().matching { it.repository == the<PublishingExtension>().repositories["release"] })
        }
    }
}

project(":multigradle") {
    repositories {
        google()
    }

    dependencies {
        "api"("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion")
        "api"("com.github.node-gradle:gradle-node-plugin:$nodeVersion")
        "api"("org.jetbrains.dokka:dokka-gradle-plugin:$dokkaVersion")
        "api"("com.android.tools.build:gradle:$androidBuildToolsVersion")
    }

    configure<GradlePluginDevelopmentExtension> {
        plugins {
            createMultiGradlePlugin("modular", "project")
            createMultiGradlePlugin("simple", "project")
        }
    }

    tasks.withType<KotlinCompile>().all {
        kotlinOptions.freeCompilerArgs += "-Xopt-in=kotlin.ExperimentalStdlibApi"
    }
}

project(":multigradle-settings") {
    configure<GradlePluginDevelopmentExtension> {
        plugins {
            createMultiGradlePlugin("modular", "settings")
            createMultiGradlePlugin("simple", "settings")
        }
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
    }
    register("publishRelease") {
        group = "publishing"
        dependsOn(named("githubRelease"))
    }
}