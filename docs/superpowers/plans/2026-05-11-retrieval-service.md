# Retrieval Service Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Build a Hybrid Search and RAG service for querying academic data in Neo4j.

**Architecture:** A Spring Boot 4.0.4 application using LangChain4j for AI and Spring Data Neo4j for database access.

**Tech Stack:** Java 25, Spring Boot 4.0.4, Neo4j, LangChain4j 0.36.2, Ollama.

---

### Task 1: Project Scaffolding

**Files:**
- Create: `retrieval/build.gradle.kts`
- Create: `retrieval/settings.gradle.kts`
- Create: `retrieval/src/main/java/com/uni_graph/retrieval/RetrievalApplication.java`
- Create: `retrieval/src/main/resources/application.yaml`

- [ ] **Step 1: Create settings.gradle.kts**

```kotlin
rootProject.name = "retrieval"
```

- [ ] **Step 2: Create build.gradle.kts**
(Based on ingestion/build.gradle.kts)

```kotlin
plugins {
    java
    id("org.springframework.boot") version "4.0.4"
    id("io.spring.dependency-management") version "1.1.7"
}

group = "com.uni-graph"
version = "0.0.1-SNAPSHOT"

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
}

dependencyManagement {
    imports {
        mavenBom("org.springframework.ai:spring-ai-bom:${property("springAiVersion")}")
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}
```

- [ ] **Step 3: Create RetrievalApplication.java**

```java
package com.uni_graph.retrieval;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class RetrievalApplication {
    public static void main(String[] args) {
        SpringApplication.run(RetrievalApplication.class, args);
    }
}
```

- [ ] **Step 4: Create application.yaml**

```yaml
spring:
  application:
    name: retrieval
  data:
    neo4j:
      uri: ${NEO4J_URI:bolt://localhost:7687}
      username: ${NEO4J_USERNAME:neo4j}
      password: ${NEO4J_PASSWORD:password}

ai:
  ollama:
    base-url: ${OLLAMA_BASE_URL:http://localhost:11434}
    embedding-model: ${OLLAMA_EMBEDDING_MODEL:bge-m3}
    chat-model: ${OLLAMA_CHAT_MODEL:llama3}
```

- [ ] **Step 5: Verify build**

Run: `cd retrieval && ./gradlew build` (Note: need to copy gradlew from ingestion first)

- [ ] **Step 6: Commit**

```bash
git add retrieval/
git commit -m "chore: scaffold retrieval project"
```

---

### Task 2: AI and Database Configuration

**Files:**
- Create: `retrieval/src/main/java/com/uni_graph/retrieval/config/AiConfig.java`

- [ ] **Step 1: Implement AiConfig.java**

```java
package com.uni_graph.retrieval.config;

import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.ollama.OllamaEmbeddingModel;
import dev.langchain4j.model.ollama.OllamaChatModel;
import java.time.Duration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AiConfig {

    @Value("${ai.ollama.base-url}")
    private String ollamaBaseUrl;

    @Value("${ai.ollama.embedding-model}")
    private String embeddingModelName;

    @Value("${ai.ollama.chat-model}")
    private String chatModelName;

    @Bean
    public EmbeddingModel embeddingModel() {
        return OllamaEmbeddingModel.builder()
            .baseUrl(ollamaBaseUrl)
            .modelName(embeddingModelName)
            .timeout(Duration.ofSeconds(60))
            .build();
    }

    @Bean
    public ChatLanguageModel chatLanguageModel() {
        return OllamaChatModel.builder()
            .baseUrl(ollamaBaseUrl)
            .modelName(chatModelName)
            .timeout(Duration.ofSeconds(120))
            .build();
    }
}
```

- [ ] **Step 2: Commit**

```bash
git add retrieval/src/main/java/com/uni_graph/retrieval/config/AiConfig.java
git commit -m "feat: add AI configuration"
```

---

### Task 3: Domain and Repository

**Files:**
- Create: `retrieval/src/main/java/com/uni_graph/retrieval/domain/Course.java`
- Create: `retrieval/src/main/java/com/uni_graph/retrieval/repository/CourseRepository.java`

- [ ] **Step 1: Create Course.java** (Minimal mapping for retrieval)

```java
package com.uni_graph.retrieval.domain;

import lombok.Data;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Property;

@Node("Course")
@Data
public class Course {
    @Id
    private String code;
    
    @Property("title_vn")
    private String titleVn;
    
    @Property("title_en")
    private String titleEn;
    
    private String summary;
}
```

- [ ] **Step 2: Create CourseRepository.java with Vector Search**

```java
package com.uni_graph.retrieval.repository;

import com.uni_graph.retrieval.domain.Course;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import java.util.List;

public interface CourseRepository extends Neo4jRepository<Course, String> {
    
    @Query("MATCH (c:Course) WHERE c.title_vn CONTAINS $query OR c.title_en CONTAINS $query RETURN c")
    List<Course> searchByKeyword(String query);

    @Query("CALL db.index.vector.queryNodes('course_embeddings', $topK, $embedding) " +
           "YIELD node, score " +
           "RETURN node")
    List<Course> searchByVector(List<Double> embedding, int topK);
}
```

