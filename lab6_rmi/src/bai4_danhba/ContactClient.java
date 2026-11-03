package bai4_danhba;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;

public class ContactClient {
    public static void main(String[] args) {
        try {
            Registry registry = LocateRegistry.getRegistry("localhost", ContactServer.PORT);
            ContactService service = (ContactService) registry.lookup(ContactServer.SERVICE_NAME);

            if (args.length > 0 && args[0].equalsIgnoreCase("test")) {
                service.addContact("Nguyen Van A", "0901234567");
                service.addContact("Tran Thi B", "0987654321");

                String phoneA = service.findContact("Nguyen Van A");
                String phoneB = service.findContact("Tran Thi B");
                String phoneC = service.findContact("Le Van C");

                boolean delA = service.deleteContact("Nguyen Van A");
                String phoneAAfterDel = service.findContact("Nguyen Van A");
                boolean delAgain = service.deleteContact("Nguyen Van A");

                System.out.println("[ContactClient] Tim Nguyen Van A: " + phoneA);
                System.out.println("[ContactClient] Tim Tran Thi B: " + phoneB);
                System.out.println("[ContactClient] Tim Le Van C (khong ton tai): " + phoneC);
                System.out.println("[ContactClient] Xoa Nguyen Van A: " + delA);
                System.out.println("[ContactClient] Tim lai sau khi xoa: " + phoneAAfterDel);

                if ("0901234567".equals(phoneA) && "0987654321".equals(phoneB) && phoneC == null
                        && delA && phoneAAfterDel == null && !delAgain) {
                    System.out.println("[ContactClient] Test PASS!");
                    System.exit(0);
                } else {
                    System.err.println("[ContactClient] Test FAIL!");
                    System.exit(1);
                }
                return;
            }

            Scanner sc = new Scanner(System.in);
            while (true) {
                System.out.println("\n=== HỆ THỐNG QUẢN LÝ DANH BẠ QUA RMI ===");
                System.out.println("1. Thêm / cập nhật liên hệ");
                System.out.println("2. Tìm kiếm liên hệ");
                System.out.println("3. Xóa liên hệ");
                System.out.println("0. Thoát");
                System.out.print("Chọn chức năng: ");
                int choice = Integer.parseInt(sc.nextLine());

                if (choice == 0) break;
                switch (choice) {
                    case 1:
                        System.out.print("Nhập tên: ");
                        String name = sc.nextLine();
                        System.out.print("Nhập số điện thoại: ");
                        String phone = sc.nextLine();
                        service.addContact(name, phone);
                        System.out.println("Đã thêm thành công!");
                        break;
                    case 2:
                        System.out.print("Nhập tên cần tìm: ");
                        String searchName = sc.nextLine();
                        String foundPhone = service.findContact(searchName);
                        if (foundPhone != null) {
                            System.out.println("-> Số điện thoại: " + foundPhone);
                        } else {
                            System.out.println("-> Không tìm thấy liên hệ!");
                        }
                        break;
                    case 3:
                        System.out.print("Nhập tên cần xóa: ");
                        String delName = sc.nextLine();
                        boolean ok = service.deleteContact(delName);
                        if (ok) {
                            System.out.println("-> Đã xóa thành công!");
                        } else {
                            System.out.println("-> Không tìm thấy liên hệ để xóa!");
                        }
                        break;
                    default:
                        System.out.println("Lựa chọn không hợp lệ!");
                }
            }
        } catch (Exception e) {
            System.err.println("[ContactClient] Lỗi: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}
