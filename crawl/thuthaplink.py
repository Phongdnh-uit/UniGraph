import requests
from bs4 import BeautifulSoup
import pandas as pd
import re

# Cấu hình
BASE_URL = "https://student.uit.edu.vn"
SOURCE_URL = "https://student.uit.edu.vn/content/chuong-trinh-dao-tao-tu-khoa-7-tro-di"
HEADERS = {
    "User-Agent": "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36"
}

def collect_modern_links(start_year=2023):
    try:
        print(f"Đang kết nối tới trang tổng: {SOURCE_URL}")
        response = requests.get(SOURCE_URL, headers=HEADERS, timeout=15)
        response.raise_for_status()
        response.encoding = 'utf-8'

        soup = BeautifulSoup(response.text, 'html.parser')
        acc_items = soup.find_all('div', class_='acc-item')

        target_programs = []

        for item in acc_items:
            accordion_header = item.find('div', class_='accordion')
            if not accordion_header:
                continue

            header_text = accordion_header.get_text(strip=True)

            # Sử dụng Regex để tìm số năm trong tiêu đề (ví dụ: "Khoá 2023" -> 2023)
            year_match = re.search(r'20\d{2}', header_text)

            if year_match:
                year = int(year_match.group())

                # LỌC: Chỉ lấy các năm từ 2023 trở lên
                if year >= start_year:
                    print(f"--- Đang trích xuất: {header_text} ---")

                    panel = item.find('div', class_='panel')
                    if not panel: continue

                    rows = panel.find_all('div', class_='views-row')
                    for row in rows:
                        link_tag = row.find('a')
                        if link_tag and link_tag.has_attr('href'):
                            program_name = link_tag.get_text(strip=True)
                            program_url = link_tag['href']

                            if not program_url.startswith('http'):
                                program_url = BASE_URL + program_url

                            target_programs.append({
                                "Năm": year,
                                "Khóa": header_text,
                                "Tên Chương Trình": program_name,
                                "URL": program_url
                            })

        if target_programs:
            df = pd.DataFrame(target_programs)
            # Sắp xếp lại theo năm cho dễ nhìn
            df = df.sort_values(by='Năm', ascending=False)

            filename = f'links_ctdt_tu_{start_year}.csv'
            df.to_csv(filename, index=False, encoding='utf-8-sig')
            print(f"\nThành công! Đã tìm thấy {len(df)} chương trình từ khóa {start_year} trở lên.")
            print(f"Dữ liệu đã lưu vào: {filename}")
        else:
            print(f"Không tìm thấy chương trình nào từ năm {start_year}.")

    except Exception as e:
        print(f"Lỗi xảy ra: {e}")

if __name__ == "__main__":
    # Bạn có thể thay đổi con số này tùy ý
    collect_modern_links(start_year=2023)
