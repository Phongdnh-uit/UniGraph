package com.uni_graph.ingestion.domain;

import lombok.Data;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.util.List;

@Node("Student")
@Data
public class Student {
    @Id
    private String id;
    
    @Relationship(type = "RELATED")
    private List<Course> relatedCourses;
}