package com.uni_graph.retrieval.domain;

import lombok.Data;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Property;

@Node("Course")
@Data
public class Course {
    @Id
    private String code;
    
    @Property("title_vn")
    private String titleVn;
    
    @Property("title_en")
    private String titleEn;
    
    private String summary;
}
