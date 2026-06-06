import unittest
import random
from generate_eval_dataset import gen_simple, gen_medium

class TestGenerateEvalDataset(unittest.TestCase):
    def setUp(self):
        self.subjects = [
            {
                'course_code': 'MATH101',
                'course_name_vi': 'Toán cao cấp 1',
                'theory_credits': '3',
                'practical_credits': '0',
                'department': 'Toán',
                'prerequisite_course_codes': '',
                'equivalent_course_codes': ''
            },
            {
                'course_code': 'CS101',
                'course_name_vi': 'Tin học cơ sở',
                'theory_credits': '2',
                'practical_credits': '1',
                'department': 'CNTT',
                'prerequisite_course_codes': 'MATH101',
                'equivalent_course_codes': 'CS102'
            }
        ]

    def test_gen_simple_count(self):
        # We need at least 'count' subjects to sample from
        count = 2
        questions = gen_simple(self.subjects, count)
        self.assertEqual(len(questions), count)

    def test_gen_simple_structure(self):
        questions = gen_simple(self.subjects, 1)
        q = questions[0]
        self.assertIn('category', q)
        self.assertIn('question', q)
        self.assertIn('reference_answer', q)
        self.assertIn('expected_behavior', q)
        self.assertEqual(q['category'], 'Level 1: Simple')

    def test_gen_simple_content(self):
        # Set seed for reproducibility if needed, but here we just check if it generates valid content
        random.seed(42)
        questions = gen_simple(self.subjects, 2)
        for q in questions:
            self.assertIsNotNone(q['question'])
            self.assertIsNotNone(q['reference_answer'])
            # Check if question contains course name or code
            self.assertTrue(any(s['course_name_vi'] in q['question'] or s['course_code'] in q['question'] for s in self.subjects))

    def test_gen_medium_count(self):
        # CS101 has prerequisites, so it should be selectable
        count = 1
        questions = gen_medium(self.subjects, count)
        self.assertEqual(len(questions), count)

    def test_gen_medium_structure(self):
        questions = gen_medium(self.subjects, 1)
        if not questions:
            self.fail("gen_medium returned empty list")
        q = questions[0]
        self.assertIn('category', q)
        self.assertIn('question', q)
        self.assertIn('reference_answer', q)
        self.assertIn('expected_behavior', q)
        self.assertEqual(q['category'], 'Level 2: Medium')

if __name__ == '__main__':
    unittest.main()
