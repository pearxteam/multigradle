package ru.pearx.multigradle.util.platform.extensions

import org.gradle.api.Project
import org.gradle.api.artifacts.Dependency
import org.gradle.api.artifacts.ProjectDependency
import ru.pearx.multigradle.util.platform.Platform

fun Project.mpdep(notation: String, unnamedPlatform: Platform?): Dependency
{
    val dep = dependencies.create(notation)
    val platform = Platform.valueOfCodeName(name)
    return dependencies.create(mapOf("group" to dep.group, "name" to (dep.name + if (platform == unnamedPlatform) "" else "-${platform.codeName}"), "version" to dep.version))
}

fun Project.mpdep(module: ProjectDependency): Project = mpdep(module.dependencyProject)

fun Project.mpdep(module: Project): Project = module.project(name)