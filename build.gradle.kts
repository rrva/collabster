import org.jetbrains.kotlin.gradle.tasks.KotlinCompile


plugins {
    kotlin("jvm") version "1.3.61"
    kotlin("plugin.serialization") version "1.3.61"
    application
    id("org.openjfx.javafxplugin") version "0.0.8"
    id("org.beryx.jlink") version "2.17.1"
}

application {
    mainClassName = "collabster/se.rrva.collabster.CollabsterApp"
}

repositories {
    jcenter()
    mavenCentral()
}

javafx {
    modules = listOf("javafx.controls", "javafx.fxml")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation(kotlin("reflect"))
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-runtime:0.14.0")
    implementation("no.tornado:tornadofx:1.7.20") {
        exclude("org.jetbrains.kotlin")
    }
}

jlink {
    launcher {
        name = "collabster"
    }
    addExtraDependencies("javafx")
    imageZip.set(project.file("${project.buildDir}/image-zip/collabster-image.zip"))
}



sourceSets {
    main {
        java {
            resources {
                srcDir("src/main/resources")
                include("**/*.fxml", "**/*.png")
            }
        }
    }
}

