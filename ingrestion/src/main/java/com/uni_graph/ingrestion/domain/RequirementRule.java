package com.uni_graph.ingrestion.domain;

import lombok.Data;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.util.ArrayList;
import java.util.List;

@Node("RequirementRule")
@Data
public class RequirementRule {
    @Id
    @GeneratedValue
    private Long id;

    private String type; // "PREREQUISITE", "PREVIOUS"

    @Relationship(type = "SATISFIED_BY")
    private List<Course> satisfiedByCourses = new ArrayList<>();

    @Relationship(type = "COMPOSED_OF")
    private List<ComposedOf> subRules = new ArrayList<>();
}
