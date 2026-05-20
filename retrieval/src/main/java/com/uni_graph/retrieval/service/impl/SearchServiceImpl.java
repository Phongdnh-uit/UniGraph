package com.uni_graph.retrieval.service.impl;

import com.uni_graph.retrieval.domain.Course;
import com.uni_graph.retrieval.repository.CourseRepository;
import com.uni_graph.retrieval.service.CypherGenerator;
import com.uni_graph.retrieval.service.SearchService;
import dev.langchain4j.model.embedding.EmbeddingModel;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.neo4j.core.Neo4jClient;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class SearchServiceImpl implements SearchService {
  private final CourseRepository courseRepository;
  private final EmbeddingModel embeddingModel;
  private final CypherGenerator cypherGenerator;
  private final Neo4jClient neo4jClient;

  @Override
  public List<Course> hybridSearch(String query) {
    // 1. Try Cypher Search first (Reasoning Chain)
    try {
      String cypher = cypherGenerator.generate(query);
      if (cypher != null && !cypher.isBlank()) {
        log.info("Generated Cypher: {}", cypher);
        Collection<Course> cypherResults = neo4jClient.query(cypher).fetchAs(Course.class).all();
        if (!cypherResults.isEmpty()) {
          log.info("Cypher search found {} results", cypherResults.size());
          return new ArrayList<>(cypherResults);
        }
      }
    } catch (Exception e) {
      log.error("Cypher search failed, falling back to vector/keyword", e);
    }

    // 2. Fallback to Vector Search
    List<Course> vectorResults = new ArrayList<>();
    try {
      var embeddingContent = embeddingModel.embed(query).content();
      List<Double> vector = new ArrayList<>();
      for (float f : embeddingContent.vector()) vector.add((double) f);
      vectorResults = courseRepository.searchByVector(vector, 5);
    } catch (Exception e) {
      // Log error or handle gracefully - here we fallback to empty vector results
    }

    // 2. Keyword Search
    List<Course> keywordResults = courseRepository.searchByKeyword(query);

    // 3. Simple merge (Avoid duplicates)
    List<Course> results = new ArrayList<>(vectorResults);
    for (Course c : keywordResults) {
      if (results.stream().noneMatch(r -> r.getCode().equals(c.getCode()))) {
        results.add(c);
      }
    }
    return results;
  }
}
