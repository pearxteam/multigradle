package ru.pearx.multigradle.util.platform.extensions

import org.gradle.api.Project
import org.gradle.api.artifacts.Dependency
import org.gradle.api.artifacts.ProjectDependency
import ru.pearx.multigradle.util.platform.Platform

fun Project.subplatforms(init: Project.(platform: Platform) -> Unit)
{
    subprojects {
        for(platform in Platform.values())
            if(platform.codeName == name)
                init(platform)
    }
}