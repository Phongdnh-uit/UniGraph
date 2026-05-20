plugins {
    java
    id("com.diffplug.spotless") version "8.2.1" apply false
    id("org.springframework.boot") version "4.0.4" apply false
    id("io.spring.dependency-management") version "1.1.7" apply false
}

subprojects {
    apply(plugin = "java")
    apply(plugin = "com.diffplug.spotless")

    group = "com.uni-graph"
    version = "0.0.1-SNAPSHOT"

    repositories {
        mavenCentral()
    }

    java {
        toolchain {
            languageVersion = JavaLanguageVersion.of(25)
        }
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
}
