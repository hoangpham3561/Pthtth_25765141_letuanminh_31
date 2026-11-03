package bai9_daugia;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.List;
import java.util.Scanner;

public class AuctionClient {
    public static void main(String[] args) {
        try {
            Registry registry = LocateRegistry.getRegistry("localhost", AuctionServer.PORT);
            AuctionService service = (AuctionService) registry.lookup(AuctionServer.SERVICE_NAME);

            if (args.length > 0 && args[0].equalsIgnoreCase("test")) {
                AuctionItem sp01 = service.getItem("SP01");
                double initialPrice = sp01.getCurrentPrice();

                boolean b1 = service.placeBid("SP01", "Bidder_A", initialPrice + 100.0);
                boolean b2 = service.placeBid("SP01", "Bidder_B", initialPrice + 50.0); // Fail because lower
                boolean b3 = service.placeBid("SP01", "Bidder_C", initialPrice + 250.0); // Win

                AuctionItem sp01After = service.getItem("SP01");
                System.out.println("[AuctionClient] Gia khoi diem: " + initialPrice);
                System.out.println("[AuctionClient] Bidder_A tra " + (initialPrice + 100) + ": " + b1);
                System.out.println("[AuctionClient] Bidder_B tra " + (initialPrice + 50) + ": " + b2);
                System.out.println("[AuctionClient] Bidder_C tra " + (initialPrice + 250) + ": " + b3);
                System.out.println("[AuctionClient] Nguoi thang cuoi: " + sp01After.getHighestBidder()
                        + " | Gia: " + sp01After.getCurrentPrice());

                if (b1 && !b2 && b3 && "Bidder_C".equals(sp01After.getHighestBidder())
                        && sp01After.getCurrentPrice() == (initialPrice + 250.0)) {
                    System.out.println("[AuctionClient] Test PASS!");
                    System.exit(0);
                } else {
                    System.err.println("[AuctionClient] Test FAIL!");
                    System.exit(1);
                }
                return;
            }

            Scanner sc = new Scanner(System.in);
            System.out.print("Nhập tên định danh của bạn (Bidder Name): ");
            String bidderName = sc.nextLine();

            while (true) {
                System.out.println("\n=== SÀN ĐẤU GIÁ TRỰC TUYẾN QUA RMI (BIDDER: " + bidderName + ") ===");
                System.out.println("1. Xem danh sách sản phẩm & giá cao nhất hiện tại");
                System.out.println("2. Đặt giá thầu (Place Bid)");
                System.out.println("0. Thoát");
                System.out.print("Chọn chức năng: ");
                int choice = Integer.parseInt(sc.nextLine());

                if (choice == 0) break;
                switch (choice) {
                    case 1:
                        List<AuctionItem> items = service.getItems();
                        System.out.println("\n--- DANH SÁCH SẢN PHẨM ĐANG ĐẤU GIÁ ---");
                        for (AuctionItem it : items) {
                            System.out.println(it);
                        }
                        break;
                    case 2:
                        System.out.print("Nhập mã sản phẩm muốn đấu giá (ví dụ SP01, SP02, SP03): ");
                        String itemId = sc.nextLine();
                        System.out.print("Nhập mức giá bạn muốn trả: ");
                        double price = Double.parseDouble(sc.nextLine());

                        boolean ok = service.placeBid(itemId, bidderName, price);
                        if (ok) {
                            System.out.println("-> Chúc mừng! Bạn đang là người giữ giá cao nhất cho " + itemId);
                        } else {
                            System.out.println("-> Đặt giá thất bại! Giá của bạn phải CAO HƠN giá hiện tại của sản phẩm.");
                        }
                        break;
                    default:
                        System.out.println("Lựa chọn không hợp lệ!");
                }
            }
        } catch (Exception e) {
            System.err.println("[AuctionClient] Lỗi: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}
