# LAB 08: JDBC (JAVA DATABASE CONNECTIVITY) VỚI SQLITE
- **Môn học**: Phát triển Hệ thống Tích hợp & Phân tán (PTHTTH)
- **Sinh viên**: Lê Tuấn Minh
- **MSSV**: 25765141
- **Lớp / STT**: DHKTPM / STT: 31
- **Tài nguyên sử dụng**: `LAB_08_JDBC.pdf`, `sqlite-jdbc-3.53.4.0.jar`

---

## 1. Cấu Trúc Dự Án

```text
lab8_jdbc/
├── lib/
│   └── sqlite-jdbc-3.53.4.0.jar             # Driver JDBC cho SQLite (3.53.4.0)
├── src/
│   ├── phan1_huongdan/
│   │   ├── B1_KetNoiSQLite.java             # B1: Tải Driver & mở kết nối CSDL
│   │   ├── NhanVienCRUD_Statement.java      # B2-B5: Tạo bảng, INSERT 4 NV, SELECT ResultSet, Close
│   │   └── NhanVienTransactionDemo.java     # Quản lý giao dịch (AutoCommit=false, Commit, Rollback)
│   └── phan2_baitap/
│       ├── bai1_quanly_sanpham/
│       │   ├── SanPham.java                 # Entity SanPham (maSP, tenSP, gia, soluong)
│       │   ├── SanPhamDAO.java              # DAO Pattern: CRUD dữ liệu
│       │   └── MainSanPhamCRUD.java         # Test harness toàn bộ Create/Read/Update/Delete
│       ├── bai2_transaction/
│       │   └── TransactionSanPhamDemo.java  # Giao dịch điều chuyển kho hàng (Commit / Rollback)
│       ├── bai3_exception_handling/
│       │   └── ExceptionHandlingStrictDemo.java # Try-catch-finally đóng tài nguyên chống rò rỉ
│       ├── bai4_prepared_statement/
│       │   └── PreparedStatementSecurityDemo.java # PreparedStatement vs SQL Injection & Benchmark
│       └── bai5_aggregation/
│           └── NhanVienAggregationDemo.java # Hàm tổng hợp SUM, AVG, MIN, MAX, COUNT & GROUP BY
├── screenshots/                             # 8 ảnh chụp màn hình terminal thực nghiệm
│   ├── screenshot_01_ketnoi_sqlite.png
│   ├── screenshot_02_nhanvien_crud.png
│   ├── screenshot_03_nhanvien_transaction.png
│   ├── screenshot_04_sanpham_crud.png
│   ├── screenshot_05_sanpham_transaction.png
│   ├── screenshot_06_exception_handling.png
│   ├── screenshot_07_prepared_statement.png
│   └── screenshot_08_aggregation.png
├── run_all.sh                               # Script tự động biên dịch & chạy toàn bộ 8 bài
├── create_report_docx.py                    # Script tự động xuất file báo cáo Word
├── generate_screenshots.py                 # Script tự động tạo ảnh chụp màn hình terminal
├── Bao_Cao_Lab_08_JDBC_25765141_LeTuanMinh.docx # Báo cáo Word hoàn chỉnh kèm ảnh chụp
└── README.md
```

---

## 2. Hướng Dẫn Biên Dịch & Chạy

### Cách 1: Chạy tự động tất cả các bài bằng script
```bash
chmod +x run_all.sh
./run_all.sh
```

### Cách 2: Biên dịch thủ công
```bash
mkdir -p bin
javac -encoding UTF-8 -cp "lib/sqlite-jdbc-3.53.4.0.jar" -d bin $(find src -name "*.java")
```

### Cách 3: Chạy từng bài riêng lẻ
```bash
# Phần 1: B1 Kết nối SQLite
java --enable-native-access=ALL-UNNAMED -cp "bin:lib/sqlite-jdbc-3.53.4.0.jar" phan1_huongdan.B1_KetNoiSQLite

# Phần 1: B2->B5 NhanVien CRUD & Đóng tài nguyên
java --enable-native-access=ALL-UNNAMED -cp "bin:lib/sqlite-jdbc-3.53.4.0.jar" phan1_huongdan.NhanVienCRUD_Statement

# Phần 1 Mở Rộng: Transaction Commit & Rollback
java --enable-native-access=ALL-UNNAMED -cp "bin:lib/sqlite-jdbc-3.53.4.0.jar" phan1_huongdan.NhanVienTransactionDemo

# Phần 2 - Bài 1: Quản lý danh mục Sản Phẩm CRUD (DAO)
java --enable-native-access=ALL-UNNAMED -cp "bin:lib/sqlite-jdbc-3.53.4.0.jar" phan2_baitap.bai1_quanly_sanpham.MainSanPhamCRUD

# Phần 2 - Bài 2: Giao dịch điều chuyển kho hàng
java --enable-native-access=ALL-UNNAMED -cp "bin:lib/sqlite-jdbc-3.53.4.0.jar" phan2_baitap.bai2_transaction.TransactionSanPhamDemo

# Phần 2 - Bài 3: Exception Handling nghiêm ngặt trong finally
java --enable-native-access=ALL-UNNAMED -cp "bin:lib/sqlite-jdbc-3.53.4.0.jar" phan2_baitap.bai3_exception_handling.ExceptionHandlingStrictDemo

# Phần 2 - Bài 4: PreparedStatement vs SQL Injection
java --enable-native-access=ALL-UNNAMED -cp "bin:lib/sqlite-jdbc-3.53.4.0.jar" phan2_baitap.bai4_prepared_statement.PreparedStatementSecurityDemo

# Phần 2 - Bài 5: Truy vấn nâng cao Aggregation (SUM, AVG, MIN, MAX, GROUP BY)
java --enable-native-access=ALL-UNNAMED -cp "bin:lib/sqlite-jdbc-3.53.4.0.jar" phan2_baitap.bai5_aggregation.NhanVienAggregationDemo
```

---

## 3. Báo Cáo Thực Hành (Word Document)
- File báo cáo đầy đủ: `Bao_Cao_Lab_08_JDBC_25765141_LeTuanMinh.docx`
- Đã nhúng toàn bộ 8 hình ảnh chụp màn hình chạy code thành công.
- Trình bày mục tiêu, lý thuyết, đoạn mã trọng tâm, bảng tổng kết và phân tích kết quả thực nghiệm.
