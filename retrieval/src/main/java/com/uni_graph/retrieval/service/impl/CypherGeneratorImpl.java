package com.uni_graph.retrieval.service.impl;

import com.uni_graph.retrieval.service.CypherGenerator;
import com.uni_graph.retrieval.service.SchemaService;
import dev.langchain4j.model.chat.ChatModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class CypherGeneratorImpl implements CypherGenerator {

  private final ChatModel chatLanguageModel;
  private final SchemaService schemaService;

  private static final String SYSTEM_PROMPT =
      """
      You are an expert Neo4j Cypher query generator for a university course system.
      Given a user question, generate a Cypher query that returns the relevant Course nodes.

      Schema:
      %s

      CRITICAL RULES:
      1. Return ONLY the Cypher query. No preamble, no explanation, no backticks.
      2. ALWAYS return the full Course nodes (e.g., RETURN c, p), NOT just properties like c.code.
      3. For questions about prerequisites, equivalents, or relationships, you MUST return BOTH the starting course node AND the related course nodes.
         Example: MATCH (c:Course {code: 'SE356'})-[:REQUIRES]->(r:RequirementRule)-[:SATISFIED_BY]->(p:Course) RETURN c, p
      4. Use toLower() for case-insensitive matching on string properties if needed.
      5. If the question cannot be answered via Cypher, return an empty string.

      Question: %s
      """;

  @Override
  public String generate(String question) {
    String schema = schemaService.getFormattedSchema();
    String prompt = String.format(SYSTEM_PROMPT, schema, question);
    try {
      String response = chatLanguageModel.chat(prompt).trim();
      // Remove potential markdown formatting
      if (response.startsWith("```cypher")) {
        response = response.substring(9);
      }
      if (response.startsWith("```")) {
        response = response.substring(3);
      }
      if (response.endsWith("```")) {
        response = response.substring(0, response.length() - 3);
      }
      return response.trim();
    } catch (Exception e) {
      log.error("Error generating Cypher for question: {}", question, e);
      return "";
    }
  }
}
