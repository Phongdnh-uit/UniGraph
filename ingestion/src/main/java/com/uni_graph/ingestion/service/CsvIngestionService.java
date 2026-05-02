package com.uni_graph.ingestion.service;

import java.io.InputStream;

public interface CsvIngestionService {
    void ingestCoursesFromCsv(InputStream inputStream);
}
