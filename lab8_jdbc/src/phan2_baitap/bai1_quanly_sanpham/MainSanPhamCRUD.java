package phan2_baitap.bai1_quanly_sanpham;

import java.util.List;

/**
 * Bài tập 1: Quản lý danh mục sản phẩm (CRUD đầy đủ)
 */
public class MainSanPhamCRUD {
    public static void main(String[] args) {
        String dbUrl = "jdbc:sqlite:sanpham.db";
        SanPhamDAO dao = new SanPhamDAO(dbUrl);

        System.out.println("========================================================================");
        System.out.println(" BÀI TẬP 1: QUẢN LÝ DANH MỤC SẢN PHẨM (CRUD VỚI SQLITE JDBC)");
        System.out.println("========================================================================");

        // Chuẩn bị dữ liệu ban đầu
        dao.clearTable();

        // 1. CREATE: Thêm ít nhất 3 sản phẩm
        System.out.println("\n[THAO TÁC 1: CREATE] Thêm 4 sản phẩm mẫu vào cơ sở dữ liệu:");
        SanPham sp1 = new SanPham("SP01", "MacBook Pro M3 Max", 65000000, 15);
        SanPham sp2 = new SanPham("SP02", "iPhone 16 Pro Max", 34500000, 30);
        SanPham sp3 = new SanPham("SP03", "Ban phim co Keychron Q1", 4200000, 50);
        SanPham sp4 = new SanPham("SP04", "Tai nghe Sony WH-1000XM5", 7990000, 25);

        dao.insert(sp1);
        dao.insert(sp2);
        dao.insert(sp3);
        dao.insert(sp4);
        System.out.println("-> Thêm 4 sản phẩm thành công!");

        // 2. READ: Liệt kê tất cả sản phẩm
        System.out.println("\n[THAO TÁC 2A: READ - ALL] Danh sách toàn bộ sản phẩm trong kho:");
        inBangSanPham(dao.getAll());

        // 2b. READ: Tìm kiếm sản phẩm theo tên
        String tuKhoa = "pro";
        System.out.println("\n[THAO TÁC 2B: READ - SEARCH] Tìm kiếm sản phẩm với từ khóa: '" + tuKhoa + "':");
        List<SanPham> ketQuaTimKiem = dao.searchByName(tuKhoa);
        inBangSanPham(ketQuaTimKiem);

        // 3. UPDATE: Cập nhật giá và số lượng theo maSP
        System.out.println("\n[THAO TÁC 3: UPDATE] Cập nhật giá và số lượng cho mã SP01:");
        System.out.println("-> Đổi giá SP01 thành 59,990,000 VND và số lượng thành 40:");
        boolean daSua = dao.update("SP01", 59990000, 40);
        System.out.println("-> Trạng thái cập nhật: " + (daSua ? "THÀNH CÔNG" : "THẤT BẠI"));

        // 4. DELETE: Xóa sản phẩm theo maSP
        System.out.println("\n[THAO TÁC 4: DELETE] Xóa sản phẩm mã SP03 khỏi kho:");
        boolean daXoa = dao.delete("SP03");
        System.out.println("-> Trạng thái xóa: " + (daXoa ? "THÀNH CÔNG" : "THẤT BẠI"));

        // 5. HIỂN THỊ LẠI SAU KHI CẬP NHẬT VÀ XÓA
        System.out.println("\n[KẾT QUẢ CUỐI CÙNG] Bảng sản phẩm sau khi đã Update SP01 và Delete SP03:");
        inBangSanPham(dao.getAll());
    }

    private static void inBangSanPham(List<SanPham> danhSach) {
        System.out.printf("%-10s | %-30s | %-19s | %-10s%n", "Mã SP", "Tên Sản Phẩm", "Đơn Giá", "Số Lượng");
        System.out.println("-----------------------------------------------------------------------------");
        if (danhSach.isEmpty()) {
            System.out.println("  (Không tìm thấy sản phẩm nào)");
        } else {
            for (SanPham sp : danhSach) {
                System.out.println(sp);
            }
        }
        System.out.println("-----------------------------------------------------------------------------");
    }
}
