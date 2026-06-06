package com.uni_graph.retrieval.controllers;

import com.uni_graph.common.domain.Course;
import com.uni_graph.common.dto.ApiResponse;
import com.uni_graph.retrieval.dto.ChatRequest;
import com.uni_graph.retrieval.service.ChatService;
import com.uni_graph.retrieval.service.SearchService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class RetrievalController {
  private final SearchService searchService;
  private final ChatService chatService;

  @GetMapping("/search")
  public ApiResponse<List<Course>> search(@RequestParam("query") String q) {
    return ApiResponse.<List<Course>>builder().data(searchService.hybridSearch(q)).build();
  }

  @PostMapping("/chat")
  public ApiResponse<String> chat(@RequestBody ChatRequest request) {
    return ApiResponse.<String>builder().data(chatService.chat(request.getMessage())).build();
  }
}
