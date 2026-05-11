# Design Spec: Retrieval Service

## 1. Overview
The `retrieval` service is a Spring Boot application designed to query academic data from a Neo4j database and provide AI-powered answers using Retrieval-Augmented Generation (RAG). It works in tandem with the `ingestion` service, which populates the database with courses, teachers, and their embeddings.

## 2. Technical Stack
- **Framework**: Spring Boot 4.0.4
- **Language**: Java 25
- **Database**: Neo4j (SDN 7+)
- **AI Framework**: LangChain4j 0.36.2
- **LLM/Embedding Provider**: Ollama (local)
- **API Documentation**: SpringDoc OpenAPI 3.0.1
- **Build Tool**: Gradle (Kotlin DSL)

## 3. Architecture & Components

### 3.1. Search Module
- **SearchService**: Responsible for hybrid search.
    - **Keyword Search**: Uses Cypher `MATCH` with property filters or indexes.
    - **Vector Search**: Uses `db.index.vector.queryNodes` on the `course_embeddings` index.
- **CourseRepository**: Extends `Neo4jRepository` and includes custom Cypher queries for similarity search.

### 3.2. RAG Module
- **ChatService**: Orchestrates the RAG flow.
    1. Embeds the user query.
    2. Retrieves top-K context from Neo4j.
    3. Formats a prompt with the context.
    4. Calls Ollama for text generation.
- **AiConfig**: Bean definitions for `EmbeddingModel` and `ChatLanguageModel`.

### 3.3. API Layer
- **RetrievalController**:
    - `GET /api/v1/search`: Hybrid search for courses.
    - `POST /api/v1/chat`: Conversational RAG.

## 4. Data Flow
1. **User Query** -> `RetrievalController`
2. `RetrievalController` -> `SearchService` (for search) or `ChatService` (for RAG).
3. `SearchService`/`ChatService` -> `EmbeddingModel` (to embed query).
4. `SearchService`/`ChatService` -> `Neo4j` (to retrieve data/context).
5. `ChatService` -> `ChatLanguageModel` (to generate answer).
6. **Response** -> User.

## 5. Configuration (application.yaml)
- Neo4j URI/Credentials.
- Ollama base URL and model names (embedding & generative).

## 6. Success Criteria
- Accurate course retrieval based on semantic meaning.
- Meaningful AI-generated answers grounded in the Neo4j data.
- Low latency for search and chat operations (local LLM performance permitting).
