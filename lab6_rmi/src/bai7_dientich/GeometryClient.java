package bai7_dientich;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;

public class GeometryClient {
    public static void main(String[] args) {
        try {
            Registry registry = LocateRegistry.getRegistry("localhost", GeometryServer.PORT);
            GeometryService service = (GeometryService) registry.lookup(GeometryServer.SERVICE_NAME);

            if (args.length > 0 && args[0].equalsIgnoreCase("test")) {
                double rArea = service.rectangleArea(4.0, 5.0);
                double cArea = service.circleArea(10.0);
                double tArea = service.triangleArea(6.0, 4.0);

                System.out.println("[GeometryClient] Dientich HCN (4x5): " + rArea);
                System.out.printf("[GeometryClient] Dientich HinhTron (r=10): %.4f\n", cArea);
                System.out.println("[GeometryClient] Dientich TamGiac (day 6, cao 4): " + tArea);

                boolean rOk = Math.abs(rArea - 20.0) < 0.001;
                boolean cOk = Math.abs(cArea - (Math.PI * 100)) < 0.001;
                boolean tOk = Math.abs(tArea - 12.0) < 0.001;

                if (rOk && cOk && tOk) {
                    System.out.println("[GeometryClient] Test PASS!");
                    System.exit(0);
                } else {
                    System.err.println("[GeometryClient] Test FAIL!");
                    System.exit(1);
                }
                return;
            }

            Scanner sc = new Scanner(System.in);
            while (true) {
                System.out.println("\n=== HỆ THỐNG TÍNH DIỆN TÍCH HÌNH HỌC QUA RMI ===");
                System.out.println("1. Diện tích hình chữ nhật");
                System.out.println("2. Diện tích hình tròn");
                System.out.println("3. Diện tích hình tam giác");
                System.out.println("0. Thoát");
                System.out.print("Chọn chức năng: ");
                int choice = Integer.parseInt(sc.nextLine());

                if (choice == 0) break;
                switch (choice) {
                    case 1:
                        System.out.print("Nhập chiều rộng: ");
                        double w = Double.parseDouble(sc.nextLine());
                        System.out.print("Nhập chiều dài: ");
                        double h = Double.parseDouble(sc.nextLine());
                        System.out.printf("-> Diện tích HCN: %.2f\n", service.rectangleArea(w, h));
                        break;
                    case 2:
                        System.out.print("Nhập bán kính: ");
                        double r = Double.parseDouble(sc.nextLine());
                        System.out.printf("-> Diện tích hình tròn: %.4f\n", service.circleArea(r));
                        break;
                    case 3:
                        System.out.print("Nhập độ dài đáy: ");
                        double base = Double.parseDouble(sc.nextLine());
                        System.out.print("Nhập chiều cao: ");
                        double th = Double.parseDouble(sc.nextLine());
                        System.out.printf("-> Diện tích tam giác: %.2f\n", service.triangleArea(base, th));
                        break;
                    default:
                        System.out.println("Lựa chọn không hợp lệ!");
                }
            }
        } catch (Exception e) {
            System.err.println("[GeometryClient] Lỗi: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}
