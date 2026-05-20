package com.uni_graph.common.domain;

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

  @Relationship(type = "KNOWLEDGE_PREREQUISITE")
  private List<Course> knowledgePrerequisites = new ArrayList<>();
}
