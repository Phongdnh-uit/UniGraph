package com.uni_graph.ingestion.repository;

import com.uni_graph.ingestion.domain.RequirementRule;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RequirementRuleRepository extends Neo4jRepository<RequirementRule, Long> {}
