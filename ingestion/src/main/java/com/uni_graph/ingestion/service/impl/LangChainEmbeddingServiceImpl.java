package com.uni_graph.ingestion.service.impl;

import com.uni_graph.ingestion.domain.Course;
import com.uni_graph.ingestion.service.EmbeddingService;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.model.embedding.EmbeddingModel;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class LangChainEmbeddingServiceImpl implements EmbeddingService {

  private final EmbeddingModel embeddingModel;

  @Override
  public List<Double> embedText(String text) {
    if (text == null || text.trim().isEmpty()) {
      log.warn("Empty text provided for embedding, returning empty list.");
      return new ArrayList<>();
    }

    try {
      Embedding embedding = embeddingModel.embed(text).content();
      List<Double> doubleList = new ArrayList<>();
      for (float f : embedding.vector()) {
        doubleList.add((double) f);
      }
      return doubleList;
    } catch (Exception e) {
      log.error("Failed to generate embedding for text", e);
      // Trả về list rỗng hoặc ném exception tùy vào policy lỗi. Ở đây log và bỏ qua để ingestion
      // không
      // chết chùm.
      return new ArrayList<>();
    }
  }

  @Override
  public String buildTextToEmbed(Course course) {
    StringBuilder sb = new StringBuilder();
    sb.append(
        String.format(
            "Môn học: %s (%s). Mã môn: %s. ",
            course.getTitleVn(), course.getTitleEn(), course.getCode()));

    if (course.getDepartment() != null && course.getDepartment().getName() != null) {
      sb.append(String.format("Thuộc sự quản lý của: %s. ", course.getDepartment().getName()));
    }

    sb.append(
        String.format(
            "Loại môn: %s. Số tín chỉ: %d lý thuyết, %d thực hành. ",
            course.getCourseType(), course.getTheoryCredits(), course.getPracticeCredits()));

    if (course.getSummary() != null && !course.getSummary().trim().isEmpty()) {
      sb.append(String.format("Nội dung chính: %s", course.getSummary()));
    }

    return sb.toString().trim();
  }
}
