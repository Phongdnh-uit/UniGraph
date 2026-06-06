import csv
import random
from collections import defaultdict

def load_csv(file_path):
    with open(file_path, mode='r', encoding='utf-8-sig') as f:
        return list(csv.DictReader(f))

def build_prereq_graph(subjects):
    graph = defaultdict(list)
    for s in subjects:
        code = s['course_code'].strip()
        prereqs = s['prerequisite_course_codes'].strip()
        # Initialize entry for the course even if no prereqs
        if code not in graph:
            graph[code] = []
        if prereqs:
            # Handle multiple prereqs if comma separated
            for p in prereqs.split(','):
                p_clean = p.strip()
                if p_clean:
                    graph[code].append(p_clean)
    return graph

def gen_simple(subjects, count=5):
    questions = []
    sampled = random.sample(subjects, count)
    for row in sampled:
        q_type = random.choice(['credits', 'code', 'dept'])
        if q_type == 'credits':
            q = f"Môn {row['course_name_vi']} có bao nhiêu tín chỉ lý thuyết và thực hành?"
            a = f"{row['theory_credits']} tín chỉ lý thuyết, {row['practical_credits']} tín chỉ thực hành."
        elif q_type == 'code':
            q = f"Mã môn học của môn {row['course_name_vi']} là gì?"
            a = f"{row['course_code']}"
        else:
            q = f"Môn {row['course_code']} thuộc khoa nào quản lý?"
            a = f"{row['department']}"
        
        questions.append({
            'category': 'Level 1: Simple',
            'question': q,
            'reference_answer': a,
            'expected_behavior': 'Direct attribute lookup. Both systems should perform well.'
        })
    return questions

def gen_medium(subjects, count=10):
    questions = []
    # Note: Using the corrected headers found in Task 1: 
    # prerequisite_course_codes and equivalent_course_codes
    with_pre = [s for s in subjects if s['prerequisite_course_codes'].strip()]
    
    # Use pool of subjects with prerequisites to ensure meaningful questions
    pool = random.sample(with_pre, min(len(with_pre), count))
    for row in pool:
        q_type = random.choice(['prereq', 'equiv'])
        if q_type == 'prereq':
            q = f"Để học môn {row['course_code']}, sinh viên cần hoàn thành môn tiên quyết nào?"
            a = row['prerequisite_course_codes']
        else:
            q = f"Môn {row['course_code']} có môn học nào tương đương không?"
            a = row['equivalent_course_codes'] if row['equivalent_course_codes'].strip() else "Không có môn tương đương."
            
        questions.append({
            'category': 'Level 2: Medium',
            'question': q,
            'reference_answer': a,
            'expected_behavior': 'Single-hop relationship. KG is preferred.'
        })
    return questions[:count]

def gen_hard(subjects, summary_map, graph, count=10):
    questions = []
    # 1. Multi-condition (Dept + Credits)
    depts = list(set(s['department'] for s in subjects))
    for _ in range(3):
        dept = random.choice(depts)
        q = f"Liệt kê các môn học thuộc khoa {dept} có tổng số tín chỉ lớn hơn 3."
        # Use credits headers: theory_credits and practical_credits
        matches = []
        for s in subjects:
            if s['department'] == dept:
                try:
                    total_credits = int(s['theory_credits']) + int(s['practical_credits'])
                    if total_credits > 3:
                        matches.append(s['course_code'])
                except (ValueError, TypeError):
                    continue
        a = ", ".join(matches[:10]) if matches else "Không có môn nào thỏa mãn."
        questions.append({
            'category': 'Level 3: Hard',
            'question': q,
            'reference_answer': a,
            'expected_behavior': 'Multi-condition KG query.'
        })

    # 2. 2-hop Prerequisite
    # Find subjects that have a prerequisite which ALSO has a prerequisite
    subjects_with_2_hop = []
    for s in subjects:
        code = s['course_code']
        if code in graph and graph[code]:
            for p in graph[code]:
                if p in graph and graph[p]:
                    subjects_with_2_hop.append((s, p, graph[p][0]))
                    break
    
    if subjects_with_2_hop:
        pool = random.sample(subjects_with_2_hop, min(len(subjects_with_2_hop), 3))
        for row, p1, p2 in pool:
            q = f"Môn tiên quyết của môn tiên quyết của môn {row['course_code']} là gì?"
            a = f"Môn tiên quyết là {p1}, và tiên quyết của {p1} là {p2}."
            questions.append({
                'category': 'Level 3: Hard',
                'question': q,
                'reference_answer': a,
                'expected_behavior': '2-hop relationship logic.'
            })

    # 3. Summarization
    subjects_with_summary = [s for s in subjects if s['course_code'] in summary_map]
    to_gen = count - len(questions)
    if to_gen > 0 and subjects_with_summary:
        for row in random.sample(subjects_with_summary, min(len(subjects_with_summary), to_gen)):
            q = f"Tóm tắt nội dung chính của môn {row['course_name_vi']}."
            a = summary_map[row['course_code']]
            questions.append({
                'category': 'Level 3: Hard',
                'question': q,
                'reference_answer': a,
                'expected_behavior': 'RAG summarization.'
            })
    return questions

