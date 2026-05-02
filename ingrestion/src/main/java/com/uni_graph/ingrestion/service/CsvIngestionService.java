package com.uni_graph.ingrestion.service;

import java.io.InputStream;

public interface CsvIngestionService {
    void ingestCoursesFromCsv(InputStream inputStream);
}
