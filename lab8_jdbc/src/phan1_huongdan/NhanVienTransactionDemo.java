package phan1_huongdan;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Phần 1 mở rộng: Demo Quản lý Giao dịch (Transaction Management) với SQLite
 * - conn.setAutoCommit(false)
 * - conn.commit()
 * - conn.rollback() trong khối catch
 */
public class NhanVienTransactionDemo {
    private static final String DB_URL = "jdbc:sqlite:nhanvien.db";

    public static void main(String[] args) {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return;
        }

        System.out.println("==========================================================");
        System.out.println("TEST CASE 1: TRANSACTION THÀNH CÔNG (TẤT CẢ COMMIT)");
        System.out.println("==========================================================");
        testTransactionThanhCong();

        System.out.println("\n==========================================================");
        System.out.println("TEST CASE 2: TRANSACTION THẤT BẠI (PHÁT SINH LỖI VÀ ROLLBACK)");
        System.out.println("==========================================================");
        testTransactionThatBaiRollback();

        System.out.println("\n==========================================================");
        System.out.println("KIỂM TRA DỮ LIỆU CUỐI CÙNG SAU CÁC GIAO DỊCH");
        System.out.println("==========================================================");
        inDanhSachNhanVien();
    }

    private static void testTransactionThanhCong() {
        Connection conn = null;
        Statement stmt = null;
        try {
            conn = DriverManager.getConnection(DB_URL);
            // 1. Tắt chế độ tự động commit
            conn.setAutoCommit(false);
            System.out.println("[Tx1] conn.setAutoCommit(false) - Bắt đầu giao dịch.");

            stmt = conn.createStatement();
            stmt.executeUpdate("INSERT INTO NhanVien(id, ten, chuc_vu) VALUES (10, 'Hoang Thi Mai', 'Ke Toan Truong');");
            System.out.println("[Tx1] Đã thực thi thao tác 1: Thêm nhân viên 10.");

            stmt.executeUpdate("INSERT INTO NhanVien(id, ten, chuc_vu) VALUES (11, 'Dang Quoc Bao', 'Nhan Vien IT');");
            System.out.println("[Tx1] Đã thực thi thao tác 2: Thêm nhân viên 11.");

            // 2. Commit nếu tất cả thành công
            conn.commit();
            System.out.println("[Tx1] Tất cả thao tác thành công -> Đã gọi conn.commit()!");
        } catch (SQLException e) {
            System.err.println("[Tx1 LỖI] Giao dịch thất bại: " + e.getMessage());
            if (conn != null) {
                try {
                    conn.rollback();
                    System.err.println("[Tx1] Đã gọi conn.rollback() để hoàn tác.");
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        } finally {
            dongTaiNguyen(null, stmt, conn);
        }
    }

    private static void testTransactionThatBaiRollback() {
        Connection conn = null;
        Statement stmt = null;
        try {
            conn = DriverManager.getConnection(DB_URL);
            conn.setAutoCommit(false);
            System.out.println("[Tx2] conn.setAutoCommit(false) - Bắt đầu giao dịch 2.");

            stmt = conn.createStatement();
            // Thao tác 1 hợp lệ
            stmt.executeUpdate("INSERT INTO NhanVien(id, ten, chuc_vu) VALUES (20, 'Vu Duc Tri', 'Marketing');");
            System.out.println("[Tx2] Đã thực thi thao tác 1 (id=20 hợp lệ).");

            // Thao tác 2 cố tình gây lỗi vi phạm khóa chính (Primary Key id=10 đã tồn tại)
            System.out.println("[Tx2] Cố tình chèn id=10 trùng khóa chính để kích hoạt lỗi SQL...");
            stmt.executeUpdate("INSERT INTO NhanVien(id, ten, chuc_vu) VALUES (10, 'Nhan Vien Loi', 'Tester');");

            conn.commit();
        } catch (SQLException e) {
            System.err.println("[Tx2 LỖI BẮT ĐƯỢC]: " + e.getMessage());
            if (conn != null) {
                try {
                    conn.rollback();
                    System.out.println("[Tx2 ROLLBACK] Đã kích hoạt conn.rollback() thành công! Thao tác 1 (id=20) bị hủy bỏ hoàn toàn.");
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        } finally {
            dongTaiNguyen(null, stmt, conn);
        }
    }

    private static void inDanhSachNhanVien() {
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            conn = DriverManager.getConnection(DB_URL);
            stmt = conn.createStatement();
            rs = stmt.executeQuery("SELECT * FROM NhanVien ORDER BY id ASC;");
            System.out.printf("%-5s | %-25s | %-25s%n", "ID", "Tên", "Chức Vụ");
            System.out.println("-----------------------------------------------------------------");
            while (rs.next()) {
                System.out.printf("%-5d | %-25s | %-25s%n", rs.getInt("id"), rs.getString("ten"), rs.getString("chuc_vu"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            dongTaiNguyen(rs, stmt, conn);
        }
    }

    private static void dongTaiNguyen(ResultSet rs, Statement stmt, Connection conn) {
        if (rs != null) {
            try { rs.close(); } catch (SQLException ignored) {}
        }
        if (stmt != null) {
            try { stmt.close(); } catch (SQLException ignored) {}
        }
        if (conn != null) {
            try {
                conn.setAutoCommit(true);
                conn.close();
            } catch (SQLException ignored) {}
        }
    }
}
