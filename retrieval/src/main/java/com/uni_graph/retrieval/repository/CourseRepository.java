package com.uni_graph.retrieval.repository;

import com.uni_graph.common.domain.Course;
import java.util.List;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;

public interface CourseRepository extends Neo4jRepository<Course, String> {

  @Query(
      "MATCH (c:Course) WHERE toLower(c.titleVn) CONTAINS toLower($query) OR toLower(c.titleEn)"
          + " CONTAINS toLower($query) RETURN c LIMIT 10")
  List<Course> searchByKeyword(@Param("query") String query);

  @Query(
      "CALL db.index.vector.queryNodes('course_embeddings', $topK, $embedding) "
          + "YIELD node, score "
          + "RETURN node")
  List<Course> searchByVector(@Param("embedding") List<Double> embedding, @Param("topK") int topK);
}
