# Design Document: Multi-Module Refactoring for UniGraph

**Date:** 2026-05-20
**Status:** Draft
**Topic:** Transitioning to a multi-module Gradle project with a `common` library module.

## 1. Overview
The UniGraph project currently consists of two independent Spring Boot services: `ingestion` and `retrieval`. These services share several domain concepts (e.g., `Course`), enums, and utility classes, leading to code duplication and potential inconsistencies. This design proposes refactoring the project into a multi-module structure with a shared `common` module.

## 2. Proposed Architecture

The project will follow a standard Gradle multi-module structure:

```text
UniGraph/ (Root)
├── settings.gradle.kts           # Defines modules: :common, :ingestion, :retrieval
├── build.gradle.kts              # Global configurations (Java 25, Spotless, Repositories)
├── common/                       # Shared library module (java-library)
│   ├── build.gradle.kts          # Shared dependencies (Neo4j, Lombok, etc.)
│   └── src/main/java/com/uni_graph/common/
│       ├── domain/               # Shared Neo4j Entities (Course, Student, etc.)
│       ├── enums/                # Shared Enums (ErrorCode, RuleType, etc.)
│       ├── dto/                  # Shared DTOs (ApiResponse)
│       └── exception/            # Shared Exception Handling (AppException)
├── ingestion/                    # Spring Boot Application (Data Ingestion)
│   ├── build.gradle.kts          # Depends on :common
│   └── src/main/java/com/uni_graph/ingestion/
└── retrieval/                    # Spring Boot Application (RAG & Search)
    ├── build.gradle.kts          # Depends on :common
    └── src/main/java/com/uni_graph/retrieval/
```

## 3. Detailed Component Mapping

### 3.1. Module: `common`
This module will be a `java-library` containing all shared code.

| Category | Classes to Move/Create | Source (Current) |
| :--- | :--- | :--- |
| **Domain** | `Course`, `Student`, `Teacher`, `Classroom`, `Department`, `Group`, `Section`, `Semester`, `TimeSlot`, `RequirementRule` | `ingestion.domain.*` |
| **Enums** | `ErrorCode`, `LogicType`, `RuleType` | `ingestion.enums.*` |
| **DTO** | `ApiResponse` | `ingestion.dto.*` |
| **Exception** | `AppException`, `GlobalExceptionHandler` | `ingestion.exception.*` |

### 3.2. Module: `ingestion`
*   **Role:** Handles data crawling, CSV ingestion, and embedding generation.
*   **Dependencies:** `:common`, `spring-boot-starter-webmvc`, `spring-boot-starter-data-neo4j`, `langchain4j-ollama`, `opencsv`.
*   **Refactor:** Update all imports from `com.uni_graph.ingestion.domain.*` to `com.uni_graph.common.domain.*`.

### 3.3. Module: `retrieval`
*   **Role:** Handles RAG-based search and chat functionalities.
*   **Dependencies:** `:common`, `spring-boot-starter-webmvc`, `spring-boot-starter-data-neo4j`, `langchain4j-ollama`.
*   **Refactor:** 
    *   Remove `retrieval.domain.Course` (already in `common`).
    *   Update all imports to use `com.uni_graph.common.*`.
    *   `CourseRepository` will now extend `Neo4jRepository<Course, String>` where `Course` is from `common`.

## 4. Build Configuration

### 4.1. Root `settings.gradle.kts`
```kotlin
rootProject.name = "uni-graph"
include("common")
include("ingestion")
include("retrieval")
```

### 4.2. Root `build.gradle.kts`
Move common plugins and toolchain settings here using `subprojects {}`.

### 4.3. Module `build.gradle.kts`
Each module will have its specific dependencies. `ingestion` and `retrieval` will include:
```kotlin
dependencies {
    implementation(project(":common"))
}
```

## 5. Success Criteria
*   Project compiles successfully using `./gradlew build`.
*   No duplicated domain models or exception classes across modules.
*   `ingestion` and `retrieval` services run independently.
*   All existing tests pass.
