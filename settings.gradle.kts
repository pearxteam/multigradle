rootProject.name = "multigradle"

val kotlinVersion: String by settings
val pluginPublishVersion: String by settings

enableFeaturePreview("STABLE_PUBLISHING")

pluginManagement {
    resolutionStrategy {
        eachPlugin {
            if(requested.id.id == "com.gradle.plugin-publish")
                useVersion(pluginPublishVersion)
        }
    }
}