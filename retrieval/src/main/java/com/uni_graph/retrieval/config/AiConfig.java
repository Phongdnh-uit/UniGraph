package com.uni_graph.retrieval.config;

import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.ollama.OllamaEmbeddingModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import java.time.Duration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AiConfig {

  @Value("${ai.embedding.base-url}")
  private String embeddingBaseUrl;

  @Value("${ai.embedding.model}")
  private String embeddingModelName;

  @Value("${ai.embedding.timeout:60s}")
  private Duration embeddingTimeout;

  @Value("${ai.chat.base-url}")
  private String chatBaseUrl;

  @Value("${ai.chat.model}")
  private String chatModelName;

  @Value("${ai.chat.api-key:}")
  private String chatApiKey;

  @Value("${ai.chat.timeout:120s}")
  private Duration chatTimeout;

  @Bean
  public EmbeddingModel embeddingModel() {
    return OllamaEmbeddingModel.builder()
        .baseUrl(embeddingBaseUrl)
        .modelName(embeddingModelName)
        .timeout(embeddingTimeout)
        .build();
  }

  // @Bean
  // public ChatModel chatLanguageModel() {
  //   return OllamaChatModel.builder()
  //       .baseUrl(chatBaseUrl)
  //       .modelName(chatModelName)
  //       .timeout(chatTimeout)
  //       .build();
  // }

  @Bean
  public ChatModel chatLanguageModel() {
    return OpenAiChatModel.builder()
        .apiKey(chatApiKey)
        .baseUrl(chatBaseUrl)
        .modelName(chatModelName)
        .timeout(chatTimeout)
        .build();
  }
}
