package phan1_huongdan;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * B1. Chuẩn bị môi trường và thiết lập kết nối SQLite qua JDBC
 */
public class B1_KetNoiSQLite {
    public static void main(String[] args) {
        String dbUrl = "jdbc:sqlite:nhanvien.db";
        Connection conn = null;

        try {
            // Nạp JDBC Driver cho SQLite
            Class.forName("org.sqlite.JDBC");
            System.out.println("[B1] Nạp driver org.sqlite.JDBC thành công!");

            // Thiết lập kết nối
            conn = DriverManager.getConnection(dbUrl);
            if (conn != null) {
                System.out.println("[B1] Kết nối cơ sở dữ liệu SQLite thành công: " + dbUrl);
                System.out.println("[B1] Thông tin Driver: " + conn.getMetaData().getDriverName() 
                        + " - Phiên bản: " + conn.getMetaData().getDriverVersion());
            }
        } catch (ClassNotFoundException e) {
            System.err.println("[B1 LỖI] Không tìm thấy driver JDBC: " + e.getMessage());
        } catch (SQLException e) {
            System.err.println("[B1 LỖI] Lỗi kết nối cơ sở dữ liệu: " + e.getMessage());
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                    System.out.println("[B1] Đã đóng kết nối an toàn.");
                } catch (SQLException e) {
                    System.err.println("[B1 LỖI] Lỗi khi đóng kết nối: " + e.getMessage());
                }
            }
        }
    }
}
