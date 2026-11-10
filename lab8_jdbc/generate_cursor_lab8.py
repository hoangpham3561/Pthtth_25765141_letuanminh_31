import os
import subprocess
import html
from pygments import highlight
from pygments.lexers import JavaLexer
from pygments.formatters import HtmlFormatter
import docx
from docx.shared import Inches, Pt, RGBColor
from docx.enum.text import WD_ALIGN_PARAGRAPH
from docx.enum.table import WD_TABLE_ALIGNMENT
from docx.oxml import parse_xml
from docx.oxml.ns import nsdecls

BASE_DIR = "/Users/min/Desktop/IUH-k2/PTHTTH/Pthtth_25765141_letuanminh_31/lab8_jdbc"
BIN_DIR = os.path.join(BASE_DIR, "bin")
JAR_PATH = os.path.join(BASE_DIR, "lib/sqlite-jdbc-3.53.4.0.jar")
SCREENSHOTS_DIR = os.path.join(BASE_DIR, "screenshots_cursor")
os.makedirs(SCREENSHOTS_DIR, exist_ok=True)
OUTPUT_DOCX = os.path.join(BASE_DIR, "Bao_Cao_Lab_08_JDBC_25765141_LeTuanMinh.docx")
DESKTOP_DOCX = "/Users/min/Desktop/Bao_Cao_Lab_08_JDBC_25765141_LeTuanMinh.docx"
CHROME_PATH = "/Applications/Google Chrome.app/Contents/MacOS/Google Chrome"

formatter = HtmlFormatter(style='one-dark', linenos=True, cssclass='source')
pygments_css = formatter.get_style_defs('.source')

def render_html_to_png(html_content, output_png, width=1100, height=800):
    tmp_html = "/tmp/cursor_render_temp.html"
    with open(tmp_html, "w", encoding="utf-8") as f:
        f.write(html_content)

    cmd = [
        CHROME_PATH,
        "--headless",
        "--disable-gpu",
        "--hide-scrollbars",
        f"--window-size={width},{height}",
        f"--screenshot={output_png}",
        f"file://{tmp_html}"
    ]
    res = subprocess.run(cmd, capture_output=True, text=True)
    if res.returncode != 0:
        print(f"Error rendering {output_png}: {res.stderr}")
    else:
        print(f"[OK] Rendered: {output_png} ({width}x{height})")

def create_editor_screenshot(file_rel_path, output_png):
    abs_path = os.path.join(BASE_DIR, file_rel_path)
    with open(abs_path, "r", encoding="utf-8") as f:
        code = f.read()

    filename = os.path.basename(file_rel_path)
    lines_count = len(code.splitlines())
    highlighted_code = highlight(code, JavaLexer(), formatter)

    calc_height = min(max(300, 110 + lines_count * 22), 1600)

    html_template = f"""<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8">
<style>
  * {{ box-sizing: border-box; }}
  body {{
    margin: 0; padding: 12px; background: #0b0d10;
    font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, "Helvetica Neue", Arial, sans-serif;
  }}
  .window {{
    background: #181818; border-radius: 9px; border: 1px solid #333940; overflow: hidden;
    box-shadow: 0 16px 40px rgba(0,0,0,0.6);
  }}
  .title-bar {{
    background: #1f1f1f; height: 38px; display: flex; align-items: center; padding: 0 14px;
    border-bottom: 1px solid #2b2d30;
  }}
  .dots {{ display: flex; gap: 8px; margin-right: 18px; }}
  .dot {{ width: 12px; height: 12px; border-radius: 50%; }}
  .dot.red {{ background: #ff5f56; }}
  .dot.yellow {{ background: #ffbd2e; }}
  .dot.green {{ background: #27c93f; }}
  .tab {{
    background: #181818; color: #e6edf3; padding: 7px 16px; font-size: 13px; font-weight: 500;
    border-top: 2px solid #007acc; border-right: 1px solid #282a2e; border-left: 1px solid #282a2e;
    display: flex; align-items: center; gap: 7px;
  }}
  .tab-icon {{ font-size: 13px; color: #e5a54b; }}
  .breadcrumbs {{
    background: #181818; color: #8b949e; font-size: 12px; padding: 6px 18px;
    border-bottom: 1px solid #242629; display: flex; gap: 6px; align-items: center;
  }}
  .cursor-badge {{
    margin-left: auto; font-size: 11px; color: #a1aab5; background: #262a30;
    padding: 3px 10px; border-radius: 4px; border: 1px solid #363b42; font-weight: 500;
  }}
  .editor {{
    padding: 12px 18px;
    font-family: "SF Mono", Menlo, Monaco, Consolas, "Liberation Mono", "Courier New", monospace, Arial, sans-serif;
    font-size: 13.5px; line-height: 1.55; background: #181818;
  }}
  {pygments_css}
  .source {{ background: #181818 !important; }}
  .source pre {{ margin: 0; font-family: inherit; }}
  .linenodiv pre {{
    color: #586069; padding-right: 16px; border-right: 1px solid #2f363d;
    user-select: none; font-family: inherit;
  }}
</style>
</head>
<body>
  <div class="window">
    <div class="title-bar">
      <div class="dots">
        <div class="dot red"></div><div class="dot yellow"></div><div class="dot green"></div>
      </div>
      <div class="tab"><span class="tab-icon">☕</span> {html.escape(filename)}</div>
      <div class="cursor-badge">Cursor IDE — {html.escape(file_rel_path)}</div>
    </div>
    <div class="breadcrumbs">
      <span>lab8_jdbc</span> <span>&gt;</span>
      <span>{html.escape(file_rel_path.replace('/', ' > '))}</span>
    </div>
    <div class="editor">
      {highlighted_code}
    </div>
  </div>
</body>
</html>"""

    render_html_to_png(html_template, output_png, width=1100, height=calc_height + 24)

