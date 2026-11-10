import os
import subprocess
import html

BASE_DIR = "/Users/min/Desktop/IUH-k2/PTHTTH/Pthtth_25765141_letuanminh_31/lab8_jdbc"
SCREENSHOTS_DIR = os.path.join(BASE_DIR, "screenshots_cursor")
PDF_OUTPUT = "/Users/min/Desktop/Bao_Cao_Lab_08_JDBC_25765141_LeTuanMinh.pdf"
REPO_PDF = os.path.join(BASE_DIR, "Bao_Cao_Lab_08_JDBC_25765141_LeTuanMinh.pdf")
CHROME_PATH = "/Applications/Google Chrome.app/Contents/MacOS/Google Chrome"

tasks = [
    {
        "title": "Câu 1: B1. Thiết lập kết nối cơ sở dữ liệu SQLite qua JDBC",
        "file": "src/phan1_huongdan/B1_KetNoiSQLite.java",
        "img_code": os.path.join(SCREENSHOTS_DIR, "cau1_code_cursor.png"),
        "img_term": os.path.join(SCREENSHOTS_DIR, "cau1_terminal_cursor.png")
    },
    {
        "title": "Câu 2: B2 - B5. Thao tác bảng NhanVien (Tạo bảng, Chèn bản ghi, Đọc ResultSet & Đóng kết nối)",
        "file": "src/phan1_huongdan/NhanVienCRUD_Statement.java",
        "img_code": os.path.join(SCREENSHOTS_DIR, "cau2_code_cursor.png"),
        "img_term": os.path.join(SCREENSHOTS_DIR, "cau2_terminal_cursor.png")
    },
    {
        "title": "Câu 3: B3 (Mở rộng). Quản lý giao dịch Transaction (Commit thành công & Rollback khi lỗi)",
        "file": "src/phan1_huongdan/NhanVienTransactionDemo.java",
        "img_code": os.path.join(SCREENSHOTS_DIR, "cau3_code_cursor.png"),
        "img_term": os.path.join(SCREENSHOTS_DIR, "cau3_terminal_cursor.png")
    },
    {
        "title": "Câu 4: Bài tập 1. Quản lý danh mục Sản Phẩm (CRUD đầy đủ: Create, Read, Update, Delete)",
        "file": "src/phan2_baitap/bai1_quanly_sanpham/MainSanPhamCRUD.java",
        "img_code": os.path.join(SCREENSHOTS_DIR, "cau4_code_cursor.png"),
        "img_term": os.path.join(SCREENSHOTS_DIR, "cau4_terminal_cursor.png")
    },
    {
        "title": "Câu 5: Bài tập 2. Sử dụng giao dịch (Transaction) trong cập nhật kho hàng",
        "file": "src/phan2_baitap/bai2_transaction/TransactionSanPhamDemo.java",
        "img_code": os.path.join(SCREENSHOTS_DIR, "cau5_code_cursor.png"),
        "img_term": os.path.join(SCREENSHOTS_DIR, "cau5_terminal_cursor.png")
    },
    {
        "title": "Câu 6: Bài tập 3. Xử lý ngoại lệ triệt để & Giải phóng tài nguyên an toàn trong khối finally",
        "file": "src/phan2_baitap/bai3_exception_handling/ExceptionHandlingStrictDemo.java",
        "img_code": os.path.join(SCREENSHOTS_DIR, "cau6_code_cursor.png"),
        "img_term": os.path.join(SCREENSHOTS_DIR, "cau6_terminal_cursor.png")
    },
    {
        "title": "Câu 7: Bài tập 4. Sử dụng PreparedStatement tăng cường bảo mật (Chống SQL Injection) & Benchmark",
        "file": "src/phan2_baitap/bai4_prepared_statement/PreparedStatementSecurityDemo.java",
        "img_code": os.path.join(SCREENSHOTS_DIR, "cau7_code_cursor.png"),
        "img_term": os.path.join(SCREENSHOTS_DIR, "cau7_terminal_cursor.png")
    },
    {
        "title": "Câu 8: Bài tập 5. Truy vấn dữ liệu nâng cao (Aggregation Functions: SUM, AVG, MIN, MAX, GROUP BY)",
        "file": "src/phan2_baitap/bai5_aggregation/NhanVienAggregationDemo.java",
        "img_code": os.path.join(SCREENSHOTS_DIR, "cau8_code_cursor.png"),
        "img_term": os.path.join(SCREENSHOTS_DIR, "cau8_terminal_cursor.png")
    }
]

