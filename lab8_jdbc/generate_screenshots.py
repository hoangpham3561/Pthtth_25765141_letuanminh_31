import os
import subprocess
from PIL import Image, ImageDraw, ImageFont

BASE_DIR = "/Users/min/Desktop/IUH-k2/PTHTTH/Pthtth_25765141_letuanminh_31/lab8_jdbc"
BIN_DIR = os.path.join(BASE_DIR, "bin")
JAR_PATH = os.path.join(BASE_DIR, "lib/sqlite-jdbc-3.53.4.0.jar")
SCREENSHOTS_DIR = os.path.join(BASE_DIR, "screenshots")
os.makedirs(SCREENSHOTS_DIR, exist_ok=True)

FONT_PATH = "/System/Library/Fonts/Menlo.ttc"
FONT_SIZE = 14
LINE_HEIGHT = 22
PADDING_X = 24
PADDING_Y = 16
TITLE_BAR_HEIGHT = 36

def render_terminal_image(title, command, output_lines, output_file):
    font = ImageFont.truetype(FONT_PATH, FONT_SIZE)
    bold_font = ImageFont.truetype(FONT_PATH, FONT_SIZE)
    title_font = ImageFont.truetype(FONT_PATH, 12)

    all_display_lines = []
    all_display_lines.append(("$ " + command, (80, 250, 123), True)) # Green prompt
    all_display_lines.append(("", (255, 255, 255), False))

    for line in output_lines:
        line_clean = line.rstrip()
        color = (205, 214, 244) # Default light
        is_bold = False

        if "===" in line or "---" in line or "[1/" in line or "BÀI TẬP" in line or "TEST CASE" in line:
            color = (249, 226, 175) # Warm Yellow
            is_bold = True
        elif "LỖI" in line or "thất bại" in line.lower() or "bị lộ" in line.lower() or "nguy cơ" in line.lower():
            color = (243, 139, 168) # Red/Coral
            is_bold = True
        elif "thành công" in line.lower() or "commit" in line.lower() or "kết nối" in line.lower() or "an toàn" in line.lower():
            color = (166, 227, 161) # Soft Green
        elif "|" in line:
            color = (137, 180, 250) # Light blue table
        elif "->" in line or "[+]" in line or "[*]" in line:
            color = (148, 226, 213) # Cyan
        
        all_display_lines.append((line_clean, color, is_bold))

    width = 1100
    height = TITLE_BAR_HEIGHT + PADDING_Y * 2 + len(all_display_lines) * LINE_HEIGHT

    img = Image.new("RGBA", (width, height), (30, 30, 46, 255))
    draw = ImageDraw.Draw(img)

    # Title bar
    draw.rectangle([0, 0, width, TITLE_BAR_HEIGHT], fill=(24, 24, 37, 255))
    # Window buttons
    draw.ellipse([14, 12, 26, 24], fill=(255, 95, 86))   # Red
    draw.ellipse([34, 12, 46, 24], fill=(255, 189, 46))  # Yellow
    draw.ellipse([54, 12, 66, 24], fill=(39, 201, 63))   # Green

    # Title text
    full_title = f"min@macbook: ~/Pthtth_25765141_letuanminh_31/lab8_jdbc — {title}"
    bbox = font.getbbox(full_title)
    title_w = bbox[2] - bbox[0]
    draw.text(((width - title_w) // 2, 10), full_title, font=title_font, fill=(166, 173, 200))

    # Terminal body text
    y = TITLE_BAR_HEIGHT + PADDING_Y
    for text, color, is_bold in all_display_lines:
        cur_font = bold_font if is_bold else font
        draw.text((PADDING_X, y), text, font=cur_font, fill=color)
        y += LINE_HEIGHT

    # Border
    draw.rectangle([0, 0, width - 1, height - 1], outline=(69, 71, 90), width=1)

    img.save(output_file, "PNG")
    print(f"[OK] Generated screenshot: {output_file}")

tasks = [
    {
        "title": "B1 - Kết nối cơ sở dữ liệu SQLite",
        "cmd": "java -cp bin:lib/sqlite-jdbc-3.53.4.0.jar phan1_huongdan.B1_KetNoiSQLite",
        "class": "phan1_huongdan.B1_KetNoiSQLite",
        "output_file": os.path.join(SCREENSHOTS_DIR, "screenshot_01_ketnoi_sqlite.png")
    },
    {
        "title": "B2 -> B5 - CRUD NhanVien & Đóng tài nguyên",
        "cmd": "java -cp bin:lib/sqlite-jdbc-3.53.4.0.jar phan1_huongdan.NhanVienCRUD_Statement",
        "class": "phan1_huongdan.NhanVienCRUD_Statement",
        "output_file": os.path.join(SCREENSHOTS_DIR, "screenshot_02_nhanvien_crud.png")
    },
    {
        "title": "Phần 1 Mở Rộng - Quản lý Giao dịch (Transaction)",
        "cmd": "java -cp bin:lib/sqlite-jdbc-3.53.4.0.jar phan1_huongdan.NhanVienTransactionDemo",
        "class": "phan1_huongdan.NhanVienTransactionDemo",
        "output_file": os.path.join(SCREENSHOTS_DIR, "screenshot_03_nhanvien_transaction.png")
    },
    {
        "title": "Bài tập 1 - Quản lý Danh mục Sản phẩm (CRUD)",
        "cmd": "java -cp bin:lib/sqlite-jdbc-3.53.4.0.jar phan2_baitap.bai1_quanly_sanpham.MainSanPhamCRUD",
        "class": "phan2_baitap.bai1_quanly_sanpham.MainSanPhamCRUD",
        "output_file": os.path.join(SCREENSHOTS_DIR, "screenshot_04_sanpham_crud.png")
    },
    {
        "title": "Bài tập 2 - Transaction trong chuyển kho",
        "cmd": "java -cp bin:lib/sqlite-jdbc-3.53.4.0.jar phan2_baitap.bai2_transaction.TransactionSanPhamDemo",
        "class": "phan2_baitap.bai2_transaction.TransactionSanPhamDemo",
        "output_file": os.path.join(SCREENSHOTS_DIR, "screenshot_05_sanpham_transaction.png")
    },
    {
        "title": "Bài tập 3 - Strict Exception Handling & Resource Cleanup",
        "cmd": "java -cp bin:lib/sqlite-jdbc-3.53.4.0.jar phan2_baitap.bai3_exception_handling.ExceptionHandlingStrictDemo",
        "class": "phan2_baitap.bai3_exception_handling.ExceptionHandlingStrictDemo",
        "output_file": os.path.join(SCREENSHOTS_DIR, "screenshot_06_exception_handling.png")
    },
    {
        "title": "Bài tập 4 - PreparedStatement vs SQL Injection",
        "cmd": "java -cp bin:lib/sqlite-jdbc-3.53.4.0.jar phan2_baitap.bai4_prepared_statement.PreparedStatementSecurityDemo",
        "class": "phan2_baitap.bai4_prepared_statement.PreparedStatementSecurityDemo",
        "output_file": os.path.join(SCREENSHOTS_DIR, "screenshot_07_prepared_statement.png")
    },
    {
        "title": "Bài tập 5 - Aggregation Functions (SUM, AVG, MIN, MAX, GROUP BY)",
        "cmd": "java -cp bin:lib/sqlite-jdbc-3.53.4.0.jar phan2_baitap.bai5_aggregation.NhanVienAggregationDemo",
        "class": "phan2_baitap.bai5_aggregation.NhanVienAggregationDemo",
        "output_file": os.path.join(SCREENSHOTS_DIR, "screenshot_08_aggregation.png")
    }
]

for t in tasks:
    res = subprocess.run(
        ["java", "--enable-native-access=ALL-UNNAMED", "-cp", f"{BIN_DIR}:{JAR_PATH}", t["class"]],
        cwd=BASE_DIR,
        capture_output=True,
        text=True
    )
    lines = (res.stdout + res.stderr).splitlines()
    render_terminal_image(t["title"], t["cmd"], lines, t["output_file"])

print("\nToàn bộ 8 ảnh chụp màn hình đã được tạo thành công!")
