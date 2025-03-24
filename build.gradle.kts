plugins {
    kotlin("jvm") version "2.1.0"
    id("org.openjfx.javafxplugin") version "0.1.0"
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
}

group = "org.tera201"
version = "1.2.2-SNAPSHOT"

val javafxVersion = "21"

repositories {
    mavenCentral()
}


javafx {
    version = javafxVersion
    modules = listOf("javafx.controls")
}