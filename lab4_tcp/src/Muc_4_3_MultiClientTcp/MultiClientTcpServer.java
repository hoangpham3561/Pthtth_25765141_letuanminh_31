package Muc_4_3_MultiClientTcp;

import Muc_4_2_TcpCommand.TcpCommandServer;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Mục thực hành 4.3: TCP Server phục vụ đồng thời nhiều client (Multi-client).
 * Sử dụng ExecutorService (Fixed Thread Pool) thay vì tạo Thread mới thủ công mỗi khi có client.
 * Điều này giúp kiểm soát tài nguyên hệ thống, tránh bị cạn kiệt bộ nhớ khi có quá nhiều kết nối.
 */
public class MultiClientTcpServer {

    private static final int DEFAULT_PORT = 5000;
    // Giới hạn số lượng client tối đa được phục vụ cùng một thời điểm trong pool
    private static final int MAX_CLIENTS = 20;

    public static void main(String[] args) {
        int port = args.length > 0 ? Integer.parseInt(args[0]) : DEFAULT_PORT;
        // Tạo hồ bơi luồng (thread pool) cố định gồm 20 worker threads
        ExecutorService pool = Executors.newFixedThreadPool(MAX_CLIENTS);

        try (ServerSocket server = new ServerSocket(port)) {
            System.out.println("Multi-client server đang lắng nghe trên port " + port + " (Tối đa " + MAX_CLIENTS + " luồng)...");

            while (true) {
                // Chấp nhận một client mới
                Socket socket = server.accept();

                // Đưa tác vụ xử lý client này vào thread pool chạy ngầm
                pool.submit(() -> {
                    String clientAddress = String.valueOf(socket.getRemoteSocketAddress());
                    System.out.println("[+] Client kết nối: " + clientAddress);

                    // Sử dụng try-with-resources để đảm bảo socket của client được đóng khi xong phiên
                    try (socket) {
                        // Tái sử dụng hàm serve() đã viết ở TcpCommandServer
                        TcpCommandServer.serve(socket);
                    } catch (IOException e) {
                        System.err.println("[-] Client " + clientAddress + " gặp lỗi: " + e.getMessage());
                    } finally {
                        System.out.println("[-] Client ngắt kết nối: " + clientAddress);
                    }
                });
            }

        } catch (IOException e) {
            System.err.println("Lỗi Server: " + e.getMessage());
        } finally {
            // Khi server dừng, giải phóng thread pool
            pool.shutdown();
        }
    }
}
