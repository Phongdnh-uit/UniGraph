package com.uni_graph.ingrestion.repository;

import com.uni_graph.ingrestion.domain.RequirementRule;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RequirementRuleRepository extends Neo4jRepository<RequirementRule, Long> {
}
