package com.uni_graph.retrieval.service.impl;

import com.uni_graph.common.domain.Course;
import com.uni_graph.common.util.PerformanceProfiler;
import com.uni_graph.retrieval.repository.CourseRepository;
import com.uni_graph.retrieval.service.CypherGenerator;
import com.uni_graph.retrieval.service.SearchService;
import dev.langchain4j.model.embedding.EmbeddingModel;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.neo4j.core.Neo4jTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class SearchServiceImpl implements SearchService {
  private final CourseRepository courseRepository;
  private final EmbeddingModel embeddingModel;
  private final CypherGenerator cypherGenerator;
  private final Neo4jTemplate neo4jTemplate;

  @Override
  public List<Course> hybridSearch(String query) {
    PerformanceProfiler profiler = new PerformanceProfiler("Hybrid Search: " + query, log);

    // 1. Try Cypher Search first (Reasoning Chain)
    try {
      profiler.start("1. Cypher Generation");
      String cypher = cypherGenerator.generate(query);
      if (cypher != null && !cypher.isBlank()) {
        log.info("Generated Cypher: {}", cypher);

        profiler.start("2. Neo4j Cypher Execution");
        List<Course> cypherResults = neo4jTemplate.findAll(cypher, Map.of(), Course.class);

        if (!cypherResults.isEmpty()) {
          profiler.start("3. Result Hydration");
          log.info("Cypher search found {} results", cypherResults.size());
          List<Course> hydratedResults = new ArrayList<>();
          for (Course c : cypherResults) {
            courseRepository
                .findById(c.getCode())
                .ifPresent(
                    course -> {
                      hydratedResults.add(course);
                      // Lấy thêm các môn học có quan hệ ngược lại (để biết c là môn tương đương của
                      // môn nào)
                      String reverseCypher =
                          String.format(
                              "MATCH (src:Course)-[:EQUIVALENT_TO|KNOWLEDGE_PREREQUISITE|REQUIRES]->(target:Course {code: '%s'}) "
                                  + "RETURN src",
                              course.getCode());
                      List<Course> sources =
                          neo4jTemplate.findAll(reverseCypher, Map.of(), Course.class);
                      for (Course s : sources) {
                        courseRepository.findById(s.getCode()).ifPresent(hydratedResults::add);
                      }
                    });
          }
          // Loại bỏ trùng lặp nếu có
          List<Course> finalResults =
              hydratedResults.stream().filter(distinctByKey(Course::getCode)).toList();

          profiler.logSummary();
          return finalResults;
        }
      }
    } catch (Exception e) {
      log.error("Cypher search failed, falling back to vector/keyword", e);
    }

    profiler.start("4. Fallback Search (Vector/Keyword)");
    List<Course> results = fallbackSearch(query);
    profiler.logSummary();
    return results;
  }

  private List<Course> fallbackSearch(String query) {
    // 2. Fallback to Vector Search
    List<Course> vectorResults = new ArrayList<>();
    try {
      var embeddingContent = embeddingModel.embed(query).content();
      List<Double> vector = new ArrayList<>();
      for (float f : embeddingContent.vector()) vector.add((double) f);
      vectorResults = courseRepository.searchByVector(vector, 5);
    } catch (Exception e) {
      log.error("Vector search failed", e);
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

  public static <T> java.util.function.Predicate<T> distinctByKey(
      java.util.function.Function<? super T, ?> keyExtractor) {
    Map<Object, Boolean> seen = new java.util.concurrent.ConcurrentHashMap<>();
    return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
  }
}
