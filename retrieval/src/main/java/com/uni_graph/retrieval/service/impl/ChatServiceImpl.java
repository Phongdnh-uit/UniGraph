package com.uni_graph.retrieval.service.impl;

import com.uni_graph.retrieval.domain.Course;
import com.uni_graph.retrieval.service.ChatService;
import com.uni_graph.retrieval.service.SearchService;
import dev.langchain4j.model.chat.ChatLanguageModel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {
    private final SearchService searchService;
    private final ChatLanguageModel chatModel;

    @Override
    public String chat(String message) {
        List<Course> contextCourses = searchService.hybridSearch(message);
        
        String context = contextCourses.stream()
            .map(c -> String.format("Môn %s (%s): %s", c.getTitleVn(), c.getCode(), c.getSummary()))
            .collect(Collectors.joining("\n"));

        String prompt = String.format(
            "Bạn là trợ lý học tập. Dựa vào thông tin các môn học sau đây:\n%s\n\nHãy trả lời câu hỏi: %s",
            context, message
        );

        return chatModel.generate(prompt);
    }
}
