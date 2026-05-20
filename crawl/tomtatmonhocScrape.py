import requests
from bs4 import BeautifulSoup
import pandas as pd

# URL của trang tóm tắt môn học
URL = "https://student.uit.edu.vn/content/bang-tom-tat-mon-hoc"
HEADERS = {
    "User-Agent": "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36"
}

def crawl_summaries():
    try:
        print(f"Đang kết nối tới: {URL}...")
        response = requests.get(URL, headers=HEADERS, timeout=15)
        response.raise_for_status()
        response.encoding = 'utf-8'

        soup = BeautifulSoup(response.text, 'html.parser')

        # Tìm bảng trong vùng content của trang mới
        table = soup.find('table')
        if not table:
            print("Không tìm thấy bảng tóm tắt!")
            return

        rows = table.find('tbody').find_all('tr')
        summary_data = []

        for row in rows:
            cols = row.find_all('td')
            if len(cols) < 4:
                continue

            # Lấy mã môn và nội dung tóm tắt
            ma_mh = cols[1].get_text(strip=True)
            ten_mh = cols[2].get_text(strip=True)
            tom_tat = cols[3].get_text(strip=True)

            summary_data.append({
                "course_code": ma_mh,
                "course_name_vi": ten_mh,
                "course_summary": tom_tat
            })

        df = pd.DataFrame(summary_data)

        # Lưu ra file CSV riêng
        df.to_csv('subject_summary.csv', index=False, encoding='utf-8-sig')
        print(f"Thành công! Đã lấy được {len(df)} dòng tóm tắt.")
        return df

    except Exception as e:
        print(f"Lỗi: {e}")
        return None

if __name__ == "__main__":
    crawl_summaries()