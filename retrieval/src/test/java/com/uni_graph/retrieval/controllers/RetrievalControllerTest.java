package com.uni_graph.retrieval.controllers;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.uni_graph.retrieval.domain.Course;
import com.uni_graph.retrieval.service.ChatService;
import com.uni_graph.retrieval.service.SearchService;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(RetrievalController.class)
class RetrievalControllerTest {

  @Autowired private MockMvc mockMvc;

  @MockitoBean private SearchService searchService;

  @MockitoBean private ChatService chatService;

  @Test
  void search() throws Exception {
    Course course = new Course();
    course.setCode("C1");
    course.setTitleVn("Math");

    when(searchService.hybridSearch("Math")).thenReturn(List.of(course));

    mockMvc
        .perform(get("/api/v1/search").param("q", "Math"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].code").value("C1"))
        .andExpect(jsonPath("$[0].titleVn").value("Math"));
  }

  @Test
  void chat() throws Exception {
    when(chatService.chat("Hello")).thenReturn("Hi there");

    mockMvc
        .perform(post("/api/v1/chat").contentType(MediaType.TEXT_PLAIN).content("Hello"))
        .andExpect(status().isOk())
        .andExpect(content().string("Hi there"));
  }
}
