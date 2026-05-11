package com.uni_graph.ingestion.service;

import com.uni_graph.ingestion.domain.Course;
import java.util.List;

public interface EmbeddingService {
  /**
   * Tạo vector embedding từ văn bản.
   *
   * @param text Văn bản cần nhúng
   * @return Danh sách các giá trị vector (độ dài phụ thuộc vào model)
   */
  List<Double> embedText(String text);

  /**
   * Tạo văn bản giàu ngữ nghĩa (Enriched Text) từ thông tin môn học.
   *
   * @param course Môn học cần tạo văn bản
   * @return Văn bản hoàn chỉnh để đưa vào mô hình AI
   */
  String buildTextToEmbed(Course course);
}
