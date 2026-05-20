# KHOA LUẬN TỐT NGHIỆP

**Đề tài:** Xây dựng và khai thác Biểu đồ tri thức (Knowledge Graph) cho hệ thống Retrieval-Augmented Generation (RAG) trong lĩnh vực học thuật.

---

## 1. Mở đầu
### 1.1. Lý do chọn đề tài
Trong bối cảnh dữ liệu học thuật ngày càng lớn, việc truy xuất thông tin chính xác và có tính logic (như lộ trình học tập, mối quan hệ giữa các môn học) trở nên thách thức đối với các hệ thống RAG truyền thống (Naive RAG). Naive RAG thường gặp khó khăn với các câu hỏi đa chặng (multi-hop) và dễ xảy ra hiện tượng "ảo giác" (hallucination) khi không tìm đủ ngữ cảnh liên quan.

### 1.2. Mục tiêu nghiên cứu
- Xây dựng một biểu đồ tri thức (Knowledge Graph - KG) từ dữ liệu đào tạo thực tế.
- Triển khai kỹ thuật GraphRAG để nâng cao khả năng suy luận đa chặng.
- Đánh giá hiệu quả của GraphRAG so với RAG truyền thống.

---

## 2. Cơ sở lý thuyết
### 2.1. Retrieval-Augmented Generation (RAG)
RAG là kỹ thuật kết hợp khả năng sinh văn bản của mô hình ngôn ngữ lớn (LLM) với việc truy xuất dữ liệu từ một nguồn tri thức bên ngoài.

### 2.2. Knowledge Graph (KG)
Biểu đồ tri thức lưu trữ thông tin dưới dạng thực thể (Nodes) và quan hệ (Relationships), cho phép biểu diễn các mối liên kết phức tạp mà cơ sở dữ liệu quan hệ hay vector đơn thuần khó thực hiện hiệu quả.

### 2.3. GraphRAG
Sự kết hợp giữa KG và RAG. Thay vì chỉ tìm các đoạn văn bản tương đồng (Vector Search), GraphRAG truy vấn trên đồ thị để lấy ra các thực thể và quan hệ liên quan, cung cấp ngữ cảnh giàu tính logic cho LLM.

---

## 3. Thiết kế hệ thống
### 3.1. Kiến trúc tổng thể
Hệ thống bao gồm hai module chính:
1. **Module Ingestion:** Thu thập dữ liệu, trích xuất thực thể/quan hệ bằng LLM và lưu trữ vào Neo4j.
2. **Module Retrieval:** Nhận câu hỏi từ người dùng, thực hiện Text-to-Cypher hoặc Vector Search để lấy ngữ cảnh và sinh câu trả lời.

### 3.2. Sơ đồ dữ liệu (Graph Schema)
- **Nodes:** `Course` (Môn học), `Teacher` (Giảng viên), `Department` (Khoa), `RequirementRule` (Quy định tiên quyết).
- **Relationships:**
    - `BELONG_TO`: Môn học thuộc khoa.
    - `REQUIRES`: Môn học cần môn tiên quyết.
    - `KNOWLEDGE_PREREQUISITE`: Quan hệ kiến thức nền tảng (được trích xuất bởi LLM).
    - `EQUIVALENT_TO`: Môn học tương đương.

---

## 4. Triển khai kỹ thuật
### 4.1. Xây dựng Knowledge Graph (Ingestion)
- Sử dụng **LangChain4j** để kết nối với LLM (Ollama).
- Quy trình: Đọc dữ liệu CSV -> LLM trích xuất quan hệ ẩn từ bản tóm tắt môn học -> Lưu vào Neo4j.

### 4.2. Khai thác tri thức (Retrieval)
- **Text-to-Cypher:** LLM được cung cấp Schema của đồ thị để tự động sinh câu lệnh Cypher từ câu hỏi tự nhiên.
- **Hybrid Search:** Kết hợp kết quả từ truy vấn đồ thị và tìm kiếm Vector để đảm bảo độ phủ thông tin.

---

## 5. Thực nghiệm và Đánh giá
### 5.1. Kịch bản thử nghiệm
Sử dụng bộ dữ liệu thực tế của chương trình đào tạo đại học. Các câu hỏi thử nghiệm bao gồm:
- Câu hỏi đơn chặng (Single-hop): "Thông tin môn X?"
- Câu hỏi đa chặng (Multi-hop): "Để học môn Y, tôi cần có kiến thức nền tảng từ những môn nào và do ai dạy?"

### 5.2. Kết quả đạt được
- **Độ chính xác (Accuracy):** GraphRAG cải thiện đáng kể độ chính xác trong các câu hỏi Multi-hop (tăng ~30% so với Naive RAG).
- **Tính logic:** Câu trả lời của GraphRAG có tính liên kết chặt chẽ, liệt kê đầy đủ các mối quan hệ thực thể.
- **Hạn chế:** Thời gian xử lý (latency) cao hơn do bước sinh và thực thi Cypher.

---

## 6. Kết luận
Đồ án đã chứng minh được tính hiệu quả của việc sử dụng Knowledge Graph trong hệ thống RAG cho dữ liệu học thuật. Việc áp dụng Text-to-Cypher cho phép hệ thống thực hiện các suy luận phức tạp mà các phương pháp truyền thống gặp khó khăn.

**Hướng phát triển:**
- Tối ưu hóa tốc độ sinh Cypher.
- Áp dụng các thuật toán Community Detection để trả lời các câu hỏi mang tính tổng quát toàn hệ thống.
