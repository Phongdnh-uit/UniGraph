package com.uni_graph.retrieval.repository;

import com.uni_graph.retrieval.domain.Course;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import java.util.List;

public interface CourseRepository extends Neo4jRepository<Course, String> {
    
    @Query("MATCH (c:Course) WHERE c.title_vn CONTAINS $query OR c.title_en CONTAINS $query RETURN c")
    List<Course> searchByKeyword(String query);

    @Query("CALL db.index.vector.queryNodes('course_embeddings', $topK, $embedding) " +
           "YIELD node, score " +
           "RETURN node")
    List<Course> searchByVector(List<Double> embedding, int topK);
}
