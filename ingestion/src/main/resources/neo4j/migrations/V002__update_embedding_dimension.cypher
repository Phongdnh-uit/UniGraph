// ---------------------------------------------------------
// Migration V002: Update embedding dimension to 768
// ---------------------------------------------------------

// Xóa index cũ (1024 dimensions)
DROP INDEX course_embeddings IF EXISTS;

// Tạo lại index với dimension 768 (phù hợp với embeddinggemma)
CREATE VECTOR INDEX course_embeddings IF NOT EXISTS
FOR (c:Course) ON (c.embedding)
OPTIONS {indexConfig: {
 `vector.dimensions`: 768,
 `vector.similarity_function`: 'cosine'
}};