content_sections = []
for t in tasks:
    sec = f"""
    <div class="question-block">
      <h2>{html.escape(t['title'])}</h2>
      
      <div class="image-box">
        <p class="img-caption">1. Ảnh chụp Full Code trên Cursor IDE ({os.path.basename(t['file'])}):</p>
        <img src="file://{t['img_code']}" alt="Code Cursor" />
      </div>

      <div class="image-box">
        <p class="img-caption">2. Ảnh chụp Màn hình kết quả chạy trên Cursor Terminal:</p>
        <img src="file://{t['img_term']}" alt="Terminal Cursor" />
      </div>
    </div>
    """
    content_sections.append(sec)

all_sections_html = "\n".join(content_sections)

full_html = f"""<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8">
<title>Báo Cáo Lab 08 JDBC - Lê Tuấn Minh</title>
<style>
  @page {{
    size: A4;
    margin: 15mm 15mm 15mm 15mm;
  }}
  body {{
    font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, Arial, sans-serif;
    color: #1a202c;
    line-height: 1.4;
    padding: 0;
    margin: 0;
  }}
  .header {{
    text-align: center;
    border-bottom: 2px solid #184c78;
    padding-bottom: 12px;
    margin-bottom: 18px;
  }}
  .header h3 {{
    margin: 0 0 6px 0;
    color: #184c78;
    font-size: 14pt;
    text-transform: uppercase;
  }}
  .header h1 {{
    margin: 0;
    color: #0f172a;
    font-size: 18pt;
  }}
  .meta-table {{
    width: 100%;
    border-collapse: collapse;
    margin-bottom: 24px;
    font-size: 10.5pt;
  }}
  .meta-table td {{
    padding: 6px 12px;
    border: 1px solid #cbd5e1;
  }}
  .meta-table .label {{
    width: 28%;
    font-weight: bold;
    background: #f1f5f9;
  }}
  .question-block {{
    margin-bottom: 28px;
    page-break-inside: avoid;
  }}
  h2 {{
    color: #184c78;
    font-size: 13pt;
    border-bottom: 1px solid #e2e8f0;
    padding-bottom: 4px;
    margin-top: 20px;
    margin-bottom: 12px;
  }}
  .image-box {{
    margin-bottom: 16px;
    page-break-inside: avoid;
  }}
  .img-caption {{
    font-weight: bold;
    font-size: 10.5pt;
    color: #334155;
    margin: 0 0 6px 0;
  }}
  img {{
    width: 100%;
    max-width: 100%;
    border-radius: 6px;
    box-shadow: 0 2px 8px rgba(0,0,0,0.15);
    display: block;
  }}
</style>
</head>
<body>
  <div class="header">
    <h3>Trường Đại học Công nghiệp TP. Hồ Chí Minh (IUH) — Khoa CNTT</h3>
    <h1>BÁO CÁO THỰC HÀNH LAB 08: JDBC VỚI SQLITE</h1>
  </div>

  <table class="meta-table">
    <tr>
      <td class="label">Họ và tên sinh viên:</td>
      <td>Lê Tuấn Minh</td>
    </tr>
    <tr>
      <td class="label">Mã số sinh viên (MSSV):</td>
      <td>25765141</td>
    </tr>
    <tr>
      <td class="label">Môn học / STT:</td>
      <td>Phát triển Hệ thống Tích hợp & Phân tán (PTHTTH) — STT: 31</td>
    </tr>
    <tr>
      <td class="label">Môi trường thực hiện:</td>
      <td>Cursor IDE, macOS ARM64, Java 25 LTS, SQLite JDBC 3.53.4.0</td>
    </tr>
  </table>

  {all_sections_html}
</body>
</html>"""

tmp_html = "/tmp/report_full_pdf.html"
with open(tmp_html, "w", encoding="utf-8") as f:
    f.write(full_html)

cmd = [
    CHROME_PATH,
    "--headless",
    "--disable-gpu",
    "--no-pdf-header-footer",
    f"--print-to-pdf={PDF_OUTPUT}",
    f"file://{tmp_html}"
]
subprocess.run(cmd, check=True)
print(f"[OK] Đã xuất PDF ra Desktop: {PDF_OUTPUT}")

subprocess.run(["cp", PDF_OUTPUT, REPO_PDF], check=True)
print(f"[OK] Đã sao lưu PDF vào repo: {REPO_PDF}")