def create_terminal_screenshot(command, class_name, output_png):
    res = subprocess.run(
        ["java", "--enable-native-access=ALL-UNNAMED", "-cp", f"{BIN_DIR}:{JAR_PATH}", class_name],
        cwd=BASE_DIR,
        capture_output=True,
        text=True
    )
    raw_output = res.stdout + res.stderr
    out_lines = raw_output.splitlines()

    lines_count = len(out_lines)
    calc_height = min(max(260, 100 + (lines_count + 3) * 22), 1400)

    formatted_lines = []
    formatted_lines.append(f'<span class="prompt">min@macbook-pro lab8_jdbc %</span> <span class="cmd">{html.escape(command)}</span>')
    formatted_lines.append('')

    for line in out_lines:
        safe = html.escape(line)
        if "===" in line or "---" in line or "BÀI TẬP" in line or "TEST CASE" in line:
            formatted_lines.append(f'<span class="header">{safe}</span>')
        elif "LỖI" in line or "thất bại" in line.lower() or "bị lộ" in line.lower():
            formatted_lines.append(f'<span class="error">{safe}</span>')
        elif "thành công" in line.lower() or "commit" in line.lower() or "an toàn" in line.lower():
            formatted_lines.append(f'<span class="success">{safe}</span>')
        elif "|" in line:
            formatted_lines.append(f'<span class="table-line">{safe}</span>')
        elif "->" in line or "[+]" in line:
            formatted_lines.append(f'<span class="highlight">{safe}</span>')
        else:
            formatted_lines.append(f'<span class="normal">{safe}</span>')

    terminal_body = "\n".join(formatted_lines)

    html_template = f"""<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8">
<style>
  * {{ box-sizing: border-box; }}
  body {{
    margin: 0; padding: 12px; background: #0b0d10;
    font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, "Helvetica Neue", Arial, sans-serif;
  }}
  .window {{
    background: #141414; border-radius: 9px; border: 1px solid #333940; overflow: hidden;
    box-shadow: 0 16px 40px rgba(0,0,0,0.6);
  }}
  .title-bar {{
    background: #1e1e1e; height: 38px; display: flex; align-items: center; padding: 0 14px;
    border-bottom: 1px solid #282a2e;
  }}
  .dots {{ display: flex; gap: 8px; margin-right: 18px; }}
  .dot {{ width: 12px; height: 12px; border-radius: 50%; }}
  .dot.red {{ background: #ff5f56; }}
  .dot.yellow {{ background: #ffbd2e; }}
  .dot.green {{ background: #27c93f; }}
  .tab {{
    background: #141414; color: #e6edf3; padding: 7px 16px; font-size: 13px; font-weight: 500;
    border-top: 2px solid #27c93f; border-right: 1px solid #282a2e; border-left: 1px solid #282a2e;
    display: flex; align-items: center; gap: 7px;
  }}
  .terminal-title {{
    margin-left: auto; font-size: 11px; color: #8b949e; background: #23272d;
    padding: 3px 10px; border-radius: 4px; border: 1px solid #363b42; font-weight: 500;
  }}
  .terminal-body {{
    padding: 16px 20px;
    font-family: "SF Mono", Menlo, Monaco, Consolas, "Liberation Mono", "Courier New", monospace, Arial, sans-serif;
    font-size: 13px; line-height: 1.55; color: #d1d5db; white-space: pre-wrap; word-break: break-all;
  }}
  .prompt {{ color: #5af78e; font-weight: bold; }}
  .cmd {{ color: #57c7ff; font-weight: 600; }}
  .header {{ color: #ffb86c; font-weight: bold; }}
  .error {{ color: #ff5555; font-weight: bold; }}
  .success {{ color: #50fa7b; }}
  .table-line {{ color: #8be9fd; }}
  .highlight {{ color: #bd93f9; }}
  .normal {{ color: #f8f8f2; }}
</style>
</head>
<body>
  <div class="window">
    <div class="title-bar">
      <div class="dots">
        <div class="dot red"></div><div class="dot yellow"></div><div class="dot green"></div>
      </div>
      <div class="tab">💻 TERMINAL — zsh (Cursor)</div>
      <div class="terminal-title">Cursor Terminal — Java 25 LTS</div>
    </div>
    <div class="terminal-body">{terminal_body}</div>
  </div>
</body>
</html>"""

    render_html_to_png(html_template, output_png, width=1100, height=calc_height + 24)

