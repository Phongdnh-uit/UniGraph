package com.uni_graph.ingestion.domain;

import lombok.Data;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;

@Node("TimeSlot")
@Data
public class TimeSlot {
    @Id
    private String id;
}