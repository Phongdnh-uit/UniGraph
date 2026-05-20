package com.uni_graph.retrieval.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.uni_graph.retrieval.domain.Course;
import com.uni_graph.retrieval.repository.CourseRepository;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.output.Response;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class SearchServiceImplTest {

  @Mock private CourseRepository courseRepository;

  @Mock private EmbeddingModel embeddingModel;

  @InjectMocks private SearchServiceImpl searchService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void hybridSearch_shouldCombineResults() {
    // Arrange
    String query = "AI";
    float[] vector = {0.1f, 0.2f};
    Embedding embedding = Embedding.from(vector);
    when(embeddingModel.embed(query)).thenReturn(Response.from(embedding));

    Course c1 = new Course();
    c1.setCode("CS101");
    c1.setTitleVn("Computer Science");

    Course c2 = new Course();
    c2.setCode("AI101");
    c2.setTitleVn("Artificial Intelligence");

    when(courseRepository.searchByVector(anyList(), anyInt())).thenReturn(List.of(c1));
    when(courseRepository.searchByKeyword(query)).thenReturn(List.of(c1, c2));

    // Act
    List<Course> results = searchService.hybridSearch(query);

    // Assert
    assertThat(results).hasSize(2);
    assertThat(results).extracting(Course::getCode).containsExactlyInAnyOrder("CS101", "AI101");
    verify(embeddingModel).embed(query);
    verify(courseRepository).searchByVector(anyList(), anyInt());
    verify(courseRepository).searchByKeyword(query);
  }

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
}
