package bai2_tinhtong;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;

public class CalculatorClient {
    public static void main(String[] args) {
        try {
            Registry registry = LocateRegistry.getRegistry("localhost", CalculatorServer.PORT);
            CalculatorService service = (CalculatorService) registry.lookup(CalculatorServer.SERVICE_NAME);

            if (args.length > 0 && args[0].equalsIgnoreCase("test")) {
                int sum1 = service.add(15, 27);
                int sum2 = service.add(-5, 10);
                System.out.println("[CalculatorClient] Test add(15, 27) = " + sum1);
                System.out.println("[CalculatorClient] Test add(-5, 10) = " + sum2);

                if (sum1 == 42 && sum2 == 5) {
                    System.out.println("[CalculatorClient] Test PASS!");
                    System.exit(0);
                } else {
                    System.err.println("[CalculatorClient] Test FAIL!");
                    System.exit(1);
                }
                return;
            }

            Scanner sc = new Scanner(System.in);
            System.out.println("=== CHƯƠNG TRÌNH TÍNH TỔNG HAI SỐ TỪ XA QUA RMI ===");
            System.out.print("Nhập số nguyên a: ");
            int a = sc.nextInt();
            System.out.print("Nhập số nguyên b: ");
            int b = sc.nextInt();

            int result = service.add(a, b);
            System.out.println("Kết quả từ server: " + a + " + " + b + " = " + result);
        } catch (Exception e) {
            System.err.println("[CalculatorClient] Lỗi: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}
