package com.uni_graph.common.domain;

import lombok.Data;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;

@Node("Group")
@Data
public class Group {
  @Id private String id;
}
