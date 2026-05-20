package com.uni_graph.retrieval.domain;

import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

@Node("Course")
@Data
public class Course {
  @Id private String code;

  private String titleVn;

  private String titleEn;

  private String summary;

  @Relationship(type = "KNOWLEDGE_PREREQUISITE")
  private List<Course> knowledgePrerequisites = new ArrayList<>();
}
