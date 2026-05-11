package com.uni_graph.ingestion.config;

import dev.langchain4j.model.embedding.EmbeddingModel;
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

  @Bean
  public EmbeddingModel embeddingModel() {
    return OllamaEmbeddingModel.builder()
        .baseUrl(ollamaBaseUrl)
        .modelName(embeddingModelName)
        .timeout(Duration.ofSeconds(60))
        .build();
  }
}
