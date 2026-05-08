package com.uni_graph.ingestion.domain;

import java.util.List;
import lombok.Data;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

@Node("Teacher")
@Data
public class Teacher {
  @Id private String id;

  @Relationship(type = "TEACHES")
  private List<Section> sections;

  @Relationship(type = "BELONG_TO")
  private Department department;
}
