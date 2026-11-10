package phan2_baitap.bai3_exception_handling;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Bài tập 3: Xử lý ngoại lệ (Exception Handling) triệt để trong JDBC
 * Đảm bảo mọi tài nguyên Connection, Statement, ResultSet luôn được đóng an toàn trong khối finally,
 * ngăn chặn hoàn toàn rò rỉ tài nguyên (Resource Leakage) kể cả khi phát sinh ngoại lệ runtime hoặc SQLException.
 */
public class ExceptionHandlingStrictDemo {
    private static final String DB_URL = "jdbc:sqlite:sanpham.db";

    public static void main(String[] args) {
        System.out.println("========================================================================");
        System.out.println(" BÀI TẬP 3: XỬ LÝ NGOẠI LỆ TRIỆT ĐỂ (STRICT EXCEPTION HANDLING & CLEANUP)");
        System.out.println("========================================================================");

        System.out.println("\n--- TEST CASE 1: TRUY VẤN BÌNH THƯỜNG VÀ ĐÓNG TÀI NGUYÊN AN TOÀN ---");
        thucThiTruyVan("SELECT * FROM SanPham LIMIT 2;");

        System.out.println("\n--- TEST CASE 2: TRUY VẤN BẢNG KHÔNG TỒN TẠI (PHÁT SINH LỖI SQL) ---");
        thucThiTruyVan("SELECT * FROM BangKhongTonTai;");
    }

    private static void thucThiTruyVan(String sql) {
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            Class.forName("org.sqlite.JDBC");
            System.out.println("[1] Mở kết nối Connection...");
            conn = DriverManager.getConnection(DB_URL);

            System.out.println("[2] Tạo đối tượng Statement...");
            stmt = conn.createStatement();

            System.out.println("[3] Thực thi câu lệnh SQL: " + sql);
            rs = stmt.executeQuery(sql);

            System.out.println("[4] Duyệt dữ liệu từ ResultSet:");
            while (rs.next()) {
                System.out.println("    -> SP: " + rs.getString(1) + " | " + rs.getString(2));
            }
            System.out.println("[5] Xử lý câu lệnh SQL thành công!");

        } catch (ClassNotFoundException e) {
            System.err.println("[CATCH] Không tìm thấy JDBC Driver: " + e.getMessage());
        } catch (SQLException e) {
            System.err.println("[CATCH] Bắt lỗi SQLException (" + e.getErrorCode() + "): " + e.getMessage());
        } finally {
            // Khối FINALLY LUÔN ĐƯỢC THỰC THI dù có exception hay không
            System.out.println("[FINALLY] Bắt đầu dọn dẹp và giải phóng tài nguyên theo thứ tự ngược lại:");

            // 1. Đóng ResultSet
            if (rs != null) {
                try {
                    rs.close();
                    System.out.println("   [+] rs.close() -> Thành công (ResultSet đã đóng)");
                } catch (SQLException e) {
                    System.err.println("   [-] Lỗi khi đóng ResultSet: " + e.getMessage());
                }
            } else {
                System.out.println("   [*] rs là null (không cần đóng)");
            }

            // 2. Đóng Statement
            if (stmt != null) {
                try {
                    stmt.close();
                    System.out.println("   [+] stmt.close() -> Thành công (Statement đã đóng)");
                } catch (SQLException e) {
                    System.err.println("   [-] Lỗi khi đóng Statement: " + e.getMessage());
                }
            } else {
                System.out.println("   [*] stmt là null (không cần đóng)");
            }

            // 3. Đóng Connection
            if (conn != null) {
                try {
                    conn.close();
                    System.out.println("   [+] conn.close() -> Thành công (Connection đã đóng)");
                } catch (SQLException e) {
                    System.err.println("   [-] Lỗi khi đóng Connection: " + e.getMessage());
                }
            } else {
                System.out.println("   [*] conn là null (không cần đóng)");
            }

            System.out.println("[FINALLY] Toàn bộ tài nguyên đã được giải phóng triệt để. Không rò rỉ bộ nhớ/kết nối!");
        }
    }
}
