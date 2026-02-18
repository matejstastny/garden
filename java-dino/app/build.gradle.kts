val mainClassName = "jdino.App"
val displayName = "Java Dino"

plugins {
    application
    java
}

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(libs.junit.jupiter)                          // Use JUnit Jupiter for testing.
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    implementation(libs.guava)                                      // This dependency is used by the application.
    implementation("com.catppuccin:catppuccin-palette:2.0.2")       // Catppuccin color theme
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

application {
    mainClass.set(mainClassName)
}

tasks {
    test {
        useJUnitPlatform()
    }

    jar {
        archiveFileName.set("$displayName.jar")
        manifest {
            attributes(
                "Main-Class" to mainClassName,
                "Class-Path" to configurations.runtimeClasspath.get()
                    .joinToString(" ") { it.name }
            )
        }
        from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) })
    }
}