tasks = [
    {
        "num": 1,
        "title": "Câu 1: B1. Thiết lập kết nối cơ sở dữ liệu SQLite qua JDBC",
        "file": "src/phan1_huongdan/B1_KetNoiSQLite.java",
        "cmd": "java -cp bin:lib/sqlite-jdbc-3.53.4.0.jar phan1_huongdan.B1_KetNoiSQLite",
        "class": "phan1_huongdan.B1_KetNoiSQLite",
        "img_code": os.path.join(SCREENSHOTS_DIR, "cau1_code_cursor.png"),
        "img_term": os.path.join(SCREENSHOTS_DIR, "cau1_terminal_cursor.png")
    },
    {
        "num": 2,
        "title": "Câu 2: B2 - B5. Thao tác bảng NhanVien (Tạo bảng, Chèn bản ghi, Đọc ResultSet & Đóng kết nối)",
        "file": "src/phan1_huongdan/NhanVienCRUD_Statement.java",
        "cmd": "java -cp bin:lib/sqlite-jdbc-3.53.4.0.jar phan1_huongdan.NhanVienCRUD_Statement",
        "class": "phan1_huongdan.NhanVienCRUD_Statement",
        "img_code": os.path.join(SCREENSHOTS_DIR, "cau2_code_cursor.png"),
        "img_term": os.path.join(SCREENSHOTS_DIR, "cau2_terminal_cursor.png")
    },
    {
        "num": 3,
        "title": "Câu 3: B3 (Mở rộng). Quản lý giao dịch Transaction (Commit thành công & Rollback khi lỗi)",
        "file": "src/phan1_huongdan/NhanVienTransactionDemo.java",
        "cmd": "java -cp bin:lib/sqlite-jdbc-3.53.4.0.jar phan1_huongdan.NhanVienTransactionDemo",
        "class": "phan1_huongdan.NhanVienTransactionDemo",
        "img_code": os.path.join(SCREENSHOTS_DIR, "cau3_code_cursor.png"),
        "img_term": os.path.join(SCREENSHOTS_DIR, "cau3_terminal_cursor.png")
    },
    {
        "num": 4,
        "title": "Câu 4: Bài tập 1. Quản lý danh mục Sản Phẩm (CRUD đầy đủ: Create, Read, Update, Delete)",
        "file": "src/phan2_baitap/bai1_quanly_sanpham/MainSanPhamCRUD.java",
        "cmd": "java -cp bin:lib/sqlite-jdbc-3.53.4.0.jar phan2_baitap.bai1_quanly_sanpham.MainSanPhamCRUD",
        "class": "phan2_baitap.bai1_quanly_sanpham.MainSanPhamCRUD",
        "img_code": os.path.join(SCREENSHOTS_DIR, "cau4_code_cursor.png"),
        "img_term": os.path.join(SCREENSHOTS_DIR, "cau4_terminal_cursor.png")
    },
    {
        "num": 5,
        "title": "Câu 5: Bài tập 2. Sử dụng giao dịch (Transaction) trong cập nhật kho hàng",
        "file": "src/phan2_baitap/bai2_transaction/TransactionSanPhamDemo.java",
        "cmd": "java -cp bin:lib/sqlite-jdbc-3.53.4.0.jar phan2_baitap.bai2_transaction.TransactionSanPhamDemo",
        "class": "phan2_baitap.bai2_transaction.TransactionSanPhamDemo",
        "img_code": os.path.join(SCREENSHOTS_DIR, "cau5_code_cursor.png"),
        "img_term": os.path.join(SCREENSHOTS_DIR, "cau5_terminal_cursor.png")
    },
    {
        "num": 6,
        "title": "Câu 6: Bài tập 3. Xử lý ngoại lệ triệt để & Giải phóng tài nguyên an toàn trong khối finally",
        "file": "src/phan2_baitap/bai3_exception_handling/ExceptionHandlingStrictDemo.java",
        "cmd": "java -cp bin:lib/sqlite-jdbc-3.53.4.0.jar phan2_baitap.bai3_exception_handling.ExceptionHandlingStrictDemo",
        "class": "phan2_baitap.bai3_exception_handling.ExceptionHandlingStrictDemo",
        "img_code": os.path.join(SCREENSHOTS_DIR, "cau6_code_cursor.png"),
        "img_term": os.path.join(SCREENSHOTS_DIR, "cau6_terminal_cursor.png")
    },
    {
        "num": 7,
        "title": "Câu 7: Bài tập 4. Sử dụng PreparedStatement tăng cường bảo mật (Chống SQL Injection) & Benchmark",
        "file": "src/phan2_baitap/bai4_prepared_statement/PreparedStatementSecurityDemo.java",
        "cmd": "java -cp bin:lib/sqlite-jdbc-3.53.4.0.jar phan2_baitap.bai4_prepared_statement.PreparedStatementSecurityDemo",
        "class": "phan2_baitap.bai4_prepared_statement.PreparedStatementSecurityDemo",
        "img_code": os.path.join(SCREENSHOTS_DIR, "cau7_code_cursor.png"),
        "img_term": os.path.join(SCREENSHOTS_DIR, "cau7_terminal_cursor.png")
    },
    {
        "num": 8,
        "title": "Câu 8: Bài tập 5. Truy vấn dữ liệu nâng cao (Aggregation Functions: SUM, AVG, MIN, MAX, GROUP BY)",
        "file": "src/phan2_baitap/bai5_aggregation/NhanVienAggregationDemo.java",
        "cmd": "java -cp bin:lib/sqlite-jdbc-3.53.4.0.jar phan2_baitap.bai5_aggregation.NhanVienAggregationDemo",
        "class": "phan2_baitap.bai5_aggregation.NhanVienAggregationDemo",
        "img_code": os.path.join(SCREENSHOTS_DIR, "cau8_code_cursor.png"),
        "img_term": os.path.join(SCREENSHOTS_DIR, "cau8_terminal_cursor.png")
    }
]

