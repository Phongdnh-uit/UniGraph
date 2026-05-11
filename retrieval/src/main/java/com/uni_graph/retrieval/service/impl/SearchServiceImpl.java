package com.uni_graph.retrieval.service.impl;

import com.uni_graph.retrieval.domain.Course;
import com.uni_graph.retrieval.repository.CourseRepository;
import com.uni_graph.retrieval.service.SearchService;
import dev.langchain4j.model.embedding.EmbeddingModel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class SearchServiceImpl implements SearchService {
    private final CourseRepository courseRepository;
    private final EmbeddingModel embeddingModel;

    @Override
    public List<Course> hybridSearch(String query) {
        // 1. Vector Search
        var embeddingContent = embeddingModel.embed(query).content();
        List<Double> vector = new ArrayList<>();
        for (float f : embeddingContent.vector()) vector.add((double) f);
        List<Course> vectorResults = courseRepository.searchByVector(vector, 5);

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
}
