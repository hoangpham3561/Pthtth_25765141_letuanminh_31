import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/** Bài tập tổng hợp bắt buộc - InventoryManager (mục 9). */
public class BaiTapTongHop {
    private static final Path CSV = Path.of("data", "inventory.csv");
    private static final Path REPORT = Path.of("data", "inventory-report.txt");

    public static void main(String[] args) {
        List<Product> products = nhapTuBanPhim();
        luuCsv(products);
        List<Product> loaded = docCsv();
        if (loaded.isEmpty()) {
            return;
        }
        hienThiVaBaoCao(loaded);
    }

    private static List<Product> nhapTuBanPhim() {
        List<Product> products = new ArrayList<>();
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(System.in, StandardCharsets.UTF_8));
        System.out.println("Nhập sản phẩm (mã rỗng hoặc q để kết thúc):");
        try {
            while (true) {
                System.out.print("Mã: ");
                String code = reader.readLine();
                if (code == null || code.isBlank() || code.equalsIgnoreCase("q")) {
                    break;
                }
                System.out.print("Tên: ");
                String name = reader.readLine();
                System.out.print("Đơn giá: ");
                String priceStr = reader.readLine();
                System.out.print("Số lượng: ");
                String qtyStr = reader.readLine();
                try {
                    products.add(new Product(
                            code.trim(),
                            name.trim(),
                            Double.parseDouble(priceStr.trim()),
                            Integer.parseInt(qtyStr.trim())));
                    System.out.println("→ Đã thêm.");
                } catch (NumberFormatException e) {
                    System.err.println("Dữ liệu số không hợp lệ, bỏ qua.");
                } catch (IllegalArgumentException e) {
                    System.err.println("Từ chối: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.err.println("Lỗi đọc bàn phím: " + e.getMessage());
        }
        return products;
    }

    private static void luuCsv(List<Product> products) {
        try {
            Files.createDirectories(CSV.getParent());
            try (BufferedWriter writer = Files.newBufferedWriter(CSV, StandardCharsets.UTF_8)) {
                writer.write("ma,ten,donGia,soLuong");
                writer.newLine();
                for (Product p : products) {
                    writer.write(p.toCsvLine());
                    writer.newLine();
                }
            }
            System.out.println("Đã lưu: " + CSV.toAbsolutePath());
        } catch (IOException e) {
            System.err.println("Không ghi được " + CSV + ": " + e.getMessage());
        }
    }

    private static List<Product> docCsv() {
        List<Product> products = new ArrayList<>();
        if (!Files.exists(CSV)) {
            System.err.println("Không tìm thấy tệp: " + CSV.toAbsolutePath());
            return products;
        }
        try (BufferedReader reader = Files.newBufferedReader(CSV, StandardCharsets.UTF_8)) {
            reader.readLine();
            String line;
            int lineNumber = 1;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                if (line.isBlank()) continue;
                String[] parts = line.split(",", -1);
                if (parts.length != 4) {
                    System.err.println("Bỏ qua dòng " + lineNumber + " (thiếu cột) trong " + CSV);
                    continue;
                }
                try {
                    products.add(new Product(
                            parts[0].trim(), parts[1].trim(),
                            Double.parseDouble(parts[2].trim()),
                            Integer.parseInt(parts[3].trim())));
                } catch (NumberFormatException e) {
                    System.err.println("Dòng " + lineNumber + " số không hợp lệ trong " + CSV);
                } catch (IllegalArgumentException e) {
                    System.err.println("Dòng " + lineNumber + " không hợp lệ: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.err.println("Không đọc được " + CSV + ": " + e.getMessage());
        }
        return products;
    }

    private static void hienThiVaBaoCao(List<Product> products) {
        double total = 0;
        Product max = products.get(0);
        for (Product p : products) {
            System.out.println(p);
            total += p.inventoryValue();
            if (p.inventoryValue() > max.inventoryValue()) {
                max = p;
            }
        }
        System.out.printf("Tổng giá trị tồn kho: %,.0f VND%n", total);
        System.out.println("Tồn kho cao nhất: " + max);
        try (BufferedWriter writer = Files.newBufferedWriter(REPORT, StandardCharsets.UTF_8)) {
            writer.write("Số sản phẩm: " + products.size());
            writer.newLine();
            writer.write("Tổng giá trị tồn kho: %,.0f VND".formatted(total));
            writer.newLine();
            writer.write("Sản phẩm tồn kho cao nhất: " + max);
            writer.newLine();
        } catch (IOException e) {
            System.err.println("Không ghi được " + REPORT + ": " + e.getMessage());
        }
    }
}
