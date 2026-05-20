package com.uni_graph.retrieval.controllers;

import com.uni_graph.retrieval.domain.Course;
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
  public List<Course> search(@RequestParam String q) {
    return searchService.hybridSearch(q);
  }

  @PostMapping("/chat")
  public String chat(@RequestBody String message) {
    return chatService.chat(message);
  }
}
