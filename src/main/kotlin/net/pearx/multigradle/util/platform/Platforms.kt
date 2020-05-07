package net.pearx.multigradle.util.platform

import net.pearx.multigradle.util.MultiGradleExtension
import org.gradle.api.Project
import org.gradle.kotlin.dsl.*

interface Platform<T : PlatformConfig> {
    val name: String
    fun initialize(project: Project)
    fun createConfig(project: Project): T
}

open class PlatformConfig(val project: Project)

inline fun <T : PlatformConfig> platform(name: String, crossinline config: (Project) -> T, crossinline initializer: Project.(() -> T) -> Unit): Platform<T> = object : Platform<T> {
    override val name: String
        get() = name

    override fun initialize(project: Project) {
        project.initializer { project.the<MultiGradleExtension>().platform(this) }
    }

    override fun createConfig(project: Project): T {
        return config(project)
    }

    override fun equals(other: Any?): Boolean {
        return (other as? Platform<*>)?.name?.equals(name) ?: false
    }

    override fun hashCode(): Int {
        return name.hashCode()
    }

    override fun toString(): String {
        return "Platform($name)"
    }
}

val PLATFORMS = setOf(JsPlatform, JvmPlatform).associateBy { it.name }