package com.uni_graph.common.domain;

import com.uni_graph.common.enums.LogicType;
import com.uni_graph.common.enums.RuleType;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

@Node("RequirementRule")
@Data
public class RequirementRule {
  @Id @GeneratedValue private Long id;

  private RuleType ruleType;
  private LogicType logicType;

  @Relationship(type = "SATISFIED_BY")
  private List<Course> satisfiedByCourses = new ArrayList<>();

  @Relationship(type = "SATISFIED_BY")
  private List<RequirementRule> satisfiedByRules = new ArrayList<>();
}
