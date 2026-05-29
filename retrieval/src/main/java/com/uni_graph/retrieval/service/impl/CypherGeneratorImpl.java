package com.uni_graph.retrieval.service.impl;

import com.uni_graph.retrieval.service.CypherGenerator;
import com.uni_graph.retrieval.service.SchemaService;
import dev.langchain4j.model.chat.ChatLanguageModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class CypherGeneratorImpl implements CypherGenerator {

  private final ChatLanguageModel chatLanguageModel;
  private final SchemaService schemaService;

  private static final String SYSTEM_PROMPT =
      """
      You are an expert Neo4j Cypher query generator.
      Given a user question, generate a Cypher query to answer it.

      Schema:
      %s

      Instructions:
      1. Return ONLY the Cypher query. No preamble, no explanation, no backticks.
      2. Use case-insensitive matching for string properties if appropriate (e.g., using toLower()).
      3. CRITICAL: For prerequisite or previous course questions, you MUST traverse through RequirementRule.
         Example for "Prerequisites of EN001":
         MATCH (c:Course {code: 'EN001'})-[:REQUIRES]->(r:RequirementRule {ruleType: 'PREREQUISITE'})-[:SATISFIED_BY]->(p:Course) RETURN p
      4. If the user asks about properties or relationships of a specific course (e.g., "Môn MAT23 có môn tương đương không?"), you MUST return the Course node with that code to allow the system to hydrate all its relationships.
         Correct: MATCH (c:Course {code: 'MAT23'}) RETURN c
      5. If the question cannot be answered by Cypher, return an empty string.

      Question: %s
      """;

  @Override
  public String generate(String question) {
    String schema = schemaService.getFormattedSchema();
    String prompt = String.format(SYSTEM_PROMPT, schema, question);
    try {
      String response = chatLanguageModel.generate(prompt).trim();
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
