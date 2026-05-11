package com.uni_graph.retrieval.domain;

import lombok.Data;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;

@Node("Course")
@Data
public class Course {
  @Id private String code;

  private String titleVn;

  private String titleEn;

  private String summary;
}
