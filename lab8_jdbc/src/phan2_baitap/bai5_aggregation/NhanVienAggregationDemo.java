package phan2_baitap.bai5_aggregation;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Bài tập 5: Truy vấn dữ liệu nâng cao (Aggregation Functions)
 * - SUM(luong), AVG(luong), MIN(luong), MAX(luong)
 * - COUNT(*) và GROUP BY chuc_vu
 */
public class NhanVienAggregationDemo {
    private static final String DB_URL = "jdbc:sqlite:nhanvien_luong.db";

    public static void main(String[] args) {
        initData();

        System.out.println("========================================================================");
        System.out.println(" BÀI TẬP 5: TRUY VẤN DỮ LIỆU NÂNG CAO (AGGREGATION FUNCTIONS VỚI JDBC)");
        System.out.println("========================================================================");

        System.out.println("\n[DANH SÁCH NHÂN VIÊN VÀ MỨC LƯƠNG TRONG HỆ THỐNG]:");
        inDanhSachNhanVien();

        System.out.println("\n--- 1. TỔNG QUAN THỐNG KÊ LƯƠNG TOÀN DOANH NGHIỆP ---");
        thongKeLuongTongQuat();

        System.out.println("\n--- 2. CHI TIẾT NHÂN VIÊN CÓ MỨC LƯƠNG CAO NHẤT VÀ THẤP NHẤT ---");
        timNhanVienMinMaxLuong();

        System.out.println("\n--- 3. THỐNG KÊ SỐ LƯỢNG VÀ BÌNH QUÂN LƯƠNG THEO CHỨC VỤ (GROUP BY) ---");
        thongKeTheoChucVu();
    }

    private static void initData() {
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {

            stmt.executeUpdate("DROP TABLE IF EXISTS NhanVien;");
            stmt.executeUpdate("CREATE TABLE NhanVien ("
                    + "id INTEGER PRIMARY KEY, "
                    + "ten TEXT NOT NULL, "
                    + "chuc_vu TEXT NOT NULL, "
                    + "luong REAL NOT NULL"
                    + ");");

            String sqlInsert = "INSERT INTO NhanVien (id, ten, chuc_vu, luong) VALUES (?, ?, ?, ?);";
            try (PreparedStatement pstmt = conn.prepareStatement(sqlInsert)) {
                Object[][] records = {
                        {1, "Le Tuan Minh", "Quan Ly", 35000000.0},
                        {2, "Tran Thu Hang", "Quan Ly", 32000000.0},
                        {3, "Nguyen Van An", "Lap Trinh Vien", 22000000.0},
                        {4, "Pham Hoang Nam", "Lap Trinh Vien", 25000000.0},
                        {5, "Vo Van Cuong", "Lap Trinh Vien", 18500000.0},
                        {6, "Tran Thi Binh", "Kiem Thu Vien", 16000000.0},
                        {7, "Dang Quoc Bao", "Kiem Thu Vien", 17500000.0},
                        {8, "Hoang Bao Ngoc", "Thuc Tap Sinh", 6000000.0}
                };

                for (Object[] r : records) {
                    pstmt.setInt(1, (int) r[0]);
                    pstmt.setString(2, (String) r[1]);
                    pstmt.setString(3, (String) r[2]);
                    pstmt.setDouble(4, (double) r[3]);
                    pstmt.executeUpdate();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void inDanhSachNhanVien() {
        String sql = "SELECT id, ten, chuc_vu, luong FROM NhanVien ORDER BY id ASC;";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            System.out.printf("%-5s | %-20s | %-18s | %-18s%n", "ID", "Họ và Tên", "Chức Vụ", "Lương Hàng Tháng");
            System.out.println("-------------------------------------------------------------------------");
            while (rs.next()) {
                System.out.printf("%-5d | %-20s | %-18s | %,15.0f VND%n",
                        rs.getInt("id"),
                        rs.getString("ten"),
                        rs.getString("chuc_vu"),
                        rs.getDouble("luong"));
            }
            System.out.println("-------------------------------------------------------------------------");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void thongKeLuongTongQuat() {
        String sql = "SELECT "
                + "SUM(luong) AS tong_luong, "
                + "AVG(luong) AS luong_tb, "
                + "MIN(luong) AS luong_min, "
                + "MAX(luong) AS luong_max, "
                + "COUNT(*) AS tong_so_nv "
                + "FROM NhanVien;";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                double tongLuong = rs.getDouble("tong_luong");
                double luongTB = rs.getDouble("luong_tb");
                double luongMin = rs.getDouble("luong_min");
                double luongMax = rs.getDouble("luong_max");
                int tongSoNV = rs.getInt("tong_so_nv");

                System.out.println("-> Tổng số nhân viên:                  " + tongSoNV + " người");
                System.out.printf("-> Tổng quỹ lương (SUM):               %,18.0f VND%n", tongLuong);
                System.out.printf("-> Mức lương trung bình (AVG):         %,18.0f VND%n", luongTB);
                System.out.printf("-> Mức lương thấp nhất (MIN):          %,18.0f VND%n", luongMin);
                System.out.printf("-> Mức lương cao nhất (MAX):           %,18.0f VND%n", luongMax);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void timNhanVienMinMaxLuong() {
        String sql = "SELECT 'Lương Cao Nhất' AS loai, id, ten, chuc_vu, luong FROM NhanVien "
                + "WHERE luong = (SELECT MAX(luong) FROM NhanVien) "
                + "UNION ALL "
                + "SELECT 'Lương Thấp Nhất' AS loai, id, ten, chuc_vu, luong FROM NhanVien "
                + "WHERE luong = (SELECT MIN(luong) FROM NhanVien);";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                System.out.printf("-> [%-16s] ID: %-2d | Tên: %-18s | Chức vụ: %-15s | Lương: %,15.0f VND%n",
                        rs.getString("loai"),
                        rs.getInt("id"),
                        rs.getString("ten"),
                        rs.getString("chuc_vu"),
                        rs.getDouble("luong"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void thongKeTheoChucVu() {
        String sql = "SELECT chuc_vu, "
                + "COUNT(*) AS so_luong, "
                + "AVG(luong) AS luong_trung_binh, "
                + "SUM(luong) AS tong_luong "
                + "FROM NhanVien "
                + "GROUP BY chuc_vu "
                + "ORDER BY so_luong DESC;";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            System.out.printf("%-18s | %-10s | %-20s | %-20s%n", "Chức Vụ", "Số Lượng", "Lương Bình Quân", "Tổng Lương Chức Vụ");
            System.out.println("-----------------------------------------------------------------------------------");
            while (rs.next()) {
                System.out.printf("%-18s | %-10d | %,16.0f VND | %,16.0f VND%n",
                        rs.getString("chuc_vu"),
                        rs.getInt("so_luong"),
                        rs.getDouble("luong_trung_binh"),
                        rs.getDouble("tong_luong"));
            }
            System.out.println("-----------------------------------------------------------------------------------");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
