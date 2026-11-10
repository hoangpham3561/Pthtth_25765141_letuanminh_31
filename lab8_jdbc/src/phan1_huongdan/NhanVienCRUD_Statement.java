package phan1_huongdan;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * B2 -> B5: Tạo bảng, thêm dữ liệu, truy vấn ResultSet, đóng tài nguyên.
 */
public class NhanVienCRUD_Statement {
    public static void main(String[] args) {
        String dbUrl = "jdbc:sqlite:nhanvien.db";
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            // B1: Đăng ký Driver & Kết nối
            Class.forName("org.sqlite.JDBC");
            conn = DriverManager.getConnection(dbUrl);
            System.out.println("=== B1: KẾT NỐI THÀNH CÔNG ĐẾN " + dbUrl + " ===");

            // B2: Tạo Statement
            stmt = conn.createStatement();
            System.out.println("=== B2: ĐÃ TẠO STATEMENT TỪ CONNECTION ===");

            // B3.1: Tạo bảng NhanVien (xóa cũ nếu có để làm mới thực hành)
            stmt.executeUpdate("DROP TABLE IF EXISTS NhanVien;");
            String sqlCreateTable = "CREATE TABLE NhanVien ("
                    + "id INTEGER PRIMARY KEY, "
                    + "ten TEXT NOT NULL, "
                    + "chuc_vu TEXT"
                    + ");";
            stmt.executeUpdate(sqlCreateTable);
            System.out.println("=== B3.1: ĐÃ TẠO BẢNG NhanVien THÀNH CÔNG ===");

            // B3.2: Thêm ít nhất 3 nhân viên bằng executeUpdate
            String sqlInsert1 = "INSERT INTO NhanVien(id, ten, chuc_vu) VALUES (1, 'Le Tuan Minh', 'Truong Phong CNTT');";
            String sqlInsert2 = "INSERT INTO NhanVien(id, ten, chuc_vu) VALUES (2, 'Nguyen Van An', 'Ky Su Phan Mem');";
            String sqlInsert3 = "INSERT INTO NhanVien(id, ten, chuc_vu) VALUES (3, 'Tran Thi Binh', 'Chuyen Vien Phan Tich');";
            String sqlInsert4 = "INSERT INTO NhanVien(id, ten, chuc_vu) VALUES (4, 'Pham Hoang Nam', 'Quan Tri He Thong');";

            int rows1 = stmt.executeUpdate(sqlInsert1);
            int rows2 = stmt.executeUpdate(sqlInsert2);
            int rows3 = stmt.executeUpdate(sqlInsert3);
            int rows4 = stmt.executeUpdate(sqlInsert4);
            System.out.println("=== B3.2: ĐÃ INSERT " + (rows1 + rows2 + rows3 + rows4) + " BẢN GHI VÀO NhanVien ===");

            // B4: Truy vấn và hiển thị dữ liệu
            String sqlSelect = "SELECT id, ten, chuc_vu FROM NhanVien;";
            rs = stmt.executeQuery(sqlSelect);
            System.out.println("\n=== B4: DANH SÁCH NHÂN VIÊN TRUY VẤN TỪ CSDL ===");
            System.out.printf("%-5s | %-25s | %-25s%n", "ID", "Họ và Tên", "Chức Vụ");
            System.out.println("-----------------------------------------------------------------");
            while (rs.next()) {
                int id = rs.getInt("id");
                String ten = rs.getString("ten");
                String chucVu = rs.getString("chuc_vu");
                System.out.printf("%-5d | %-25s | %-25s%n", id, ten, chucVu);
            }
            System.out.println("-----------------------------------------------------------------");

        } catch (ClassNotFoundException e) {
            System.err.println("[LỖI ClassNotFoundException]: " + e.getMessage());
        } catch (SQLException e) {
            System.err.println("[LỖI SQLException]: " + e.getMessage());
        } finally {
            // B5: Đóng tài nguyên theo thứ tự ngược lại trong finally
            System.out.println("\n=== B5: ĐÓNG TÀI NGUYÊN (ResultSet -> Statement -> Connection) ===");
            if (rs != null) {
                try {
                    rs.close();
                    System.out.println("-> Đã đóng ResultSet.");
                } catch (SQLException e) {
                    System.err.println("Lỗi đóng ResultSet: " + e.getMessage());
                }
            }
            if (stmt != null) {
                try {
                    stmt.close();
                    System.out.println("-> Đã đóng Statement.");
                } catch (SQLException e) {
                    System.err.println("Lỗi đóng Statement: " + e.getMessage());
                }
            }
            if (conn != null) {
                try {
                    conn.close();
                    System.out.println("-> Đã đóng Connection.");
                } catch (SQLException e) {
                    System.err.println("Lỗi đóng Connection: " + e.getMessage());
                }
            }
        }
    }
}
