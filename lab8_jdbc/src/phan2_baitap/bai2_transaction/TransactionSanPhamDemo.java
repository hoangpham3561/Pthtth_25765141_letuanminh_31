package phan2_baitap.bai2_transaction;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Bài tập 2: Sử dụng giao dịch (Transaction) trong cập nhật dữ liệu
 * Minh họa tính ACID (Atomicity) khi cập nhật nhiều bản ghi đồng thời.
 */
public class TransactionSanPhamDemo {
    private static final String DB_URL = "jdbc:sqlite:sanpham_tx.db";

    public static void main(String[] args) {
        initDatabase();

        System.out.println("========================================================================");
        System.out.println(" BÀI TẬP 2: QUẢN LÝ GIAO DỊCH (TRANSACTION) TRONG JDBC");
        System.out.println("========================================================================");

        System.out.println("\n[TRẠNG THÁI BAN ĐẦU CỦA KHO HÀNG]:");
        inBangSanPham();

        // KỊCH BẢN 1: Giao dịch thành công (Giảm số lượng SP01 đi 5 và tăng SP02 thêm 5)
        System.out.println("\n--- KỊCH BẢN 1: ĐIỀU CHUYỂN KHO HỢP LỆ (COMMIT THÀNH CÔNG) ---");
        thucHienChuyenKho("SP01", "SP02", 5, false);
        inBangSanPham();

        // KỊCH BẢN 2: Giao dịch lỗi giữa chừng (Gây lỗi ngoại lệ cố ý -> Rollback)
        System.out.println("\n--- KỊCH BẢN 2: ĐIỀU CHUYỂN KHO GẶP LỖI GIỮA CHỪNG (ROLLBACK) ---");
        thucHienChuyenKho("SP01", "SP02", 10, true);
        inBangSanPham();
    }

    private static void initDatabase() {
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("DROP TABLE IF EXISTS SanPham;");
            stmt.executeUpdate("CREATE TABLE SanPham ("
                    + "maSP TEXT PRIMARY KEY, "
                    + "tenSP TEXT NOT NULL, "
                    + "gia REAL NOT NULL, "
                    + "soluong INTEGER NOT NULL CHECK (soluong >= 0)"
                    + ");");
            stmt.executeUpdate("INSERT INTO SanPham VALUES ('SP01', 'Kho A - Laptop Dell', 20000000, 50);");
            stmt.executeUpdate("INSERT INTO SanPham VALUES ('SP02', 'Kho B - Laptop Dell', 20000000, 10);");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Chuyển số lượng hàng từ maXuat sang maNhap
     */
    private static void thucHienChuyenKho(String maXuat, String maNhap, int soLuongChuyen, boolean giaLapLoi) {
        Connection conn = null;
        PreparedStatement pstmtTru = null;
        PreparedStatement pstmtCong = null;

        try {
            conn = DriverManager.getConnection(DB_URL);
            // 1. Tắt Auto-Commit để kiểm soát Transaction thủ công
            conn.setAutoCommit(false);
            System.out.println("-> [Bắt đầu Tx] conn.setAutoCommit(false)");

            // Bước A: Trừ số lượng kho xuất
            String sqlTru = "UPDATE SanPham SET soluong = soluong - ? WHERE maSP = ?;";
            pstmtTru = conn.prepareStatement(sqlTru);
            pstmtTru.setInt(1, soLuongChuyen);
            pstmtTru.setString(2, maXuat);
            pstmtTru.executeUpdate();
            System.out.println("-> [Bước A] Trừ " + soLuongChuyen + " sản phẩm tại " + maXuat + " thành công.");

            // Giả lập lỗi hệ thống nếu bật cờ giaLapLoi
            if (giaLapLoi) {
                System.out.println("-> [MÔ PHỎNG SỰ CỐ]: Mạng bị ngắt hoặc truy vấn SQL sai cú pháp!");
                throw new SQLException("Mô phỏng lỗi: Mất kết nối đường truyền giữa chừng!");
            }

            // Bước B: Cộng số lượng kho nhập
            String sqlCong = "UPDATE SanPham SET soluong = soluong + ? WHERE maSP = ?;";
            pstmtCong = conn.prepareStatement(sqlCong);
            pstmtCong.setInt(1, soLuongChuyen);
            pstmtCong.setString(2, maNhap);
            pstmtCong.executeUpdate();
            System.out.println("-> [Bước B] Cộng " + soLuongChuyen + " sản phẩm tại " + maNhap + " thành công.");

            // Tất cả bước hoàn tất mỹ mãn -> Commit
            conn.commit();
            System.out.println("-> [GIAO DỊCH COMMIT THÀNH CÔNG] Dữ liệu đã lưu vĩnh viễn vào DB!");

        } catch (SQLException e) {
            System.err.println("-> [GIAO DỊCH THẤT BẠI - BẮT NGOẠI LỆ]: " + e.getMessage());
            if (conn != null) {
                try {
                    conn.rollback();
                    System.out.println("-> [ROLLBACK HOÀN TẤT] Hệ thống hoàn tác mọi thay đổi! Số lượng kho được khôi phục nguyên vẹn.");
                } catch (SQLException ex) {
                    System.err.println("Lỗi khi rollback: " + ex.getMessage());
                }
            }
        } finally {
            if (pstmtTru != null) try { pstmtTru.close(); } catch (SQLException ignored) {}
            if (pstmtCong != null) try { pstmtCong.close(); } catch (SQLException ignored) {}
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException ignored) {}
            }
        }
    }

    private static void inBangSanPham() {
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT maSP, tenSP, gia, soluong FROM SanPham;")) {
            System.out.printf("%-8s | %-25s | %-15s | %-10s%n", "Mã SP", "Tên Sản Phẩm", "Đơn Giá", "Số Lượng");
            System.out.println("-----------------------------------------------------------------");
            while (rs.next()) {
                System.out.printf("%-8s | %-25s | %,15.0f | %,10d%n",
                        rs.getString("maSP"),
                        rs.getString("tenSP"),
                        rs.getDouble("gia"),
                        rs.getInt("soluong"));
            }
            System.out.println("-----------------------------------------------------------------");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
