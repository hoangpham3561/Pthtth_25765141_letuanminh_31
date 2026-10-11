package Bai_Tap_5_ChatTcpNhieuClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Bài 5: Chat TCP Server hỗ trợ nhiều client.
 * Cổng: 5006
 * Đặc điểm kỹ thuật:
 * 1. Dùng ConcurrentHashMap để lưu danh sách các client (nickname -> PrintWriter) đảm bảo Thread-safe.
 * 2. Dùng ExecutorService quản lý hồ bơi luồng.
 * 3. Hỗ trợ các lệnh:
 *    - NICK <nickname>: đăng ký tên khi mới vào (phải là duy nhất)
 *    - USERS: xem danh sách các client đang trực tuyến
 *    - MSG <nội dung>: gửi tin nhắn quảng bá (broadcast) tới mọi người
 *    - QUIT: rời phòng chat
 * 4. Tự động dọn dẹp và thông báo khi client ngắt kết nối đột ngột.
 */
public class ChatServer {

    private static final int PORT = 5006;
    private static final int MAX_THREADS = 50;

    // Bảng lưu trữ tên người dùng và luồng gửi tin nhắn của họ (Thread-safe)
    private static final ConcurrentHashMap<String, PrintWriter> activeClients = new ConcurrentHashMap<>();

    public static void main(String[] args) {
        ExecutorService pool = Executors.newFixedThreadPool(MAX_THREADS);

        try (ServerSocket server = new ServerSocket(PORT)) {
            System.out.println("ChatServer đang chạy trên cổng " + PORT + "...");

            while (true) {
                Socket socket = server.accept();
                pool.submit(() -> handleClient(socket));
            }

        } catch (IOException e) {
            System.err.println("Lỗi Chat Server: " + e.getMessage());
        } finally {
            pool.shutdown();
        }
    }

    private static void handleClient(Socket socket) {
        String clientNick = null;
        PrintWriter out = null;

        try (socket;
             BufferedReader in = new BufferedReader(new InputStreamReader(
                     socket.getInputStream(), StandardCharsets.UTF_8))) {

            out = new PrintWriter(new OutputStreamWriter(
                    socket.getOutputStream(), StandardCharsets.UTF_8), true);

            out.println("WELCOME! Vui lòng đặt nickname bằng lệnh: NICK <tên_của_bạn>");

            // Vòng lặp đăng ký Nickname duy nhất
            while (true) {
                String line = in.readLine();
                if (line == null) {
                    return; // Client ngắt trước khi đăng ký
                }

                line = line.trim();
                if (line.toUpperCase().startsWith("NICK ")) {
                    String candidate = line.substring(5).trim();
                    if (candidate.isEmpty()) {
                        out.println("ERR NICK_EMPTY");
                    } else if (activeClients.containsKey(candidate)) {
                        out.println("ERR NICK_TAKEN. Vui lòng chọn tên khác!");
                    } else {
                        clientNick = candidate;
                        activeClients.put(clientNick, out);
                        out.println("OK NICK_ACCEPTED " + clientNick);
                        broadcast("[Hệ thống] " + clientNick + " đã tham gia phòng chat!", clientNick);
                        break;
                    }
                } else if (line.equalsIgnoreCase("QUIT")) {
                    out.println("OK BYE");
                    return;
                } else {
                    out.println("ERR PLEASE_SET_NICK_FIRST");
                }
            }

            // Vòng lặp nhận các lệnh trò chuyện sau khi đã có Nickname
            String message;
            while ((message = in.readLine()) != null) {
                message = message.trim();

                if (message.equalsIgnoreCase("QUIT")) {
                    out.println("OK BYE");
                    break;
                } else if (message.equalsIgnoreCase("USERS")) {
                    // Trả về danh sách nickname online
                    out.println("ONLINE USERS: " + String.join(", ", activeClients.keySet()));
                } else if (message.toUpperCase().startsWith("MSG ")) {
                    String content = message.substring(4).trim();
                    broadcast("[" + clientNick + "]: " + content, clientNick);
                    out.println("OK MSG_SENT");
                } else {
                    out.println("ERR UNKNOWN_COMMAND. Các lệnh hỗ trợ: USERS, MSG <nội dung>, QUIT");
                }
            }

        } catch (IOException e) {
            System.err.println("Lỗi kết nối từ client (" + clientNick + "): " + e.getMessage());
        } finally {
            // Dọn dẹp tài nguyên khi client thoát hoặc ngắt đột ngột
            if (clientNick != null) {
                activeClients.remove(clientNick);
                broadcast("[Hệ thống] " + clientNick + " đã rời khỏi phòng chat.", null);
                System.out.println("[-] Client đã thoát: " + clientNick);
            }
        }
    }

    /**
     * Gửi tin nhắn đến tất cả các client đang online (trừ người gửi nếu cần)
     */
    private static void broadcast(String message, String senderNick) {
        System.out.println("[BROADCAST] " + message);
        for (var entry : activeClients.entrySet()) {
            // Không gửi lại cho chính người vừa nhắn (nếu có chỉ định senderNick)
            if (senderNick != null && entry.getKey().equals(senderNick)) {
                continue;
            }
            try {
                entry.getValue().println(message);
            } catch (Exception e) {
                // Bỏ qua lỗi gửi lẻ tẻ
            }
        }
    }
}
