package com.uni_graph.retrieval.service.impl;

import com.uni_graph.common.domain.Course;
import com.uni_graph.retrieval.service.ChatService;
import com.uni_graph.retrieval.service.SearchService;
import dev.langchain4j.model.chat.ChatModel;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatServiceImpl implements ChatService {
  private final SearchService searchService;
  private final ChatModel chatModel;

  @Override
  public String chat(String message) {
    List<Course> contextCourses = searchService.hybridSearch(message);

    String prompt;
    if (contextCourses.isEmpty()) {
      prompt =
          String.format(
              "Bạn là trợ lý tư vấn học tập thông minh. Tôi không tìm thấy dữ liệu nào về môn học"
                  + " này trong cơ sở dữ liệu. Hãy trả lời rằng bạn không tìm thấy thông tin cụ thể"
                  + " trong hệ thống UniGraph và khuyến khích người dùng kiểm tra lại mã môn hoặc"
                  + " tên môn. Câu hỏi của người dùng: %s",
              message);
    } else {
      String context =
          contextCourses.stream()
              .map(
                  c -> {
                    StringBuilder sb = new StringBuilder();
                    sb.append(
                        String.format("### MÔN HỌC: %s (Mã: %s)\n", c.getTitleVn(), c.getCode()));
                    sb.append(
                        String.format(
                            "- Khoa: %s | Tín chỉ: %d LT, %d TH\n",
                            c.getDepartment() != null ? c.getDepartment().getName() : "N/A",
                            c.getTheoryCredits(),
                            c.getPracticeCredits()));
                    sb.append(
                        String.format(
                            "- Trạng thái: %s | Loại môn: %s\n", c.getStatus(), c.getCourseType()));

                    if (c.getSummary() != null && !c.getSummary().isBlank()) {
                      sb.append(String.format("- Tóm tắt: %s\n", c.getSummary()));
                    }

                    if (!c.getEquivalentCourses().isEmpty()) {
                      sb.append("- [QUAN HỆ] MÔN TƯƠNG ĐƯƠNG: ")
                          .append(
                              c.getEquivalentCourses().stream()
                                  .map(
                                      eq -> String.format("%s (%s)", eq.getTitleVn(), eq.getCode()))
                                  .collect(Collectors.joining(", ")))
                          .append("\n");
                    }

                    if (!c.getKnowledgePrerequisites().isEmpty()) {
                      sb.append("- [QUAN HỆ] KIẾN THỨC NỀN TẢNG: ")
                          .append(
                              c.getKnowledgePrerequisites().stream()
                                  .map(
                                      kp -> String.format("%s (%s)", kp.getTitleVn(), kp.getCode()))
                                  .collect(Collectors.joining(", ")))
                          .append("\n");
                    }

                    if (!c.getRequirementRules().isEmpty()) {
                      for (var rule : c.getRequirementRules()) {
                        String type =
                            rule.getRuleType().name().equals("PREREQUISITE")
                                ? "TIÊN QUYẾT"
                                : "HỌC TRƯỚC";
                        sb.append(String.format("- [QUAN HỆ] ĐIỀU KIỆN %s: ", type))
                            .append(
                                rule.getSatisfiedByCourses().stream()
                                    .map(
                                        rc ->
                                            String.format("%s (%s)", rc.getTitleVn(), rc.getCode()))
                                    .collect(Collectors.joining(", ")))
                            .append("\n");
                      }
                    }

                    return sb.toString().trim();
                  })
              .collect(Collectors.joining("\n\n---\n\n"));

      prompt =
          String.format(
              "Bạn là trợ lý ảo UniGraph, chuyên gia tư vấn lộ trình học tập dựa trên dữ liệu đồ"
                  + " thị môn học chính xác.\n"
                  + "Dưới đây là DỮ LIỆU THỰC TẾ từ hệ thống về các môn học liên quan đến câu"
                  + " hỏi:\n\n"
                  + "%s\n\n"
                  + "NHIỆM VỤ CỦA BẠN:\n"
                  + "1. Phân tích câu hỏi của sinh viên và tìm môn học mục tiêu trong DỮ LIỆU THỰC"
                  + " TẾ.\n"
                  + "2. Kiểm tra các mục [QUAN HỆ] của môn học mục tiêu đó để tìm câu trả lời (Ví"
                  + " dụ: Nếu hỏi 'môn gì trước SE356', hãy tìm 'SE356' và xem mục 'ĐIỀU KIỆN TIÊN"
                  + " QUYẾT').\n"
                  + "3. Nếu môn học mục tiêu không có quan hệ trực tiếp, hãy kiểm tra các môn TƯƠNG"
                  + " ĐƯƠNG của nó xem chúng có thông tin không.\n"
                  + "4. Trả lời một cách tự nhiên, chuyên nghiệp. Giải thích rõ ràng các điều kiện"
                  + " tiên quyết hoặc môn học trước nếu có.\n"
                  + "5. TUYỆT ĐỐI KHÔNG BỊA THÔNG TIN. Nếu dữ liệu trên không chứa thông tin cần"
                  + " thiết cho môn học cụ thể đó, hãy nói rõ là hệ thống chưa cập nhật dữ liệu"
                  + " quan hệ cho môn này.\n\n"
                  + "Câu hỏi của sinh viên: %s",
              context, message);
    }

    log.info("Sending prompt to LLM: \n{}", prompt);

    try {
      return chatModel.chat(prompt);
    } catch (Exception e) {
      log.error("Error generating response from LLM", e);
      return "Xin lỗi, dịch vụ tư vấn đang gặp sự cố. Vui lòng thử lại sau.";
    }
  }
}
