import requests
from bs4 import BeautifulSoup
import pandas as pd
import re
import json
import time
import os

# Cấu hình
INPUT_FILE = 'links_ctdt_tu_2023.csv'
OUTPUT_FILE = 'uni_curriculum_full_data.json'
HEADERS = {
    "User-Agent": "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36"
}

def clean(text):
    return re.sub(r'\s+', ' ', text).strip() if text else ""

def parse_ultra_detail(url, metadata):
    try:
        res = requests.get(url, headers=HEADERS, timeout=20)
        res.encoding = 'utf-8'
        soup = BeautifulSoup(res.text, 'html.parser')
        container = soup.select_one('div.field-item.even[property="content:encoded"]')
        if not container: return None

        # Khởi tạo object dữ liệu cho ngành này
        program_data = {
            "metadata": metadata, # Chứa Khóa, Năm, Tên ngành từ CSV
            "curriculum": [],
            "teaching_plan": []
        }

        # --- PHẦN 1: CHƯƠNG TRÌNH ĐÀO TẠO (MỤC 3) ---
        dt_3 = None
        for dt in container.find_all('dt'):
            if "3." in dt.get_text():
                dt_3 = dt
                break

        if dt_3:
            dd_3 = dt_3.find_next_sibling('dd')
            c_h2, c_h3, c_h4 = "", "", ""
            for elem in dd_3.find_all(['h2', 'h3', 'h4', 'table', 'p']):
                if elem.name == 'h2': c_h2 = clean(elem.text)
                elif elem.name == 'h3': c_h3 = clean(elem.text)
                elif elem.name == 'h4' or (elem.name == 'p' and elem.find('strong')):
                    c_h4 = clean(elem.get_text())
                elif elem.name == 'table':
                    for row in elem.find_all('tr'):
                        cells = row.find_all('td')
                        if len(cells) < 3: continue
                        ma = clean(cells[1].text).replace('*', '')
                        if re.match(r'^[A-Z]{2,}\d{2,}', ma):
                            program_data["curriculum"].append({
                                "block_l2": c_h2, "block_l3": c_h3, "block_l4": c_h4,
                                "ma_mh": ma, "ten_mh": clean(cells[2].text),
                                "tc": clean(cells[4].text) if len(cells) > 4 else ""
                            })

        # --- PHẦN 2: KẾ HOẠCH GIẢNG DẠY (MỤC 4 - Rowspan) ---
        dt_4 = None
        for dt in container.find_all('dt'):
            text_up = dt.get_text().upper()
            if "4." in text_up or "KẾ HOẠCH GIẢNG DẠY" in text_up:
                dt_4 = dt
                break

        if dt_4:
            dd_4 = dt_4.find_next_sibling('dd')
            table_plan = dd_4.find('table')
            if table_plan:
                current_hk = ""
                for row in table_plan.find_all('tr'):
                    cells = row.find_all('td')
                    row_text = clean(row.get_text())

                    if "Học kỳ" in row_text:
                        for cell in cells:
                            if "Học kỳ" in cell.text:
                                current_hk = clean(cell.text)
                                break

                    row_ma, row_ten = "", ""
                    for cell in cells:
                        txt = clean(cell.text).replace('*', '')
                        if re.match(r'^[A-Z]{2,}\d{2,}', txt):
                            row_ma = txt
                            next_td = cell.find_next_sibling('td')
                            if next_td: row_ten = clean(next_td.text)
                            break

                    if row_ma:
                        program_data["teaching_plan"].append({
                            "hoc_ky": current_hk, "ma_mh": row_ma, "ten_mh": row_ten
                        })

        return program_data
    except Exception as e:
        print(f" -> Lỗi tại {url}: {e}")
        return None

def main():
    if not os.path.exists(INPUT_FILE):
        print(f"Lỗi: Không tìm thấy file {INPUT_FILE}!")
        return

    df_links = pd.read_csv(INPUT_FILE)
    all_results = []

    print(f"Bắt đầu crawl sâu {len(df_links)} chương trình đào tạo...")

    for idx, row in df_links.iterrows():
        metadata = {
            "year": int(row['Năm']),
            "cohort": row['Khóa'],
            "program_name": row['Tên Chương Trình']
        }

        print(f"[{idx+1}/{len(df_links)}] Đang quét: {metadata['program_name']}...")

        data = parse_ultra_detail(row['URL'], metadata)
        if data:
            all_results.append(data)

        # Nghỉ 1s để tránh bị block IP
        time.sleep(1)

    # Xuất toàn bộ ra file JSON
    with open(OUTPUT_FILE, 'w', encoding='utf-8') as f:
        json.dump(all_results, f, ensure_ascii=False, indent=4)

    print(f"\n--- HOÀN THÀNH ---")
    print(f"Đã lưu dữ liệu {len(all_results)} ngành vào {OUTPUT_FILE}")

if __name__ == "__main__":
    main()
