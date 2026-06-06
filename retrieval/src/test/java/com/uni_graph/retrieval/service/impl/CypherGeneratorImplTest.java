package com.uni_graph.retrieval.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import com.uni_graph.retrieval.service.SchemaService;
import dev.langchain4j.model.chat.ChatModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

class CypherGeneratorImplTest {

  private ChatModel chatLanguageModel;
  private SchemaService schemaService;
  private CypherGeneratorImpl cypherGenerator;

  @BeforeEach
  void setUp() {
    chatLanguageModel = mock(ChatModel.class);
    schemaService = mock(SchemaService.class);
    cypherGenerator = new CypherGeneratorImpl(chatLanguageModel, schemaService);
  }

  @Test
  void generate_ShouldIncludeSchemaInPrompt() {
    // Given
    String question = "What are the prerequisites of EN001?";
    String mockSchema = "Nodes: Course {code}";
    when(schemaService.getFormattedSchema()).thenReturn(mockSchema);

    when(chatLanguageModel.chat(anyString()))
        .thenReturn("MATCH (c:Course {code: 'EN001'}) RETURN c");

    // When
    cypherGenerator.generate(question);

    // Then
    verify(schemaService).getFormattedSchema();

    ArgumentCaptor<String> promptCaptor = ArgumentCaptor.forClass(String.class);
    verify(chatLanguageModel).chat(promptCaptor.capture());
    assertThat(promptCaptor.getValue()).contains(mockSchema);
    assertThat(promptCaptor.getValue()).contains(question);
  }
}
