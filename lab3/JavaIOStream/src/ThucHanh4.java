import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class ThucHanh4 {
    public static void main(String[] args) {
        Path dataDir = timThuMucData();
        if (dataDir == null) {
            System.err.println("Không tìm thấy data/products.csv trên máy.");
            System.err.println("Thư mục chạy hiện tại: " + Path.of("").toAbsolutePath());
            return;
        }
        System.out.println("Đang đọc: " + dataDir.resolve("products.csv").toAbsolutePath());

        Path input = dataDir.resolve("products.csv");
        Path report = dataDir.resolve("report.txt");
        List<Product> products = new ArrayList<>();
        try (BufferedReader reader = Files.newBufferedReader(
                input, StandardCharsets.UTF_8)) {
            reader.readLine(); // bỏ qua dòng tiêu đề
            String line;
            int lineNumber = 1;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                if (line.isBlank()) continue;
                String[] parts = line.split(",", -1);
                if (parts.length != 4) {
                    System.err.println("Bỏ qua dòng " + lineNumber);
                    continue;
                }
                try {
                    products.add(new Product(
                            parts[0].trim(), parts[1].trim(),
                            Double.parseDouble(parts[2].trim()),
                            Integer.parseInt(parts[3].trim())));
                } catch (IllegalArgumentException e) {
                    System.err.println("Dòng " + lineNumber
                            + " không hợp lệ: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.err.println("Không đọc được CSV: " + e.getMessage());
            return;
        }
        double total = 0;
        for (Product product : products) {
            System.out.println(product);
            total += product.inventoryValue();
        }
        try (BufferedWriter writer = Files.newBufferedWriter(
                report, StandardCharsets.UTF_8)) {
            writer.write("Số sản phẩm: " + products.size());
            writer.newLine();
            writer.write("Tổng giá trị tồn kho: %,.0f VND".formatted(total));
            writer.newLine();
            System.out.println("Đã ghi báo cáo: " + report.toAbsolutePath());
        } catch (IOException e) {
            System.err.println("Không ghi được báo cáo: " + e.getMessage());
        }
    }

    /**
     * Tìm data/products.csv kể cả khi IDE (Oracle Java) chạy từ thư mục tạm.
     */
    private static Path timThuMucData() {
        List<Path> roots = new ArrayList<>();
        roots.add(Path.of("").toAbsolutePath().normalize());
        roots.add(Path.of(System.getProperty("user.home"), "Desktop", "IUH-k2", "PTHTTH"));
        roots.add(Path.of(System.getProperty("user.home"), "Desktop"));
        try {
            var cs = ThucHanh4.class.getProtectionDomain().getCodeSource();
            if (cs != null && cs.getLocation() != null) {
                roots.add(Path.of(cs.getLocation().toURI()).toAbsolutePath().normalize());
            }
        } catch (Exception ignored) {
        }

        for (Path root : roots) {
            Path found = timTrongCay(root);
            if (found != null) {
                return found;
            }
        }
        return null;
    }

    private static Path timTrongCay(Path start) {
        if (start == null || !Files.exists(start)) {
            return null;
        }
        // Đi lên vài cấp
        Path dir = start;
        for (int i = 0; i < 8 && dir != null; i++) {
            Path hit = kiemTraData(dir);
            if (hit != null) {
                return hit;
            }
            dir = dir.getParent();
        }
        // Quét xuống (giới hạn) để tìm lab3/JavaIOStream/data/products.csv
        try (Stream<Path> walk = Files.walk(start, 6)) {
            return walk
                    .filter(p -> p.getFileName().toString().equals("products.csv"))
                    .filter(p -> p.getParent() != null
                            && p.getParent().getFileName().toString().equals("data"))
                    .map(Path::getParent)
                    .findFirst()
                    .orElse(null);
        } catch (IOException e) {
            return null;
        }
    }

    private static Path kiemTraData(Path dir) {
        Path[] tries = {
                dir.resolve("data"),
                dir.resolve("JavaIOStream").resolve("data"),
                dir.resolve("lab3").resolve("JavaIOStream").resolve("data")
        };
        for (Path candidate : tries) {
            if (Files.isRegularFile(candidate.resolve("products.csv"))) {
                return candidate;
            }
        }
        return null;
    }
}
