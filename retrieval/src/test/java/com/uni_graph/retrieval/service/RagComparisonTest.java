package com.uni_graph.retrieval.service;

import com.uni_graph.retrieval.domain.Course;
import com.uni_graph.retrieval.repository.CourseRepository;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class RagComparisonTest {

  private static final Logger log = LoggerFactory.getLogger(RagComparisonTest.class);

  @Autowired private ChatService chatService;

  @Autowired private CourseRepository courseRepository;

  @Autowired private EmbeddingModel embeddingModel;

  @Autowired private ChatLanguageModel chatModel;

  @Test
  void compareRagMethods() {
    List<String> questions =
        List.of(
            "Danh sách các môn học tiên quyết của môn Trí tuệ nhân tạo?",
            "Những giảng viên nào thuộc khoa Công nghệ thông tin và dạy môn gì?",
            "Môn học nào có liên quan đến AI và thuộc khoa nào?",
            "Môn học nào là tương đương với môn Cơ sở dữ liệu?");

    for (String question : questions) {
      log.info("==================================================");
      log.info("QUESTION: {}", question);

      // 1. GraphRAG (Default ChatService)
      String graphRagResponse;
      try {
        graphRagResponse = chatService.chat(question);
      } catch (Exception e) {
        graphRagResponse = "GraphRAG failed: " + e.getMessage();
      }
      log.info("--------------------------------------------------");
      log.info("GRAPHRAG RESPONSE:\n{}", graphRagResponse);

      // 2. Naive RAG (Vector + Keyword)
      String naiveRagResponse;
      try {
        naiveRagResponse = simulateNaiveRag(question);
      } catch (Exception e) {
        naiveRagResponse = "Naive RAG simulation failed: " + e.getMessage();
      }
      log.info("--------------------------------------------------");
      log.info("NAIVE RAG RESPONSE:\n{}", naiveRagResponse);
      log.info("==================================================\n");
    }
  }

  private String simulateNaiveRag(String message) {
    // Simulate what SearchServiceImpl does without Cypher
    List<Course> vectorResults = new ArrayList<>();
    try {
      var embeddingContent = embeddingModel.embed(message).content();
      List<Double> vector = new ArrayList<>();
      for (float f : embeddingContent.vector()) vector.add((double) f);
      vectorResults = courseRepository.searchByVector(vector, 5);
    } catch (Exception e) {
      log.warn("Vector search failed in simulation, using empty results: {}", e.getMessage());
    }

    List<Course> keywordResults = courseRepository.searchByKeyword(message);

    List<Course> contextCourses = new ArrayList<>(vectorResults);
    for (Course c : keywordResults) {
      if (contextCourses.stream().noneMatch(r -> r.getCode().equals(c.getCode()))) {
        contextCourses.add(c);
      }
    }

    // Simulate ChatServiceImpl prompt construction
    String prompt;
    if (contextCourses.isEmpty()) {
      prompt =
          String.format(
              "Bạn là trợ lý học tập. Tôi không tìm thấy thông tin nào về các môn học liên quan đến: %s. "
                  + "Hãy trả lời người dùng rằng bạn không tìm thấy thông tin và có thể gợi ý họ hỏi về các môn học khác.",
              message);
    } else {
      String context =
          contextCourses.stream()
              .map(
                  c ->
                      String.format("Môn %s (%s): %s", c.getTitleVn(), c.getCode(), c.getSummary()))
              .collect(Collectors.joining("\n"));

      prompt =
          String.format(
              "Bạn là trợ lý học tập. Dựa vào thông tin các môn học sau đây:\n%s\n\nHãy trả lời câu hỏi: %s",
              context, message);
    }

    return chatModel.generate(prompt);
  }
}
