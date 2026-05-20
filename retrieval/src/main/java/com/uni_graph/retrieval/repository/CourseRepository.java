package com.uni_graph.retrieval.repository;

import com.uni_graph.retrieval.domain.Course;
import java.util.List;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;

public interface CourseRepository extends Neo4jRepository<Course, String> {

  @Query(
      "MATCH (c:Course) WHERE toLower(c.titleVn) CONTAINS toLower($query) OR toLower(c.titleEn) CONTAINS toLower($query) "
          + "RETURN c LIMIT 10")
  List<Course> searchByKeyword(String query);

  @Query(
      "CALL db.index.vector.queryNodes('course_embeddings', $topK, $embedding) "
          + "YIELD node, score "
          + "RETURN node")
  List<Course> searchByVector(List<Double> embedding, int topK);
}
