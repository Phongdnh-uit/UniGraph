package com.uni_graph.retrieval.domain;

import java.util.ArrayList;
import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

@Node("Course")
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Course {
  @Id @EqualsAndHashCode.Include private String code;

  private String titleVn;

  private String titleEn;

  private String summary;

  @Relationship(type = "KNOWLEDGE_PREREQUISITE")
  private List<Course> knowledgePrerequisites = new ArrayList<>();
}
