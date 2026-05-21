package com.uni_graph.retrieval.service;

import com.uni_graph.common.domain.Course;
import java.util.List;

public interface SearchService {
  List<Course> hybridSearch(String query);
}
