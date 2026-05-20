package com.uni_graph.ingestion.config;

import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.ollama.OllamaChatModel;
import dev.langchain4j.model.ollama.OllamaEmbeddingModel;
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

  @Value("${ai.ollama.chat-model:qwen2.5:7b}")
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
