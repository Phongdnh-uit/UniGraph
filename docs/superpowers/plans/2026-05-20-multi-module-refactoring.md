# Multi-Module Refactoring Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Refactor the UniGraph project into a multi-module Gradle structure with a shared `common` module containing domain entities, enums, DTOs, and exceptions.

**Architecture:** A root Gradle project managing three sub-projects: `common` (Java library), `ingestion` (Spring Boot app), and `retrieval` (Spring Boot app). `ingestion` and `retrieval` will depend on `common`.

**Tech Stack:** Java 25, Gradle (Kotlin DSL), Spring Boot 4.0.4, Spring Data Neo4j, LangChain4j, Lombok.

---

### Task 1: Root Project Initialization

**Files:**
- Create: `settings.gradle.kts`
- Create: `build.gradle.kts`
- Modify: `.gitignore`

- [ ] **Step 1: Create root `settings.gradle.kts`**

```kotlin
rootProject.name = "uni-graph"
include("common")
include("ingestion")
include("retrieval")
```

- [ ] **Step 2: Create root `build.gradle.kts`**

```kotlin
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
```

- [ ] **Step 3: Update `.gitignore` to ignore root Gradle artifacts**

```bash
echo ".gradle/" >> .gitignore
echo "build/" >> .gitignore
```

- [ ] **Step 4: Commit root setup**

```bash
git add settings.gradle.kts build.gradle.kts .gitignore
git commit -m "chore: initialize root multi-module project"
```

---

### Task 2: Setup `common` Module and Migrate Code

**Files:**
- Create: `common/build.gradle.kts`
- Move: `ingestion/src/main/java/com/uni_graph/ingestion/domain/*` -> `common/src/main/java/com/uni_graph/common/domain/*`
- Move: `ingestion/src/main/java/com/uni_graph/ingestion/enums/*` -> `common/src/main/java/com/uni_graph/common/enums/*`
- Move: `ingestion/src/main/java/com/uni_graph/ingestion/dto/ApiResponse.java` -> `common/src/main/java/com/uni_graph/common/dto/ApiResponse.java`
- Move: `ingestion/src/main/java/com/uni_graph/ingestion/exception/*` -> `common/src/main/java/com/uni_graph/common/exception/*`

- [ ] **Step 1: Create `common/build.gradle.kts`**

```kotlin
plugins {
    `java-library`
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-data-neo4j:3.4.3")
    implementation("org.springframework.boot:spring-boot-starter-webmvc:3.4.3")
    compileOnly("org.projectlombok:lombok:1.18.36")
    annotationProcessor("org.projectlombok:lombok:1.18.36")
}
```

- [ ] **Step 2: Move files and update package declarations**

Use `mkdir -p` to create folders and `mv` to move files. Update `package com.uni_graph.ingestion...` to `package com.uni_graph.common...` in all moved files.

```bash
mkdir -p common/src/main/java/com/uni_graph/common/domain
mkdir -p common/src/main/java/com/uni_graph/common/enums
mkdir -p common/src/main/java/com/uni_graph/common/dto
mkdir -p common/src/main/java/com/uni_graph/common/exception

mv ingestion/src/main/java/com/uni_graph/ingestion/domain/*.java common/src/main/java/com/uni_graph/common/domain/
mv ingestion/src/main/java/com/uni_graph/ingestion/enums/*.java common/src/main/java/com/uni_graph/common/enums/
mv ingestion/src/main/java/com/uni_graph/ingestion/dto/ApiResponse.java common/src/main/java/com/uni_graph/common/dto/
mv ingestion/src/main/java/com/uni_graph/ingestion/exception/*.java common/src/main/java/com/uni_graph/common/exception/
```

- [ ] **Step 3: Update package names in moved files**

Run a script to replace `package com.uni_graph.ingestion` with `package com.uni_graph.common` in the `common/src/main/java/com/uni_graph/common/` directory.

- [ ] **Step 4: Commit common module migration**

```bash
git add common/
git commit -m "feat: migrate shared code to common module"
```

---

### Task 3: Refactor `ingestion` Module