def get_all_prereqs(code, graph, visited=None):
    if visited is None: visited = set()
    res = []
    for p in graph.get(code, []):
        if p not in visited:
            visited.add(p)
            res.append(p)
            res.extend(get_all_prereqs(p, graph, visited))
    return res

def gen_super_hard(subjects, summary_map, graph, count=10):
    questions = []
    # 1. Prerequisite Chain
    # Find subjects with at least 2 ancestors in the prereq tree
    deep_subjects = [s for s in subjects if len(get_all_prereqs(s['course_code'], graph)) >= 2]
    for row in random.sample(deep_subjects, min(len(deep_subjects), 4)):
        chain = get_all_prereqs(row['course_code'], graph)
        q = f"Để học môn {row['course_code']}, tôi cần phải hoàn thành lộ trình các môn học nào từ trước (liệt kê tất cả các môn tiên quyết liên quan)?"
        a = ", ".join(set(chain))
        questions.append({
            'category': 'Level 4: Super Hard',
            'question': q,
            'reference_answer': a,
            'expected_behavior': 'Full graph traversal (Prerequisite Tree).'
        })

    # 2. Semantic Comparison
    subjects_with_summary = [s for s in subjects if s['course_code'] in summary_map]
    for _ in range(3):
        pair = random.sample(subjects_with_summary, 2)
        q = f"So sánh sự khác biệt về mục tiêu và nội dung giữa môn {pair[0]['course_code']} và môn {pair[1]['course_code']}."
        # Provide a snippet of both summaries as reference
        a = f"Môn {pair[0]['course_code']}: {summary_map[pair[0]['course_code']][:200]}... VS Môn {pair[1]['course_code']}: {summary_map[pair[1]['course_code']][:200]}..."
        questions.append({
            'category': 'Level 4: Super Hard',
            'question': q,
            'reference_answer': a,
            'expected_behavior': 'Cross-document semantic reasoning (RAG).'
        })

    # 3. Hybrid (Logic + Semantic)
    # Fill remaining count with hybrid questions
    for _ in range(count - len(questions)):
        row = random.choice(subjects_with_summary)
        summary = summary_map[row['course_code']]
        # Pick a keyword from summary (longer than 5 chars)
        words = [w.strip('.,()\"') for w in summary.split() if len(w) > 5]
        kw = random.choice(words) if words else "kiến thức"
        q = f"Tìm các môn thuộc khoa {row['department']} có đề cập đến '{kw}' trong mô tả và yêu cầu ít nhất một môn tiên quyết."
        a = f"Môn {row['course_code']} là một ví dụ (Khoa: {row['department']}, Mô tả: {summary})"
        questions.append({
            'category': 'Level 4: Super Hard',
            'question': q,
            'reference_answer': a,
            'expected_behavior': 'Hybrid KG + RAG query.'
        })
    return questions

def write_output(questions, file_path):
    keys = ['id', 'category', 'question', 'reference_answer', 'expected_behavior', 'kg_accuracy', 'rag_accuracy', 'notes']
    with open(file_path, 'w', newline='', encoding='utf-8-sig') as f:
        writer = csv.DictWriter(f, fieldnames=keys)
        writer.writeheader()
        for i, q in enumerate(questions):
            q['id'] = i + 1
            q['kg_accuracy'] = ''
            q['rag_accuracy'] = ''
            q['notes'] = ''
            writer.writerow(q)

def main():
    subjects = load_csv('crawl/subjects.csv')
    summaries = load_csv('crawl/subject_summary.csv')
    
    # Map summaries by course_code
    summary_map = {s['course_code']: s['course_summary'] for s in summaries}
    prereq_graph = build_prereq_graph(subjects)
    
    print(f"Loaded {len(subjects)} subjects and {len(summaries)} summaries.")

    all_questions = []
    
    print("Generating questions...")
    all_questions.extend(gen_simple(subjects, 5))
    all_questions.extend(gen_medium(subjects, 10))
    all_questions.extend(gen_hard(subjects, summary_map, prereq_graph, 10))
    all_questions.extend(gen_super_hard(subjects, summary_map, prereq_graph, 10))
    
    print(f"Total questions generated: {len(all_questions)}")

    # Shuffle for variety
    random.shuffle(all_questions)

    output_file = 'crawl/evaluation_dataset_35.csv'
    write_output(all_questions, output_file)
    print(f"Dataset saved to {output_file}")
    
    # Sample output if any
    if all_questions:
        print("\n--- Sample Questions ---")
        for q in random.sample(all_questions, min(len(all_questions), 5)):
            print(f"[{q['category']}] Q: {q['question']}")
            print(f"A: {q['reference_answer']}\n")

if __name__ == "__main__":
    main()
