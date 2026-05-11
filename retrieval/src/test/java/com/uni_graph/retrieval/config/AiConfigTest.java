package com.uni_graph.retrieval.config;

import static org.assertj.core.api.Assertions.assertThat;

import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
public class AiConfigTest {

    @Autowired
    private ApplicationContext context;

    @Autowired
    private EmbeddingModel embeddingModel;

    @Autowired
    private ChatLanguageModel chatLanguageModel;

    @Test
    void beansAreLoaded() {
        assertThat(context.containsBean("embeddingModel")).isTrue();
        assertThat(context.containsBean("chatLanguageModel")).isTrue();
        assertThat(embeddingModel).isNotNull();
        assertThat(chatLanguageModel).isNotNull();
    }
}