**Files:**
- Modify: `ingestion/build.gradle.kts`
- Remove: `ingestion/settings.gradle.kts` (redundant)
- Modify: All remaining `.java` files in `ingestion` (update imports)

- [ ] **Step 1: Update `ingestion/build.gradle.kts`**

Remove global settings (spotless, java version, version, group) as they are now in the root. Add `common` dependency.

```kotlin
plugins {
    id("org.springframework.boot")
    id("io.spring.dependency-management")
}

dependencies {
    implementation(project(":common"))
    implementation("org.springframework.boot:spring-boot-starter-data-neo4j")
    implementation("org.springframework.boot:spring-boot-starter-webmvc")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:3.0.1")
    implementation("com.opencsv:opencsv:5.9")
    implementation("eu.michael-simons.neo4j:neo4j-migrations-spring-boot-starter:3.3.0")
    implementation("dev.langchain4j:langchain4j:0.36.2")
    implementation("dev.langchain4j:langchain4j-ollama:0.36.2")
    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")
    testImplementation("org.springframework.boot:spring-boot-starter-data-neo4j-test")
    testImplementation("org.springframework.boot:spring-boot-starter-webmvc-test")
}
```

- [ ] **Step 2: Update imports in `ingestion` module**

Replace all `import com.uni_graph.ingestion.domain.*`, `com.uni_graph.ingestion.enums.*`, `com.uni_graph.ingestion.dto.ApiResponse`, and `com.uni_graph.ingestion.exception.*` with their new `com.uni_graph.common.*` counterparts.

- [ ] **Step 3: Remove redundant files**

```bash
rm ingestion/settings.gradle.kts
rm -rf ingestion/.gradle ingestion/build
```

- [ ] **Step 4: Commit ingestion refactoring**

```bash
git add ingestion/
git commit -m "refactor: update ingestion module to use common module"
```

---

### Task 4: Refactor `retrieval` Module

**Files:**
- Modify: `retrieval/build.gradle.kts`
- Remove: `retrieval/settings.gradle.kts` (redundant)
- Remove: `retrieval/src/main/java/com/uni_graph/retrieval/domain/Course.java`
- Modify: All remaining `.java` files in `retrieval` (update imports)

- [ ] **Step 1: Update `retrieval/build.gradle.kts`**

Similar to `ingestion`, remove global settings and add `common` dependency.

```kotlin
plugins {
    id("org.springframework.boot")
    id("io.spring.dependency-management")
}

dependencies {
    implementation(project(":common"))
    implementation("org.springframework.boot:spring-boot-starter-data-neo4j")
    implementation("org.springframework.boot:spring-boot-starter-webmvc")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:3.0.1")
    implementation("dev.langchain4j:langchain4j:0.36.2")
    implementation("dev.langchain4j:langchain4j-ollama:0.36.2")
    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}
```

- [ ] **Step 2: Remove duplicated `Course.java` and redundant files**

```bash
rm retrieval/src/main/java/com/uni_graph/retrieval/domain/Course.java
rm retrieval/settings.gradle.kts
rm -rf retrieval/.gradle retrieval/build
```

- [ ] **Step 3: Update imports in `retrieval` module**

Replace all `import com.uni_graph.retrieval.domain.Course` with `import com.uni_graph.common.domain.Course`. Also check for any other imports that should point to `common`.

- [ ] **Step 4: Commit retrieval refactoring**

```bash
git add retrieval/
git commit -m "refactor: update retrieval module to use common module"
```

---

### Task 5: Final Verification

- [ ] **Step 1: Run full build**

Run: `./gradlew build` from root.
Expected: SUCCESS

- [ ] **Step 2: Verify `ingestion` application starts**

Run: `./gradlew :ingestion:bootRun` (ensure required infra like Neo4j is available if needed, or just check compilation/tests).

- [ ] **Step 3: Verify `retrieval` application starts**

Run: `./gradlew :retrieval:bootRun`

- [ ] **Step 4: Final commit and cleanup**

Check for any missed files or unnecessary folders (like `.settings`, `.idea` inside submodules that should be handled at root).
