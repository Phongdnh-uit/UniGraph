package com.uni_graph.ingestion.domain;

import lombok.Data;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;

@Node("Semester")
@Data
public class Semester {
  @Id private String id;
}
