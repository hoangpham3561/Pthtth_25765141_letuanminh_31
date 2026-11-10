package phan2_baitap.bai1_quanly_sanpham;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class SanPhamDAO {
    private final String dbUrl;

    public SanPhamDAO(String dbUrl) {
        this.dbUrl = dbUrl;
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        createTable();
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(dbUrl);
    }

    public void createTable() {
        String sql = "CREATE TABLE IF NOT EXISTS SanPham ("
                + "maSP TEXT PRIMARY KEY, "
                + "tenSP TEXT NOT NULL, "
                + "gia REAL NOT NULL, "
                + "soluong INTEGER NOT NULL"
                + ");";
        try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(sql);
        } catch (SQLException e) {
            System.err.println("[Lỗi tạo bảng SanPham]: " + e.getMessage());
        }
    }

    public void clearTable() {
        try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("DELETE FROM SanPham;");
        } catch (SQLException e) {
            System.err.println("[Lỗi clearTable]: " + e.getMessage());
        }
    }

    // 1. Create (Thêm mới)
    public boolean insert(SanPham sp) {
        String sql = "INSERT INTO SanPham(maSP, tenSP, gia, soluong) VALUES (?, ?, ?, ?);";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, sp.getMaSP());
            pstmt.setString(2, sp.getTenSP());
            pstmt.setDouble(3, sp.getGia());
            pstmt.setInt(4, sp.getSoLuong());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[Lỗi insert SanPham]: " + e.getMessage());
            return false;
        }
    }

    // 2. Read (Lấy tất cả)
    public List<SanPham> getAll() {
        List<SanPham> list = new ArrayList<>();
        String sql = "SELECT maSP, tenSP, gia, soluong FROM SanPham ORDER BY maSP ASC;";
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new SanPham(
                        rs.getString("maSP"),
                        rs.getString("tenSP"),
                        rs.getDouble("gia"),
                        rs.getInt("soluong")
                ));
            }
        } catch (SQLException e) {
            System.err.println("[Lỗi getAll SanPham]: " + e.getMessage());
        }
        return list;
    }

    // 2b. Read (Tìm kiếm theo tên)
    public List<SanPham> searchByName(String keyword) {
        List<SanPham> list = new ArrayList<>();
        String sql = "SELECT maSP, tenSP, gia, soluong FROM SanPham WHERE LOWER(tenSP) LIKE ? ORDER BY maSP ASC;";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, "%" + keyword.toLowerCase() + "%");
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    list.add(new SanPham(
                            rs.getString("maSP"),
                            rs.getString("tenSP"),
                            rs.getDouble("gia"),
                            rs.getInt("soluong")
                    ));
                }
            }
        } catch (SQLException e) {
            System.err.println("[Lỗi searchByName]: " + e.getMessage());
        }
        return list;
    }

    // 3. Update (Cập nhật giá hoặc số lượng theo maSP)
    public boolean update(String maSP, double giaMoi, int soLuongMoi) {
        String sql = "UPDATE SanPham SET gia = ?, soluong = ? WHERE maSP = ?;";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setDouble(1, giaMoi);
            pstmt.setInt(2, soLuongMoi);
            pstmt.setString(3, maSP);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[Lỗi update SanPham]: " + e.getMessage());
            return false;
        }
    }

    // 4. Delete (Xóa sản phẩm theo maSP)
    public boolean delete(String maSP) {
        String sql = "DELETE FROM SanPham WHERE maSP = ?;";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, maSP);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[Lỗi delete SanPham]: " + e.getMessage());
            return false;
        }
    }
}
