package com.uni_graph.retrieval.service.impl;

import com.uni_graph.retrieval.service.CypherGenerator;
import dev.langchain4j.model.chat.ChatLanguageModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class CypherGeneratorImpl implements CypherGenerator {

  private final ChatLanguageModel chatLanguageModel;

  private static final String SYSTEM_PROMPT =
      """
      You are an expert Neo4j Cypher query generator.
      Given a user question, generate a Cypher query to answer it.

      Schema:
      - Nodes:
        - Course (code, titleVn, titleEn, summary, status, courseType, theoryCredits, practiceCredits)
        - Teacher (id, email, name)
        - Department (name)
        - Section (section_id)
        - Semester (name)
      - Relationships:
        - (Course)-[:BELONG_TO]->(Department)
        - (Teacher)-[:BELONG_TO]->(Department)
        - (Teacher)-[:TEACHES]->(Section)
        - (Course)-[:OFFERED_AS]->(Section)
        - (Course)-[:KNOWLEDGE_PREREQUISITE]->(Course)
        - (Course)-[:EQUIVALENT_TO]->(Course)

      Instructions:
      1. Return ONLY the Cypher query. No preamble, no explanation, no backticks.
      2. Use case-insensitive matching for string properties if appropriate (e.g., using toLower()).
      3. CRITICAL: If the user asks about properties or relationships of a specific course (e.g., "Môn MAT23 có môn tương đương không?"), you MUST return the Course node with that code.
      4. DO NOT traverse the relationship in Cypher if the goal is to find related courses of a specific course. Just return the main course.
      5. Correct: MATCH (c:Course {code: 'MAT23'}) RETURN c
      6. Incorrect: MATCH (c1:Course {code: 'MAT23'})-[:EQUIVALENT_TO]->(c2:Course) RETURN c2
      7. If the question cannot be answered by Cypher, return an empty string.

      Question: %s
      """;

  @Override
  public String generate(String question) {
    String prompt = String.format(SYSTEM_PROMPT, question);
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
