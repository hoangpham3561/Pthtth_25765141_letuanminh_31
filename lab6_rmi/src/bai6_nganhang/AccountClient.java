package bai6_nganhang;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;

public class AccountClient {
    public static void main(String[] args) {
        try {
            Registry registry = LocateRegistry.getRegistry("localhost", AccountServer.PORT);
            AccountService service = (AccountService) registry.lookup(AccountServer.SERVICE_NAME);

            if (args.length > 0 && args[0].equalsIgnoreCase("test")) {
                double initBalance = service.getBalance();
                service.deposit(500.0);
                double afterDeposit = service.getBalance();
                boolean w1 = service.withdraw(300.0);
                double afterW1 = service.getBalance();
                boolean w2 = service.withdraw(50000.0); // should fail
                double afterW2 = service.getBalance();

                System.out.println("[AccountClient] So du ban dau: " + initBalance);
                System.out.println("[AccountClient] Sau khi nap 500: " + afterDeposit);
                System.out.println("[AccountClient] Rut 300: " + w1 + ", so du con: " + afterW1);
                System.out.println("[AccountClient] Rut 50000: " + w2 + ", so du con: " + afterW2);

                if (afterDeposit == initBalance + 500.0 && w1 && afterW1 == initBalance + 200.0
                        && !w2 && afterW2 == afterW1) {
                    System.out.println("[AccountClient] Test PASS!");
                    System.exit(0);
                } else {
                    System.err.println("[AccountClient] Test FAIL!");
                    System.exit(1);
                }
                return;
            }

            Scanner sc = new Scanner(System.in);
            while (true) {
                System.out.println("\n=== HỆ THỐNG QUẢN LÝ TÀI KHOẢN NGÂN HÀNG QUA RMI ===");
                System.out.println("1. Kiểm tra số dư");
                System.out.println("2. Gửi tiền (Deposit)");
                System.out.println("3. Rút tiền (Withdraw)");
                System.out.println("0. Thoát");
                System.out.print("Chọn chức năng: ");
                int choice = Integer.parseInt(sc.nextLine());

                if (choice == 0) break;
                switch (choice) {
                    case 1:
                        System.out.printf("-> Số dư hiện tại: %.2f VNĐ\n", service.getBalance());
                        break;
                    case 2:
                        System.out.print("Nhập số tiền muốn nạp: ");
                        double dep = Double.parseDouble(sc.nextLine());
                        service.deposit(dep);
                        System.out.printf("-> Nạp thành công! Số dư mới: %.2f VNĐ\n", service.getBalance());
                        break;
                    case 3:
                        System.out.print("Nhập số tiền muốn rút: ");
                        double w = Double.parseDouble(sc.nextLine());
                        boolean ok = service.withdraw(w);
                        if (ok) {
                            System.out.printf("-> Rút tiền thành công! Số dư còn lại: %.2f VNĐ\n", service.getBalance());
                        } else {
                            System.out.printf("-> Rút tiền thất bại! Không đủ số dư (Hiện có: %.2f VNĐ)\n", service.getBalance());
                        }
                        break;
                    default:
                        System.out.println("Lựa chọn không hợp lệ!");
                }
            }
        } catch (Exception e) {
            System.err.println("[AccountClient] Lỗi: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}
