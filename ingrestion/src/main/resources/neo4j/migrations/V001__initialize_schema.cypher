// ---------------------------------------------------------
// 1. UNIQUE CONSTRAINTS (Đảm bảo định danh duy nhất)
// ---------------------------------------------------------

// Academic Domain
CREATE CONSTRAINT unique_course_code IF NOT EXISTS FOR (c:Course) REQUIRE c.code IS UNIQUE;
CREATE CONSTRAINT unique_dept_name IF NOT EXISTS FOR (d:Department) REQUIRE d.name IS UNIQUE;
CREATE CONSTRAINT unique_group_name IF NOT EXISTS FOR (g:Group) REQUIRE g.name IS UNIQUE;

// Logic Domain
CREATE CONSTRAINT unique_rule_id IF NOT EXISTS FOR (r:RequirementRule) REQUIRE r.id IS UNIQUE;

// Operational Domain
CREATE CONSTRAINT unique_section_id IF NOT EXISTS FOR (s:Section) REQUIRE s.section_id IS UNIQUE;
CREATE CONSTRAINT unique_teacher_email IF NOT EXISTS FOR (t:Teacher) REQUIRE t.email IS UNIQUE;
CREATE CONSTRAINT unique_semester_name IF NOT EXISTS FOR (sem:Semester) REQUIRE sem.name IS UNIQUE;
CREATE CONSTRAINT unique_classroom_id IF NOT EXISTS FOR (cl:Classroom) REQUIRE cl.room_id IS UNIQUE;
CREATE CONSTRAINT unique_timeslot_id IF NOT EXISTS FOR (ts:TimeSlot) REQUIRE ts.slot_id IS UNIQUE;

// Student Domain
CREATE CONSTRAINT unique_student_id IF NOT EXISTS FOR (st:Student) REQUIRE st.student_id IS UNIQUE;

// ---------------------------------------------------------
// 2. SEARCH INDEXES (Tối ưu truy vấn tìm kiếm)
// ---------------------------------------------------------

// Hỗ trợ tìm nhanh môn học theo tên
CREATE INDEX course_title_index IF NOT EXISTS FOR (c:Course) ON (c.title);

// Hỗ trợ tìm giảng viên theo tên
CREATE INDEX teacher_name_index IF NOT EXISTS FOR (t:Teacher) ON (t.name);

// ---------------------------------------------------------
// 3. VECTOR INDEX (Trái tim của GraphRAG)
// ---------------------------------------------------------

// Khởi tạo Index cho tìm kiếm ngữ nghĩa trên mô tả môn học
// Sử dụng 1536 dimensions (chuẩn OpenAI) và cosine similarity
CREATE VECTOR INDEX course_embeddings IF NOT EXISTS
FOR (c:Course) ON (c.embedding)
OPTIONS {indexConfig: {
 `vector.dimensions`: 1536,
 `vector.similarity_function`: 'cosine'
}};
