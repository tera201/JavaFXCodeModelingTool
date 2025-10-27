plugins {
    alias(libs.plugins.kotlin)
    alias(libs.plugins.javafxplugin)
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
}

group = "org.tera201"
version = "1.2.2-SNAPSHOT"

val javafxVersion = libs.versions.javafx.get()

repositories {
    mavenCentral()
}


javafx {
    version = javafxVersion
    modules = listOf("javafx.controls")
}