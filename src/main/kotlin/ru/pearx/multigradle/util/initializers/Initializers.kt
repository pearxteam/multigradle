@file:JvmMultifileClass
@file:JvmName("InitializersKt")

package ru.pearx.multigradle.util.initializers

import org.gradle.api.Project
import ru.pearx.multigradle.plugin.MULTIGRADLE_EXTENSION_NAME
import ru.pearx.multigradle.util.MultiGradleExtension

internal typealias Initializer = Project.(extension: MultiGradleExtension) -> Unit

internal val initializers: Map<String, Initializer> = mapOf(
    "js" to Project::jsInitializer,
    "jvm" to Project::jvmInitializer,
    "metadata" to Project::metadataInitializer
)

internal fun Project.initializeMultiGradle() {
    val extension = MultiGradleExtension().load(this)
    extensions.add(MULTIGRADLE_EXTENSION_NAME, extension)
    parentInitializer(extension)
    for((name, initializer) in initializers) {
        initializer(extension)
    }
}