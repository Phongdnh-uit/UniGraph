package com.uni_graph.retrieval.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.neo4j.core.Neo4jClient;

class SchemaServiceImplTest {

    private Neo4jClient neo4jClient;
    private SchemaServiceImpl schemaService;

    @BeforeEach
    void setUp() {
        neo4jClient = mock(Neo4jClient.class);
        // We haven't created the class yet, so this will fail compilation.
        // But for TDD, we write the test first.
        schemaService = new SchemaServiceImpl(neo4jClient);
    }

    @Test
    void getFormattedSchema_ShouldReturnFormattedString() {
        // Given
        Neo4jClient.UnboundRunnableSpec runnableSpec = mock(Neo4jClient.UnboundRunnableSpec.class);
        Neo4jClient.RecordFetchSpec fetchSpec = mock(Neo4jClient.RecordFetchSpec.class);

        when(neo4jClient.query(anyString())).thenReturn(runnableSpec);
        when(runnableSpec.fetch()).thenReturn(fetchSpec);
        
        // Mock node properties
        List<Map<String, Object>> mockNodes = List.of(
            Map.of("nodeLabels", List.of("Course"), "propertyName", "code"),
            Map.of("nodeLabels", List.of("Course"), "propertyName", "titleVn")
        );
        
        // Mock relationship types
        List<Map<String, Object>> mockRels = List.of(
            Map.of("start", List.of("Course"), "type", "REQUIRES", "end", List.of("RequirementRule"))
        );

        when(fetchSpec.all())
            .thenReturn(mockNodes) // First call for nodes
            .thenReturn(mockRels); // Second call for relationships

        // When
        schemaService.refreshSchema();
        String schema = schemaService.getFormattedSchema();

        // Then
        assertThat(schema).isNotEmpty();
        assertThat(schema).contains("Nodes:");
        assertThat(schema).contains("- Course {code, titleVn}");
        assertThat(schema).contains("Relationships:");
        assertThat(schema).contains("- (:Course)-[:REQUIRES]->(:RequirementRule)");
    }
}
