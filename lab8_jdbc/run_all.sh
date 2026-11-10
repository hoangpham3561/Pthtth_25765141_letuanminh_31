#!/bin/bash
# ==============================================================================
# Script tự động biên dịch và chạy kiểm thử toàn bộ Lab 08 JDBC
# Sinh viên: Lê Tuấn Minh - MSSV: 25765141
# ==============================================================================

set -e

DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$DIR"

JAR="lib/sqlite-jdbc-3.53.4.0.jar"
BIN="bin"

echo "========================================================================"
echo " [1/7] BIÊN DỊCH TOÀN BỘ MÃ NGUỒN JAVA"
echo "========================================================================"
mkdir -p "$BIN"
javac -encoding UTF-8 -cp "$JAR" -d "$BIN" $(find src -name "*.java")
echo "-> Biên dịch thành công 100%!"

run_java() {
    local class_name="$1"
    java --enable-native-access=ALL-UNNAMED -cp "$BIN:$JAR" "$class_name"
}

echo ""
echo "========================================================================"
echo " [2/7] PHẦN 1 - B1: KẾT NỐI CƠ SỞ DỮ LIỆU SQLITE"
echo "========================================================================"
run_java "phan1_huongdan.B1_KetNoiSQLite"

echo ""
echo "========================================================================"
echo " [3/7] PHẦN 1 - B2 -> B5: TẠO BẢNG, INSERT, QUERY VÀ ĐÓNG TÀI NGUYÊN"
echo "========================================================================"
run_java "phan1_huongdan.NhanVienCRUD_Statement"

echo ""
echo "========================================================================"
echo " [4/7] PHẦN 1 MỞ RỘNG: QUẢN LÝ GIAO DỊCH (TRANSACTION COMMIT / ROLLBACK)"
echo "========================================================================"
run_java "phan1_huongdan.NhanVienTransactionDemo"

echo ""
echo "========================================================================"
echo " [5/7] PHẦN 2 - BÀI 1: QUẢN LÝ DANH MỤC SẢN PHẨM (CRUD HOÀN CHỈNH)"
echo "========================================================================"
run_java "phan2_baitap.bai1_quanly_sanpham.MainSanPhamCRUD"

echo ""
echo "========================================================================"
echo " [6/7] PHẦN 2 - BÀI 2: TRANSACTION TRONG ĐIỀU CHUYỂN KHO SẢN PHẨM"
echo "========================================================================"
run_java "phan2_baitap.bai2_transaction.TransactionSanPhamDemo"

echo ""
echo "========================================================================"
echo " [7/7] PHẦN 2 - BÀI 3: STRICT EXCEPTION HANDLING VÀ CLEANUP TÀI NGUYÊN"
echo "========================================================================"
run_java "phan2_baitap.bai3_exception_handling.ExceptionHandlingStrictDemo"

echo ""
echo "========================================================================"
echo " [BONUS] PHẦN 2 - BÀI 4: PREPAREDSTATEMENT VS STATEMENT (SQL INJECTION)"
echo "========================================================================"
run_java "phan2_baitap.bai4_prepared_statement.PreparedStatementSecurityDemo"

echo ""
echo "========================================================================"
echo " [BONUS] PHẦN 2 - BÀI 5: TRUY VẤN NÂNG CAO VỚI AGGREGATION FUNCTIONS"
echo "========================================================================"
run_java "phan2_baitap.bai5_aggregation.NhanVienAggregationDemo"

echo ""
echo "========================================================================"
echo " TOÀN BỘ 7/7 PHẦN ĐÃ HOÀN TẤT THÀNH CÔNG RỰC RỠ!"
echo "========================================================================"
