package com.uni_graph.retrieval.service.impl;

import com.uni_graph.common.domain.Course;
import com.uni_graph.retrieval.service.ChatService;
import com.uni_graph.retrieval.service.SearchService;
import dev.langchain4j.model.chat.ChatLanguageModel;
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
  private final ChatLanguageModel chatModel;

  @Override
  public String chat(String message) {
    List<Course> contextCourses = searchService.hybridSearch(message);

    String prompt;
    if (contextCourses.isEmpty()) {
      prompt =
          String.format(
              "Bạn là trợ lý tư vấn học tập thông minh. Tôi không tìm thấy dữ liệu nào về môn học này trong cơ sở dữ liệu. "
                  + "Hãy trả lời rằng bạn không tìm thấy thông tin cụ thể trong hệ thống UniGraph và khuyến khích người dùng kiểm tra lại mã môn hoặc tên môn. "
                  + "Câu hỏi của người dùng: %s",
              message);
    } else {
      String context =
          contextCourses.stream()
              .map(
                  c -> {
                    StringBuilder sb = new StringBuilder();
                    sb.append(String.format("THÔNG TIN MÔN %s (%s):\n", c.getTitleVn(), c.getCode()));
                    sb.append(String.format("- Khoa: %s\n", c.getDepartment() != null ? c.getDepartment().getName() : "N/A"));
                    sb.append(String.format("- Tín chỉ: %d Lý thuyết, %d Thực hành\n", c.getTheoryCredits(), c.getPracticeCredits()));
                    sb.append(String.format("- Trạng thái: %s\n", c.getStatus()));
                    sb.append(String.format("- Loại môn: %s\n", c.getCourseType()));
                    sb.append(String.format("- Tóm tắt: %s\n", c.getSummary() != null ? c.getSummary() : "Không có tóm tắt."));

                    if (!c.getEquivalentCourses().isEmpty()) {
                      sb.append("- CÁC MÔN TƯƠNG ĐƯƠNG CỦA MÔN NÀY: ")
                          .append(c.getEquivalentCourses().stream()
                              .map(eq -> eq.getTitleVn() + " (" + eq.getCode() + ")")
                              .collect(Collectors.joining(", ")))
                          .append("\n");
                    }

                    if (!c.getKnowledgePrerequisites().isEmpty()) {
                      sb.append("- KIẾN THỨC NỀN TẢNG CẦN CÓ: ")
                          .append(c.getKnowledgePrerequisites().stream()
                              .map(kp -> kp.getTitleVn() + " (" + kp.getCode() + ")")
                              .collect(Collectors.joining(", ")))
                          .append("\n");
                    }

                    if (!c.getRequirementRules().isEmpty()) {
                      for (var rule : c.getRequirementRules()) {
                        String type = rule.getRuleType().name().equals("PREREQUISITE") ? "TIÊN QUYẾT" : "HỌC TRƯỚC";
                        sb.append(String.format("- MÔN %s: ", type))
                            .append(rule.getSatisfiedByCourses().stream()
                                .map(rc -> rc.getTitleVn() + " (" + rc.getCode() + ")")
                                .collect(Collectors.joining(", ")))
                            .append("\n");
                      }
                    }

                    return sb.toString().trim();
                  })
              .collect(Collectors.joining("\n\n---\n\n"));

      prompt =
          String.format(
              "Bạn là trợ lý ảo thông minh của hệ thống UniGraph, chuyên hỗ trợ sinh viên về thông tin đào tạo.\n"
                  + "Dưới đây là dữ liệu thực tế từ cơ sở dữ liệu:\n\n%s\n\n"
                  + "CÁCH TRẢ LỜI:\n"
                  + "1. Hãy trả lời một cách tự nhiên như một người tư vấn thật thụ. Tránh liệt kê khô khan trừ khi cần thiết.\n"
                  + "2. Luôn ưu tiên thông tin trong dữ liệu trên. Nếu người dùng hỏi về môn A tương đương môn nào, hãy kiểm tra kỹ các mục 'CÁC MÔN TƯƠNG ĐƯƠNG' của tất cả các môn có trong dữ liệu.\n"
                  + "3. Nếu dữ liệu cho thấy quan hệ giữa các môn (như tương đương, tiên quyết), hãy giải thích rõ mối quan hệ đó cho người dùng.\n"
                  + "4. Tuyệt đối không bịa đặt thông tin không có trong dữ liệu.\n"
                  + "5. Nếu không tìm thấy thông tin cụ thể, hãy trả lời một cách khéo léo và gợi ý người dùng cung cấp thêm chi tiết (như mã môn cụ thể).\n\n"
                  + "Câu hỏi của người dùng: %s",
              context, message);
    }

    log.info("Sending prompt to LLM: \n{}", prompt);

    try {
      return chatModel.generate(prompt);
    } catch (Exception e) {
      log.error("Error generating response from LLM", e);
      return "Xin lỗi, dịch vụ tư vấn đang gặp sự cố. Vui lòng thử lại sau.";
    }
  }
}
