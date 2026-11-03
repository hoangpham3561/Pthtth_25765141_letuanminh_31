package bai3_songuyento;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;

public class PrimeClient {
    public static void main(String[] args) {
        try {
            Registry registry = LocateRegistry.getRegistry("localhost", PrimeServer.PORT);
            PrimeService service = (PrimeService) registry.lookup(PrimeServer.SERVICE_NAME);

            if (args.length > 0 && args[0].equalsIgnoreCase("test")) {
                boolean p2 = service.isPrime(2);
                boolean p7 = service.isPrime(7);
                boolean p1 = service.isPrime(1);
                boolean p4 = service.isPrime(4);
                boolean p29 = service.isPrime(29);
                boolean p100 = service.isPrime(100);

                System.out.println("[PrimeClient] Test isPrime(2): " + p2);
                System.out.println("[PrimeClient] Test isPrime(7): " + p7);
                System.out.println("[PrimeClient] Test isPrime(1): " + p1);
                System.out.println("[PrimeClient] Test isPrime(4): " + p4);
                System.out.println("[PrimeClient] Test isPrime(29): " + p29);
                System.out.println("[PrimeClient] Test isPrime(100): " + p100);

                if (p2 && p7 && !p1 && !p4 && p29 && !p100) {
                    System.out.println("[PrimeClient] Test PASS!");
                    System.exit(0);
                } else {
                    System.err.println("[PrimeClient] Test FAIL!");
                    System.exit(1);
                }
                return;
            }

            Scanner sc = new Scanner(System.in);
            System.out.println("=== CHƯƠNG TRÌNH KIỂM TRA SỐ NGUYÊN TỐ QUA RMI ===");
            System.out.print("Nhập số nguyên n cần kiểm tra: ");
            int n = sc.nextInt();
            boolean result = service.isPrime(n);
            if (result) {
                System.out.println("Số " + n + " LÀ số nguyên tố.");
            } else {
                System.out.println("Số " + n + " KHÔNG PHẢI là số nguyên tố.");
            }
        } catch (Exception e) {
            System.err.println("[PrimeClient] Lỗi: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}
