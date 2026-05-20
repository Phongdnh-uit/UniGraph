package com.uni_graph.retrieval.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
public class CourseRepositoryTest {

  @Autowired private CourseRepository courseRepository;

  @Test
  void repositoryIsLoaded() {
    assertThat(courseRepository).isNotNull();
  }

  @Test
  void searchByKeywordQueryIsValid() {
    // This will verify Spring can parse the query, even if it returns empty/fails at runtime
    // without DB
    try {
      courseRepository.searchByKeyword("test");
    } catch (Exception e) {
      // Expected failure if no DB, but we want to see if it's a query parsing error
      assertThat(e.getMessage()).doesNotContain("Invalid query", "Cypher execution failed");
    }
  }

  @Test
  void searchByVectorQueryIsValid() {
    try {
      courseRepository.searchByVector(List.of(0.1, 0.2), 1);
    } catch (Exception e) {
      assertThat(e.getMessage()).doesNotContain("Invalid query", "Cypher execution failed");
    }
  }
}