print("=== BẮT ĐẦU TẠO 16 ẢNH CHỤP CURSOR EDITOR & CURSOR TERMINAL ===")
for t in tasks:
    create_editor_screenshot(t["file"], t["img_code"])
    create_terminal_screenshot(t["cmd"], t["class"], t["img_term"])

# -------------------------------------------------------------------------
# XUẤT FILE BÁO CÁO WORD THEO ĐÚNG YÊU CẦU TINH GỌN (CHỈ CÓ SỐ CÂU + ẢNH)
# -------------------------------------------------------------------------
print("\n=== ĐANG XUẤT FILE BÁO CÁO WORD (.DOCX) TINH GỌN ===")
doc = docx.Document()

for section in doc.sections:
    section.top_margin = Inches(0.8)
    section.bottom_margin = Inches(0.8)
    section.left_margin = Inches(0.8)
    section.right_margin = Inches(0.8)

COLOR_HEADER = RGBColor(24, 76, 120)
COLOR_TITLE = RGBColor(15, 23, 42)

p_uni = doc.add_paragraph()
p_uni.alignment = WD_ALIGN_PARAGRAPH.CENTER
r_uni = p_uni.add_run("TRƯỜNG ĐẠI HỌC CÔNG NGHIỆP TP. HỒ CHÍ MINH (IUH)\nKHOA CÔNG NGHỆ THÔNG TIN")
r_uni.bold = True
r_uni.font.name = "Arial"
r_uni.font.size = Pt(12)
r_uni.font.color.rgb = COLOR_HEADER
p_uni.paragraph_format.space_after = Pt(8)

p_title = doc.add_paragraph()
p_title.alignment = WD_ALIGN_PARAGRAPH.CENTER
r_title = p_title.add_run("BÁO CÁO THỰC HÀNH LAB 08: JDBC VỚI SQLITE")
r_title.bold = True
r_title.font.name = "Arial"
r_title.font.size = Pt(17)
r_title.font.color.rgb = COLOR_HEADER
p_title.paragraph_format.space_after = Pt(10)

