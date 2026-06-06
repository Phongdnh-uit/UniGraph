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
import org.neo4j.driver.types.Node;
import org.springframework.data.neo4j.core.Neo4jClient;
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
  private final Neo4jClient neo4jClient;

  @Override
  public List<Course> hybridSearch(String query) {
    PerformanceProfiler profiler = new PerformanceProfiler("Hybrid Search: " + query, log);

    // 1. Try Cypher Search first (Reasoning Chain)
    try {
      profiler.start("1. Cypher Generation");
      String cypher = cypherGenerator.generate(query);

      // Heuristic: Extract course codes from query to ensure subject is hydrated
      List<String> codesToHydrate = new ArrayList<>();
      java.util.regex.Matcher matcher =
          java.util.regex.Pattern.compile("[A-Z]{2,4}[0-9]{3,4}").matcher(query.toUpperCase());
      while (matcher.find()) {
        codesToHydrate.add(matcher.group());
      }

      if (cypher != null && !cypher.isBlank()) {
        log.info("Generated Cypher: {}", cypher);

        profiler.start("2. Neo4j Cypher Execution");
        // Use a more flexible approach to capture results (nodes or just codes)
        neo4jClient
            .query(cypher)
            .fetch()
            .all()
            .forEach(
                row -> {
                  row.values()
                      .forEach(
                          val -> {
                            if (val instanceof String s) {
                              codesToHydrate.add(s);
                            } else if (val instanceof Node node) {
                              if (node.hasLabel("Course")) {
                                codesToHydrate.add(node.get("code").asString());
                              }
                            } else if (val instanceof Map<?, ?> map && map.containsKey("code")) {
                              Object codeVal = map.get("code");
                              if (codeVal != null) {
                                codesToHydrate.add(codeVal.toString());
                              }
                            }
                          });
                });
      }

      if (!codesToHydrate.isEmpty()) {
        profiler.start("3. Result Hydration");
        List<String> uniqueCodes =
            codesToHydrate.stream()
                .distinct()
                .limit(15) // Limit initial subjects to prevent explosion
                .toList();
        log.info("Hydrating {} codes", uniqueCodes.size());

        List<Course> hydratedResults = new ArrayList<>();
        for (String code : uniqueCodes) {
          courseRepository
              .findById(code)
              .ifPresent(
                  course -> {
                    hydratedResults.add(course);

                    // Enrich context by finding related courses (both directions)
                    String enrichmentCypher =
                        String.format(
                            "MATCH (c:Course {code: '%s'}) "
                                + "OPTIONAL MATCH (c)-[:EQUIVALENT_TO|KNOWLEDGE_PREREQUISITE]->(r1:Course) "
                                + "OPTIONAL MATCH (r2:Course)-[:EQUIVALENT_TO|KNOWLEDGE_PREREQUISITE]->(c) "
                                + "OPTIONAL MATCH (c)-[:REQUIRES]->(:RequirementRule)-[:SATISFIED_BY]->(r3:Course) "
                                + "OPTIONAL MATCH (r4:Course)-[:REQUIRES]->(:RequirementRule)-[:SATISFIED_BY]->(c) "
                                + "RETURN DISTINCT collect(r1.code) + collect(r2.code) + collect(r3.code) + collect(r4.code) as related",
                            course.getCode());

                    neo4jClient
                        .query(enrichmentCypher)
                        .fetch()
                        .one()
                        .ifPresent(
                            row -> {
                              List<String> relatedCodes = (List<String>) row.get("related");
                              if (relatedCodes != null) {
                                relatedCodes.stream()
                                    .filter(java.util.Objects::nonNull)
                                    .distinct()
                                    .limit(5) // Limit neighbors per course
                                    .forEach(
                                        rc ->
                                            courseRepository
                                                .findById(rc)
                                                .ifPresent(hydratedResults::add));
                              }
                            });
                  });

          if (hydratedResults.size() > 25) break; // Hard limit for entire context
        }

        // Remove duplicates
        List<Course> finalResults =
            hydratedResults.stream().filter(distinctByKey(Course::getCode)).toList();

        profiler.logSummary();
        return finalResults;
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
