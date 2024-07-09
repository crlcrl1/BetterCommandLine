pluginManagement {
    repositories {
        maven {
            name = "Fabric"
            url = uri("https://maven.fabricmc.net/")
        }
        gradlePluginPortal()
        mavenCentral()
    }

    val kotlinVersion: String by settings
    val fabricLoomVersion: String by settings

    plugins {
        kotlin("jvm") version kotlinVersion
        id("fabric-loom") version fabricLoomVersion
    }
}
