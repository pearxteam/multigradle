rootProject.name = "multigradle"

val kotlinVersion: String by settings
val pluginPublishVersion: String by settings

pluginManagement {
    resolutionStrategy {
        eachPlugin {
            if(requested.id.id == "com.gradle.plugin-publish")
                useVersion(pluginPublishVersion)
        }
    }
}