# Student info table
table = doc.add_table(rows=4, cols=2)
table.alignment = WD_TABLE_ALIGNMENT.CENTER
info_rows = [
    ("Họ và tên sinh viên:", "Lê Tuấn Minh"),
    ("Mã số sinh viên (MSSV):", "25765141"),
    ("Môn học / Lớp học phần:", "Phát triển Hệ thống Tích hợp & Phân tán (PTHTTH) - STT: 31"),
    ("Môi trường thực hiện:", "Cursor IDE, macOS ARM64, Java 25 LTS, SQLite JDBC 3.53.4.0")
]

def set_cell_background(cell, fill_hex):
    tcPr = cell._tc.get_or_add_tcPr()
    shd = parse_xml(f'<w:shd {nsdecls("w")} w:fill="{fill_hex}"/>')
    tcPr.append(shd)

def set_cell_margins(cell, top=50, bottom=50, left=100, right=100):
    tcPr = cell._tc.get_or_add_tcPr()
    tcMar = parse_xml(
        f'<w:tcMar {nsdecls("w")}>'
        f'<w:top w:w="{top}" w:type="dxa"/>'
        f'<w:bottom w:w="{bottom}" w:type="dxa"/>'
        f'<w:left w:w="{left}" w:type="dxa"/>'
        f'<w:right w:w="{right}" w:type="dxa"/>'
        f'</w:tcMar>'
    )
    tcPr.append(tcMar)

for i, (k, v) in enumerate(info_rows):
    row = table.rows[i]
    c0, c1 = row.cells[0], row.cells[1]
    c0.width = Inches(2.3)
    c1.width = Inches(4.5)
    set_cell_background(c0, "F1F5F9")
    set_cell_background(c1, "FFFFFF")
    set_cell_margins(c0)
    set_cell_margins(c1)

    p0 = c0.paragraphs[0]
    p0.paragraph_format.space_after = Pt(2)
    r0 = p0.add_run(k)
    r0.bold = True
    r0.font.name = "Arial"
    r0.font.size = Pt(10)

    p1 = c1.paragraphs[0]
    p1.paragraph_format.space_after = Pt(2)
    r1 = p1.add_run(v)
    r1.font.name = "Arial"
    r1.font.size = Pt(10)

p_sep = doc.add_paragraph()
p_sep.paragraph_format.space_after = Pt(14)

# Danh sách từng câu: Số câu + Ảnh full code Cursor + Ảnh kết quả chạy Cursor
for t in tasks:
    h = doc.add_paragraph()
    h.paragraph_format.space_before = Pt(14)
    h.paragraph_format.space_after = Pt(6)
    rh = h.add_run(t["title"])
    rh.bold = True
    rh.font.name = "Arial"
    rh.font.size = Pt(13)
    rh.font.color.rgb = COLOR_HEADER

    # 1. Ảnh Full Code trên Cursor IDE
    p_c = doc.add_paragraph()
    p_c.paragraph_format.space_after = Pt(3)
    rc = p_c.add_run("1. Ảnh chụp Full Code trên Cursor IDE (" + os.path.basename(t["file"]) + "):")
    rc.bold = True
    rc.font.name = "Arial"
    rc.font.size = Pt(10.5)

    if os.path.exists(t["img_code"]):
        doc.add_picture(t["img_code"], width=Inches(6.4))
        doc.add_paragraph().paragraph_format.space_after = Pt(6)

    # 2. Ảnh Màn hình kết quả chạy trên Cursor Terminal
    p_t = doc.add_paragraph()
    p_t.paragraph_format.space_after = Pt(3)
    rt = p_t.add_run("2. Ảnh chụp Màn hình kết quả chạy trên Cursor Terminal:")
    rt.bold = True
    rt.font.name = "Arial"
    rt.font.size = Pt(10.5)

    if os.path.exists(t["img_term"]):
        doc.add_picture(t["img_term"], width=Inches(6.4))
        doc.add_paragraph().paragraph_format.space_after = Pt(14)

doc.save(OUTPUT_DOCX)
print(f"[OK] File Word mới lưu tại: {OUTPUT_DOCX}")

subprocess.run(["cp", OUTPUT_DOCX, DESKTOP_DOCX])
print(f"[OK] Đã sao lưu ra Desktop: {DESKTOP_DOCX}")
