package com.uni_graph.ingestion.repository;

import com.uni_graph.ingestion.domain.Department;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DepartmentRepository extends Neo4jRepository<Department, String> {
}
