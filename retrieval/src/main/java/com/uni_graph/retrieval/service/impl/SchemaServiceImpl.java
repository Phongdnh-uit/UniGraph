package com.uni_graph.retrieval.service.impl;

import com.uni_graph.retrieval.service.SchemaService;
import jakarta.annotation.PostConstruct;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.neo4j.core.Neo4jClient;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class SchemaServiceImpl implements SchemaService {

  private final Neo4jClient neo4jClient;
  private String cachedSchema = "";

  @PostConstruct
  public void init() {
    try {
      refreshSchema();
    } catch (Exception e) {
      log.error("Failed to initialize schema cache", e);
    }
  }

  @Override
  public String getFormattedSchema() {
    if (cachedSchema == null || cachedSchema.isBlank()) {
      return "Schema information is currently unavailable.";
    }
    return cachedSchema;
  }

  @Override
  public void refreshSchema() {
    log.info("Refreshing Neo4j schema cache...");
    StringBuilder sb = new StringBuilder();

    // 1. Fetch Nodes and Properties
    sb.append("Nodes:\n");
    try {
      Collection<Map<String, Object>> nodes =
          (Collection<Map<String, Object>>)
              neo4jClient.query("CALL db.schema.nodeTypeProperties()").fetch().all();

      Map<String, List<String>> nodeProperties =
          nodes.stream()
              .collect(
                  Collectors.groupingBy(
                      m -> (String) ((List<?>) m.get("nodeLabels")).get(0),
                      Collectors.mapping(
                          m -> (String) m.get("propertyName"), Collectors.toList())));

      nodeProperties.forEach(
          (label, props) -> {
            List<String> filteredProps =
                props.stream().filter(p -> !p.equals("embedding")).collect(Collectors.toList());
            sb.append("- ")
                .append(label)
                .append(" {")
                .append(String.join(", ", filteredProps))
                .append("}\n");
          });
    } catch (Exception e) {
      log.error("Error fetching node properties", e);
      sb.append("- Error fetching node properties\n");
    }

    // 2. Fetch Relationships
    sb.append("\nRelationships:\n");
    try {
      Collection<Map<String, Object>> rels =
          (Collection<Map<String, Object>>)
              neo4jClient.query("CALL db.schema.visualization()").fetch().all();

      // Note: db.schema.visualization() returns nodes and relationships.
      // For a simpler approach, we can use:
      Collection<Map<String, Object>> relTypes =
          (Collection<Map<String, Object>>)
              neo4jClient
                  .query(
                      "MATCH (n)-[r]->(m) RETURN DISTINCT labels(n) as start, type(r) as type,"
                          + " labels(m) as end LIMIT 100")
                  .fetch()
                  .all();

      relTypes.forEach(
          rel -> {
            List<?> start = (List<?>) rel.get("start");
            String type = (String) rel.get("type");
            List<?> end = (List<?>) rel.get("end");
            sb.append("- (:")
                .append(start.get(0))
                .append(")-[:")
                .append(type)
                .append("]->(:")
                .append(end.get(0))
                .append(")\n");
          });
    } catch (Exception e) {
      log.error("Error fetching relationship types", e);
      sb.append("- Error fetching relationship types\n");
    }

    this.cachedSchema = sb.toString();
    log.info("Schema cache refreshed successfully.");
  }
}
