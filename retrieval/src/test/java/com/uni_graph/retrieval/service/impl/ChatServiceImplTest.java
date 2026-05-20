package com.uni_graph.retrieval.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.uni_graph.retrieval.domain.Course;
import com.uni_graph.retrieval.service.SearchService;
import dev.langchain4j.model.chat.ChatLanguageModel;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class ChatServiceImplTest {

  @Mock private SearchService searchService;

  @Mock private ChatLanguageModel chatModel;

  @InjectMocks private ChatServiceImpl chatService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void chat_shouldUseContextFromSearch() {
    // Arrange
    String message = "Kể tên môn học về AI";
    Course c1 = new Course();
    c1.setCode("AI101");
    c1.setTitleVn("Trí tuệ nhân tạo");
    c1.setSummary("Học về AI");

    when(searchService.hybridSearch(message)).thenReturn(List.of(c1));
    when(chatModel.generate(anyString())).thenReturn("Môn học về AI là Trí tuệ nhân tạo (AI101).");

    // Act
    String response = chatService.chat(message);

    // Assert
    assertThat(response).contains("AI101");
    verify(searchService).hybridSearch(message);
    verify(chatModel).generate(anyString());
  }

  @Test
  void chat_shouldHandleEmptyContext() {
    // Arrange
    String message = "Môn học bí ẩn";
    when(searchService.hybridSearch(message)).thenReturn(List.of());
    when(chatModel.generate(anyString()))
        .thenReturn("Xin lỗi, tôi không tìm thấy thông tin về môn học này.");

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
}
