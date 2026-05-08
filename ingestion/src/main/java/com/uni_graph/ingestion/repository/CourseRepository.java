package com.uni_graph.ingestion.repository;

import com.uni_graph.ingestion.domain.Course;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CourseRepository extends Neo4jRepository<Course, String> {}
