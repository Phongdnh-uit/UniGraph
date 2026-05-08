package com.uni_graph.ingestion.domain;

import java.util.List;
import lombok.Data;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

@Node("Section")
@Data
public class Section {
  @Id private String id;

  @Relationship(type = "SCHEDULED")
  private List<TimeSlot> timeSlots;

  @Relationship(type = "IN")
  private Semester semester;

  @Relationship(type = "HELD_IN")
  private Classroom classroom;
}
