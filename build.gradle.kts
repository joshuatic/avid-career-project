plugins {
    application
    id("org.openjfx.javafxplugin") version "0.0.13"
}

repositories {
    mavenCentral()
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

javafx {
    version = "21"
    modules = listOf("javafx.controls")
}

application {
    mainClass.set("dev.joshuatic.avidcareerproject.Main")
}

tasks.withType<JavaExec> {
    jvmArgs = listOf(
        "--add-modules=javafx.controls"
    )
}

tasks.jar {
    manifest {
        attributes["Main-Class"] = "dev.joshuatic.avidcareerproject.Main"
    }
}