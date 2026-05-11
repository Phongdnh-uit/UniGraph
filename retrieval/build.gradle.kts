plugins {
    java
    id("com.diffplug.spotless") version "8.2.1"
    id("org.springframework.boot") version "4.0.4"
    id("io.spring.dependency-management") version "1.1.7"
}

group = "com.uni-graph"
version = "0.0.1-SNAPSHOT"

spotless {
    java {
        target("**/src/**/*.java")
        googleJavaFormat()
        removeUnusedImports()
        trimTrailingWhitespace()
        endWithNewline()
    }
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(25)
    }
}

repositories {
    mavenCentral()
}

extra["springAiVersion"] = "2.0.0-M3"

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-data-neo4j")
    implementation("org.springframework.boot:spring-boot-starter-webmvc")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:3.0.1")
    implementation("dev.langchain4j:langchain4j:0.36.2")
    implementation("dev.langchain4j:langchain4j-ollama:0.36.2")
    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.boot:spring-boot-starter-webmvc-test")
}

dependencyManagement {
    imports {
        mavenBom("org.springframework.ai:spring-ai-bom:${property("springAiVersion")}")
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}