- [ ] **Step 3: Commit**

```bash
git add retrieval/src/main/java/com/uni_graph/retrieval/domain/Course.java retrieval/src/main/java/com/uni_graph/retrieval/repository/CourseRepository.java
git commit -m "feat: add Course domain and repository with vector search"
```

---

### Task 4: Search and Chat Services

**Files:**
- Create: `retrieval/src/main/java/com/uni_graph/retrieval/service/SearchService.java`
- Create: `retrieval/src/main/java/com/uni_graph/retrieval/service/ChatService.java`
- Create: `retrieval/src/main/java/com/uni_graph/retrieval/service/impl/SearchServiceImpl.java`
- Create: `retrieval/src/main/java/com/uni_graph/retrieval/service/impl/ChatServiceImpl.java`

- [ ] **Step 1: Define SearchService interface**

```java
package com.uni_graph.retrieval.service;

import com.uni_graph.retrieval.domain.Course;
import java.util.List;

public interface SearchService {
    List<Course> hybridSearch(String query);
}
```

- [ ] **Step 2: Define ChatService interface**

```java
package com.uni_graph.retrieval.service;

public interface ChatService {
    String chat(String message);
}
```

- [ ] **Step 3: Implement SearchServiceImpl**

```java
package com.uni_graph.retrieval.service.impl;

import com.uni_graph.retrieval.domain.Course;
import com.uni_graph.retrieval.repository.CourseRepository;
import com.uni_graph.retrieval.service.SearchService;
import dev.langchain4j.model.embedding.EmbeddingModel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class SearchServiceImpl implements SearchService {
    private final CourseRepository courseRepository;
    private final EmbeddingModel embeddingModel;

    @Override
    public List<Course> hybridSearch(String query) {
        // 1. Vector Search
        var embeddingContent = embeddingModel.embed(query).content();
        List<Double> vector = new ArrayList<>();
        for (float f : embeddingContent.vector()) vector.add((double) f);
        List<Course> vectorResults = courseRepository.searchByVector(vector, 5);

        // 2. Keyword Search
        List<Course> keywordResults = courseRepository.searchByKeyword(query);

        // 3. Simple merge (Avoid duplicates)
        List<Course> results = new ArrayList<>(vectorResults);
        for (Course c : keywordResults) {
            if (results.stream().noneMatch(r -> r.getCode().equals(c.getCode()))) {
                results.add(c);
            }
        }
        return results;
    }
}
```

- [ ] **Step 4: Implement ChatServiceImpl (RAG)**

```java
package com.uni_graph.retrieval.service.impl;

import com.uni_graph.retrieval.domain.Course;
import com.uni_graph.retrieval.service.ChatService;
import com.uni_graph.retrieval.service.SearchService;
import dev.langchain4j.model.chat.ChatLanguageModel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {
    private final SearchService searchService;
    private final ChatLanguageModel chatModel;

    @Override
    public String chat(String message) {
        List<Course> contextCourses = searchService.hybridSearch(message);
        
        String context = contextCourses.stream()
            .map(c -> String.format("Môn %s (%s): %s", c.getTitleVn(), c.getCode(), c.getSummary()))
            .collect(Collectors.joining("\n"));

        String prompt = String.format(
            "Bạn là trợ lý học tập. Dựa vào thông tin các môn học sau đây:\n%s\n\nHãy trả lời câu hỏi: %s",
            context, message
        );

        return chatModel.generate(prompt);
    }
}
```

- [ ] **Step 5: Commit**

```bash
git add retrieval/src/main/java/com/uni_graph/retrieval/service/ retrieval/src/main/java/com/uni_graph/retrieval/service/impl/
git commit -m "feat: implement search and chat services"
```

---

### Task 5: Controller and Final Setup

**Files:**
- Create: `retrieval/src/main/java/com/uni_graph/retrieval/controllers/RetrievalController.java`

- [ ] **Step 1: Implement RetrievalController**

```java
package com.uni_graph.retrieval.controllers;

import com.uni_graph.retrieval.domain.Course;
import com.uni_graph.retrieval.service.ChatService;
import com.uni_graph.retrieval.service.SearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class RetrievalController {
    private final SearchService searchService;
    private final ChatService chatService;

    @GetMapping("/search")
    public List<Course> search(@RequestParam String q) {
        return searchService.hybridSearch(q);
    }

    @PostMapping("/chat")
    public String chat(@RequestBody String message) {
        return chatService.chat(message);
    }
}
```

- [ ] **Step 2: Final Verification**

Run: `cd retrieval && ./gradlew build`

- [ ] **Step 3: Commit**

```bash
git add retrieval/src/main/java/com/uni_graph/retrieval/controllers/RetrievalController.java
git commit -m "feat: add RetrievalController"
```
