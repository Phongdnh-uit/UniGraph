# Refactor Retrieval Services Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Improve reliability and error handling in retrieval services.

**Architecture:** 
- Add try-catch blocks around AI model calls (embedding and chat).
- Gracefully handle empty search results in the chat service by adjusting the prompt or returning a default message.
- Ensure the system remains functional (e.g., fallback to keyword search) even if some components fail.

**Tech Stack:** Java, Spring Boot, LangChain4j, JUnit, Mockito.

---

### Task 1: Error Handling in SearchServiceImpl

**Files:**
- Modify: `retrieval/src/main/java/com/uni_graph/retrieval/service/impl/SearchServiceImpl.java`
- Test: `retrieval/src/test/java/com/uni_graph/retrieval/service/impl/SearchServiceImplTest.java`

- [ ] **Step 1: Write a failing test for embedding failure**

```java
    @Test
    void hybridSearch_shouldFallbackToKeywordSearch_whenEmbeddingFails() {
        // Arrange
        String query = "AI";
        when(embeddingModel.embed(query)).thenThrow(new RuntimeException("AI Provider down"));

        Course c2 = new Course();
        c2.setCode("AI101");
        c2.setTitleVn("Artificial Intelligence");

        when(courseRepository.searchByKeyword(query)).thenReturn(List.of(c2));

        // Act
        List<Course> results = searchService.hybridSearch(query);

        // Assert
        assertThat(results).hasSize(1);
        assertThat(results.get(0).getCode()).isEqualTo("AI101");
        verify(courseRepository).searchByKeyword(query);
    }
```

- [ ] **Step 2: Run tests to verify it fails**

Run: `./gradlew :retrieval:test --tests "com.uni_graph.retrieval.service.impl.SearchServiceImplTest"`

- [ ] **Step 3: Implement error handling in SearchServiceImpl**

```java
    @Override
    public List<Course> hybridSearch(String query) {
        List<Course> vectorResults = new ArrayList<>();
        try {
            // 1. Vector Search
            var embeddingContent = embeddingModel.embed(query).content();
            List<Double> vector = new ArrayList<>();
            for (float f : embeddingContent.vector()) vector.add((double) f);
            vectorResults = courseRepository.searchByVector(vector, 5);
        } catch (Exception e) {
            // Log error (implicitly in this context, we just catch and proceed)
            // Fallback to empty vector results
        }

        // 2. Keyword Search
        List<Course> keywordResults = courseRepository.searchByKeyword(query);

        // 3. Simple merge (Avoid duplicates)
        List<Course> results = new ArrayList<>(vectorResults);
        // ... (rest of the merge logic)
        return results;
    }
```

- [ ] **Step 4: Run tests to verify they pass**

Run: `./gradlew :retrieval:test --tests "com.uni_graph.retrieval.service.impl.SearchServiceImplTest"`

- [ ] **Step 5: Commit**

```bash
git add retrieval/src/main/java/com/uni_graph/retrieval/service/impl/SearchServiceImpl.java retrieval/src/test/java/com/uni_graph/retrieval/service/impl/SearchServiceImplTest.java
git commit -m "refactor: add error handling to SearchServiceImpl"
```

---

### Task 2: Handle Empty Results and Error Handling in ChatServiceImpl

**Files:**
- Modify: `retrieval/src/main/java/com/uni_graph/retrieval/service/impl/ChatServiceImpl.java`
- Test: `retrieval/src/test/java/com/uni_graph/retrieval/service/impl/ChatServiceImplTest.java`

- [ ] **Step 1: Write failing tests for empty results and chat model failure**

```java
    @Test
    void chat_shouldHandleEmptyContext() {
        // Arrange
        String message = "Môn học bí ẩn";
        when(searchService.hybridSearch(message)).thenReturn(List.of());
        when(chatModel.generate(anyString())).thenReturn("Xin lỗi, tôi không tìm thấy thông tin về môn học này.");

        // Act
        String response = chatService.chat(message);

        // Assert
        assertThat(response).isNotEmpty();
        verify(chatModel).generate(anyString());
    }

    @Test
    void chat_shouldHandleChatModelFailure() {
        // Arrange
        String message = "Hello";
        when(searchService.hybridSearch(message)).thenReturn(List.of());
        when(chatModel.generate(anyString())).thenThrow(new RuntimeException("Chat service down"));

        // Act
        String response = chatService.chat(message);

        // Assert
        assertThat(response).contains("đang gặp sự cố");
    }
```

- [ ] **Step 2: Run tests to verify they fail**

Run: `./gradlew :retrieval:test --tests "com.uni_graph.retrieval.service.impl.ChatServiceImplTest"`

- [ ] **Step 3: Implement changes in ChatServiceImpl**

```java
    @Override
    public String chat(String message) {
        List<Course> contextCourses = searchService.hybridSearch(message);
        
        String prompt;
        if (contextCourses.isEmpty()) {
            prompt = String.format(
                "Bạn là trợ lý học tập. Tôi không tìm thấy thông tin nào về các môn học liên quan đến: %s. " +
                "Hãy trả lời người dùng rằng bạn không tìm thấy thông tin và có thể gợi ý họ hỏi về các môn học khác.",
                message
            );
        } else {
            String context = contextCourses.stream()
                .map(c -> String.format("Môn %s (%s): %s", c.getTitleVn(), c.getCode(), c.getSummary()))
                .collect(Collectors.joining("\n"));

            prompt = String.format(
                "Bạn là trợ lý học tập. Dựa vào thông tin các môn học sau đây:\n%s\n\nHãy trả lời câu hỏi: %s",
                context, message
            );
        }

        try {
            return chatModel.generate(prompt);
        } catch (Exception e) {
            return "Xin lỗi, dịch vụ tư vấn đang gặp sự cố. Vui lòng thử lại sau.";
        }
    }
```

- [ ] **Step 4: Run tests to verify they pass**

Run: `./gradlew :retrieval:test --tests "com.uni_graph.retrieval.service.impl.ChatServiceImplTest"`

- [ ] **Step 5: Commit**

```bash
git add retrieval/src/main/java/com/uni_graph/retrieval/service/impl/ChatServiceImpl.java retrieval/src/test/java/com/uni_graph/retrieval/service/impl/ChatServiceImplTest.java
git commit -m "refactor: handle empty search results and chat model failures in ChatServiceImpl"
```

---

### Task 3: Final Verification

- [ ] **Step 1: Run full build**

Run: `./gradlew build`

- [ ] **Step 2: Final report**
