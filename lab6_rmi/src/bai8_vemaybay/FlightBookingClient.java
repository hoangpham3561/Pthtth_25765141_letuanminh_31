package bai8_vemaybay;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.List;
import java.util.Scanner;

public class FlightBookingClient {
    public static void main(String[] args) {
        try {
            Registry registry = LocateRegistry.getRegistry("localhost", FlightBookingServer.PORT);
            FlightBookingService service = (FlightBookingService) registry.lookup(FlightBookingServer.SERVICE_NAME);

            if (args.length > 0 && args[0].equalsIgnoreCase("test")) {
                int initSeats = service.availableSeats("VN123");
                boolean b1 = service.bookTicket("VN123", 5);
                int seatsAfterB1 = service.availableSeats("VN123");
                boolean b2 = service.bookTicket("VN123", 1000); // Fail due to shortage
                boolean c1 = service.cancelBooking("VN123", 2);
                int seatsAfterC1 = service.availableSeats("VN123");

                System.out.println("[FlightBookingClient] Ghe ban dau VN123: " + initSeats);
                System.out.println("[FlightBookingClient] Dat 5 ve: " + b1 + " | Con: " + seatsAfterB1);
                System.out.println("[FlightBookingClient] Dat 1000 ve: " + b2);
                System.out.println("[FlightBookingClient] Huy 2 ve: " + c1 + " | Con: " + seatsAfterC1);

                if (b1 && seatsAfterB1 == initSeats - 5 && !b2 && c1 && seatsAfterC1 == seatsAfterB1 + 2) {
                    System.out.println("[FlightBookingClient] Test PASS!");
                    System.exit(0);
                } else {
                    System.err.println("[FlightBookingClient] Test FAIL!");
                    System.exit(1);
                }
                return;
            }

            Scanner sc = new Scanner(System.in);
            while (true) {
                System.out.println("\n=== HỆ THỐNG ĐẶT VÉ MÁY BAY QUA RMI ===");
                System.out.println("1. Xem danh sách chuyến bay & số chỗ trống");
                System.out.println("2. Đặt vé máy bay");
                System.out.println("3. Hủy vé đã đặt");
                System.out.println("0. Thoát");
                System.out.print("Chọn chức năng: ");
                int choice = Integer.parseInt(sc.nextLine());

                if (choice == 0) break;
                switch (choice) {
                    case 1:
                        List<Flight> list = service.getAllFlights();
                        System.out.println("\n--- DANH SÁCH CHUYẾN BAY ---");
                        for (Flight f : list) {
                            System.out.println(f);
                        }
                        break;
                    case 2:
                        System.out.print("Nhập mã chuyến bay (ví dụ VN123, VJ456, QH789): ");
                        String code = sc.nextLine();
                        System.out.print("Nhập số lượng vé cần đặt: ");
                        int num = Integer.parseInt(sc.nextLine());
                        boolean booked = service.bookTicket(code, num);
                        if (booked) {
                            System.out.println("-> Đặt vé thành công! Chỗ còn lại: " + service.availableSeats(code));
                        } else {
                            System.out.println("-> Đặt vé thất bại! Chuyến bay không tồn tại hoặc không đủ ghế.");
                        }
                        break;
                    case 3:
                        System.out.print("Nhập mã chuyến bay: ");
                        String cCode = sc.nextLine();
                        System.out.print("Nhập số vé muốn hủy: ");
                        int cNum = Integer.parseInt(sc.nextLine());
                        boolean canceled = service.cancelBooking(cCode, cNum);
                        if (canceled) {
                            System.out.println("-> Hủy vé thành công! Chỗ còn lại: " + service.availableSeats(cCode));
                        } else {
                            System.out.println("-> Hủy vé thất bại! Số vé hủy vượt quá số vé đã đặt hoặc mã sai.");
                        }
                        break;
                    default:
                        System.out.println("Lựa chọn không hợp lệ!");
                }
            }
        } catch (Exception e) {
            System.err.println("[FlightBookingClient] Lỗi: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}
