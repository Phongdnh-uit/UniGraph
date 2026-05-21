package com.uni_graph.ingestion.repository;

import com.uni_graph.common.domain.Course;
import java.util.List;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CourseRepository extends Neo4jRepository<Course, String> {
  @Query("MATCH (c:Course) RETURN c.code")
  List<String> findAllCodes();
}
