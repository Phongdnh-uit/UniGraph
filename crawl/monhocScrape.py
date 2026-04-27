import requests
from bs4 import BeautifulSoup
import pandas as pd

# 1. Cấu hình
URL = "https://student.uit.edu.vn/danh-muc-mon-hoc-dai-hoc" # Thay URL thật của bạn vào đây
HEADERS = {
    "User-Agent": "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36"
}

def crawl_from_web(url):
    try:
        # Tải nội dung trang web
        response = requests.get(url, headers=HEADERS)
        response.raise_for_status() # Kiểm tra nếu lỗi (404, 500...)
        response.encoding = 'utf-8' # Đảm bảo đọc đúng tiếng Việt

        soup = BeautifulSoup(response.text, 'html.parser')

        # Tìm bảng - Dựa trên class bạn cung cấp
        table = soup.find('table', class_='tablesorter')
        if not table:
            print("Không tìm thấy bảng dữ liệu trên trang này!")
            return None

        results = []
        rows = table.find('tbody').find_all('tr')

        for row in rows:
            cols = row.find_all('td')
            if len(cols) < 10: continue # Bỏ qua dòng lỗi hoặc dòng trống

            # Xử lý trạng thái "Còn mở lớp" (Cột 5)
            is_open = "Đang mở" if cols[4].find('img') else "Đóng"

            item = {
                "Số TT": cols[0].text.strip(),
                "Mã MH": cols[1].text.strip(),
                "Tên MH (VN)": cols[2].text.strip(),
                "Tên MH (EN)": cols[3].text.strip(),
                "Còn mở lớp": is_open,
                "Đơn vị quản lý": cols[5].text.strip(),
                "Loại MH": cols[6].text.strip(),
                "Mã cũ": cols[7].text.strip(),
                "Mã tương đương": cols[8].text.strip(),
                "Mã tiên quyết": cols[9].text.strip(),
                "Mã môn học trước": cols[10].text.strip(),
                "Số TCLT": cols[11].text.strip(),
                "Số TCTH": cols[12].text.strip(),
            }
            results.append(item)

        return results

    except Exception as e:
        print(f"Lỗi khi crawl: {e}")
        return None

# --- THỰC THI ---
data = crawl_from_web(URL)

if data:
    df = pd.DataFrame(data)

    # Xuất file
    df.to_csv('danh_muc_mon_hoc.csv', index=False, encoding='utf-8-sig')

    print(f"Thành công! Đã lấy được {len(df)} môn học.")