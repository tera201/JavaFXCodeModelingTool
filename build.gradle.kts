plugins {
    kotlin("jvm") version "1.8.0"
    id("org.openjfx.javafxplugin") version "0.0.14"
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
}

group = "org.tera201"
version = "1.2.2-SNAPSHOT"

val javafxVersion = "20"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.openjfx:javafx-controls:$javafxVersion")
}

javafx {
    version = javafxVersion
    modules = listOf("javafx.controls")
}