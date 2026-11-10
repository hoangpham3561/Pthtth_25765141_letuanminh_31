package phan2_baitap.bai4_prepared_statement;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Bài tập 4: Sử dụng PreparedStatement để tăng cường bảo mật và hiệu năng
 * - Ngăn chặn triệt để SQL Injection so với Statement truyền thống
 * - Tối ưu hiệu năng bằng Pre-compilation khi thực thi lặp lại
 */
public class PreparedStatementSecurityDemo {
    private static final String DB_URL = "jdbc:sqlite:sanpham.db";

    public static void main(String[] args) {
        System.out.println("========================================================================");
        System.out.println(" BÀI TẬP 4: SO SÁNH STATEMENT VS PREPAREDSTATEMENT (BẢO MẬT & HIỆU NĂNG)");
        System.out.println("========================================================================");

        // Chuỗi tấn công SQL Injection điển hình
        String inputDocHai = "' OR '1'='1";
        System.out.println("\n[ĐẦU VÀO TÌM KIẾM CỦA NGƯỜI DÙNG]: " + inputDocHai);

        // PHẦN A: BẢO MẬT - SQL INJECTION
        System.out.println("\n--- 1. STATEMENT THƯỜNG (DỄ BỊ TẤN CÔNG SQL INJECTION) ---");
        demoStatementSQLInjection(inputDocHai);

        System.out.println("\n--- 2. PREPAREDSTATEMENT (BẢO MẬT TUYỆT ĐỐI VỚI THAM SỐ HÓA '?') ---");
        demoPreparedStatementAnToan(inputDocHai);

        // PHẦN B: HIỆU NĂNG - TỐC ĐỘ THỰC THI NHIỀU BẢN GHI
        System.out.println("\n--- 3. ĐO LƯỜNG HIỆU NĂNG (STATEMENT VS PREPAREDSTATEMENT BATCH) ---");
        demoHieuNang();
    }

    private static void demoStatementSQLInjection(String input) {
        // Nối chuỗi SQL thô sơ -> Tạo lỗ hổng SQL Injection
        String sql = "SELECT maSP, tenSP, gia FROM SanPham WHERE maSP = '" + input + "'";
        System.out.println("Câu SQL thực thi: " + sql);

        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            int count = 0;
            while (rs.next()) {
                count++;
                System.out.println("   [BỊ LỘ DỮ LIỆU " + count + "]: " + rs.getString("maSP") + " - " + rs.getString("tenSP"));
            }
            if (count > 0) {
                System.out.println("=> NGUY CƠ: Toàn bộ bảng dữ liệu bị trích xuất trái phép!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void demoPreparedStatementAnToan(String input) {
        // Dùng dấu hỏi chấm ? để tham số hóa, Driver tự escape ký tự đặc biệt
        String sql = "SELECT maSP, tenSP, gia FROM SanPham WHERE maSP = ?";
        System.out.println("Câu SQL có tham số: " + sql);

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, input);
            try (ResultSet rs = pstmt.executeQuery()) {
                int count = 0;
                while (rs.next()) {
                    count++;
                    System.out.println("   Dữ liệu tìm được: " + rs.getString("maSP"));
                }
                if (count == 0) {
                    System.out.println("=> AN TOÀN TUYỆT ĐỐI: Hệ thống nhận dạng '" + input + "' là một chuỗi mã SP thông thường, không tìm thấy kết quả nào!");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void demoHieuNang() {
        int soLanLap = 500;
        String dbMem = "jdbc:sqlite::memory:";

        try (Connection conn = DriverManager.getConnection(dbMem);
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("CREATE TABLE Bench (id INT, val TEXT);");

            // Đo Statement thường
            long t1 = System.currentTimeMillis();
            for (int i = 0; i < soLanLap; i++) {
                stmt.executeUpdate("INSERT INTO Bench VALUES (" + i + ", 'GiaTri_" + i + "');");
            }
            long timeStatement = System.currentTimeMillis() - t1;

            stmt.executeUpdate("DELETE FROM Bench;");

            // Đo PreparedStatement
            long t2 = System.currentTimeMillis();
            String sqlPrep = "INSERT INTO Bench VALUES (?, ?);";
            try (PreparedStatement pstmt = conn.prepareStatement(sqlPrep)) {
                for (int i = 0; i < soLanLap; i++) {
                    pstmt.setInt(1, i);
                    pstmt.setString(2, "GiaTri_" + i);
                    pstmt.executeUpdate();
                }
            }
            long timePreparedStatement = System.currentTimeMillis() - t2;

            System.out.println("-> Thời gian chèn " + soLanLap + " dòng bằng Statement thường: " + timeStatement + " ms");
            System.out.println("-> Thời gian chèn " + soLanLap + " dòng bằng PreparedStatement:  " + timePreparedStatement + " ms");
            System.out.println("=> PreparedStatement tái sử dụng Execution Plan, tối ưu hóa bộ phân tích cú pháp của DBMS.");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
