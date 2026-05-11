package com.uni_graph.ingestion.domain;

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
  private String status;
  private String courseType;
  private String oldCode;
  private Integer theoryCredits;
  private Integer practiceCredits;
  private String summary;

  private List<Double> embedding;

  @Relationship(type = "BELONG_TO")
  private Department department;

  @Relationship(type = "OFFERED_AS")
  private List<Section> sections = new ArrayList<>();

  @Relationship(type = "EQUIVALENT_TO")
  private List<Course> equivalentCourses = new ArrayList<>();

  @Relationship(type = "PART_OF")
  private List<Group> groups = new ArrayList<>();

  @Relationship(type = "REQUIRES")
  private List<RequirementRule> requirementRules = new ArrayList<>();
}
