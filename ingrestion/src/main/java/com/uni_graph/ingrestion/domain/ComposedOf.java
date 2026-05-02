package com.uni_graph.ingrestion.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.neo4j.core.schema.RelationshipId;
import org.springframework.data.neo4j.core.schema.RelationshipProperties;
import org.springframework.data.neo4j.core.schema.TargetNode;

@RelationshipProperties
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ComposedOf {
    @RelationshipId
    private Long id;

    @TargetNode
    private RequirementRule subRule;

    private String logic; // "AND", "OR"
}
