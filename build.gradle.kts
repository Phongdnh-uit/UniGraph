plugins {
    java
    id("com.diffplug.spotless") version "8.2.1"
    id("org.springframework.boot") version "4.0.6" apply false
    id("io.spring.dependency-management") version "1.1.7" apply false
}

spotless {
    java {
        target("**/src/**/*.java")
        googleJavaFormat()
        removeUnusedImports()
        trimTrailingWhitespace()
        endWithNewline()
    }
}

allprojects {
    group = "com.uit.unigraph"
    version = "0.0.1-SNAPSHOT"
    description = "A unified graph database management system for heterogeneous graph databases."
    repositories {
        mavenCentral()
    }
}

subprojects {
    apply(plugin = "java")
    apply(plugin = "com.diffplug.spotless")

    group = "com.unigraph"
    version = "0.0.1-SNAPSHOT"

    repositories {
        mavenCentral()
    }

    java {
        toolchain {
            languageVersion = JavaLanguageVersion.of(25)
        }
    }

    tasks.withType<Test> {
        useJUnitPlatform()
    }

    configurations {
        compileOnly {
            extendsFrom(configurations.annotationProcessor.get())
        }
    }
}
