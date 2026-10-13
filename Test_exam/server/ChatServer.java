import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * ============================================================================
 * CHƯƠNG TRÌNH CHAT NHÓM QUA TCP THEO PHÒNG (CHAT SERVER)
 * ============================================================================
 * Kế thừa trực tiếp từ kiến trúc Bài tập 5 (lab4_tcp):
 * 1. Quản lý luồng bằng ExecutorService (Thread Pool) thay vì tạo luồng vô hạn.
 * 2. Lưu trữ danh sách Client an toàn đa luồng bằng ConcurrentHashMap.
 * 3. Đọc/ghi dữ liệu phân dòng chuẩn mã hóa StandardCharsets.UTF_8.
 * 4. Dọn dẹp tài nguyên an toàn trong khối finally khi client ngắt kết nối.
 *
 * Phát triển nâng cấp theo đề bài mới:
 * 1. Quản lý nhiều phòng chat (Study, Game, TraiNghiem, hoặc tự đề xuất phòng mới).
 * 2. Phân tách tin nhắn: Chỉ gửi tin tới các client trong CÙNG PHÒNG đó.
 * 3. Hỗ trợ lệnh /quit để rời phòng hiện tại và quay lại danh sách chọn phòng.
 * 4. Hỗ trợ lệnh /exit để ngắt kết nối hoàn toàn tới Server.
 * 5. Tích hợp trung tâm giám sát Dashboard (Realtime log & Thống kê phòng).
 * ============================================================================
 */
public class ChatServer {

    // Cổng mặc định 5006 (tránh xung đột cổng 5000 AirPlay của macOS)
    private static final int PORT = 5006;
    private static final int MAX_THREADS = 50;

    // Danh sách các phòng chat mặc định (Thread-safe)
    private static final List<String> roomList = Collections.synchronizedList(
            new ArrayList<>(Arrays.asList("Study", "Game", "TraiNghiem"))
    );

    // Class lưu thông tin của mỗi Client (nickname, luồng gửi tin PrintWriter, và phòng hiện tại)
    public static class ClientInfo {
        public final String nick;
        public final PrintWriter out;
        public volatile String room; // Phòng hiện tại mà client đang tham gia

        public ClientInfo(String nick, PrintWriter out, String room) {
            this.nick = nick;
            this.out = out;
            this.room = room;
        }
    }

    // Bảng lưu trữ client online: Nickname -> ClientInfo (Thread-safe, kế thừa từ lab4_tcp)
    private static final ConcurrentHashMap<String, ClientInfo> activeClients = new ConcurrentHashMap<>();

    // Danh sách các kết nối Dashboard giám sát (Thread-safe)
    private static final List<PrintWriter> dashboardClients = Collections.synchronizedList(new ArrayList<>());

    public static void main(String[] args) {
        int serverPort = PORT;
        if (args.length > 0) {
            try {
                serverPort = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                System.out.println("Cổng không hợp lệ, dùng cổng mặc định: " + PORT);
            }
        }

        // Dùng Thread Pool như trong Bài 5 của lab4_tcp
        ExecutorService pool = Executors.newFixedThreadPool(MAX_THREADS);

        try (ServerSocket server = new ServerSocket(serverPort)) {
            System.out.println("==================================================");
            System.out.println("   CHAT TCP SERVER ĐANG CHẠY TRÊN CỔNG: " + serverPort);
            System.out.println("   Đang chờ các client và dashboard kết nối...");
            System.out.println("==================================================");

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

    /**
     * Gửi thông báo sự kiện theo thời gian thực tới tất cả các Dashboard đang mở
     */
    public static void notifyDashboards(String logMessage) {
        synchronized (dashboardClients) {
            for (PrintWriter out : dashboardClients) {
                try {
                    out.println("LOG: " + logMessage);
                } catch (Exception ignored) {}
            }
        }
    }

    /**
     * Gửi số liệu thống kê (phòng và danh sách người dùng) tới Dashboard
     */
    public static void sendDashboardStats(PrintWriter out) {
        StringBuilder sb = new StringBuilder();
        sb.append("STATS_BEGIN\n");
        sb.append("Tổng số Client online: ").append(activeClients.size()).append("\n");
        sb.append("--------------------------------------------------\n");
        synchronized (roomList) {
            for (String r : roomList) {
                List<String> users = new ArrayList<>();
                for (ClientInfo c : activeClients.values()) {
                    if (r.equalsIgnoreCase(c.room)) {
                        users.add(c.nick);
                    }
                }
                sb.append("• Phòng [").append(r).append("] (").append(users.size()).append(" người): ")
                  .append(users.isEmpty() ? "(Trống)" : String.join(", ", users))
                  .append("\n");
            }
        }
        sb.append("STATS_END");
        out.println(sb.toString());
    }

    /**
     * Gửi thông báo Admin từ Dashboard tới TẤT CẢ các Client trong mọi phòng
     */
    public static void broadcastAllRooms(String message) {
        for (ClientInfo client : activeClients.values()) {
            if (client.room != null) {
                try {
                    client.out.println(message);
                } catch (Exception ignored) {}
            }
        }
        notifyDashboards("[ADMIN TOÀN HỆ THỐNG]: " + message);
        System.out.println("[ADMIN BROADCAST] " + message);
    }

    /**
     * Xử lý kết nối từ trung tâm giám sát Dashboard
     */
    private static void handleDashboardClient(Socket socket, BufferedReader in, PrintWriter out) {
        dashboardClients.add(out);
        System.out.println("[*] Dashboard giám sát đã kết nối từ Port: " + socket.getPort());
        out.println("OK DASHBOARD_CONNECTED");
        sendDashboardStats(out);

        try {
            String command;
            while ((command = in.readLine()) != null) {
                command = command.trim();
                if (command.equalsIgnoreCase("STATS")) {
                    sendDashboardStats(out);
                } else if (command.toUpperCase().startsWith("BROADCAST ")) {
                    String msg = command.substring(10).trim();
                    broadcastAllRooms("[THÔNG BÁO TỪ HỆ THỐNG]: " + msg);
                } else if (command.equalsIgnoreCase("/exit")) {
                    break;
                }
            }
        } catch (IOException ignored) {
        } finally {
            dashboardClients.remove(out);
            try {
                socket.close();
            } catch (IOException ignored) {}
            System.out.println("[-] Dashboard đã ngắt kết nối.");
        }
    }

    /**
     * Xử lý kết nối và phiên làm việc của từng Client (Kế thừa từ lab4_tcp handleClient)
     */
    private static void handleClient(Socket socket) {
        String clientNick = null;
        PrintWriter out = null;

        try (socket;
             BufferedReader in = new BufferedReader(new InputStreamReader(
                     socket.getInputStream(), StandardCharsets.UTF_8))) {

            out = new PrintWriter(new OutputStreamWriter(
                    socket.getOutputStream(), StandardCharsets.UTF_8), true);

            // 1. Nhận tên của Client khi kết nối
            clientNick = in.readLine();
            if (clientNick == null || clientNick.trim().isEmpty()) {
                clientNick = "Client_" + socket.getPort();
            } else {
                clientNick = clientNick.trim();
            }

            // Nếu kết nối này là Dashboard giám sát
            if (clientNick.equalsIgnoreCase("DASHBOARD") || clientNick.equalsIgnoreCase("ADMIN")) {
                handleDashboardClient(socket, in, out);
                return;
            }

            // Xử lý nếu tên bị trùng: Tự động thêm số đếm (ví dụ: An_1, An_2) rất dễ hiểu cho Newbie
            String originalNick = clientNick;
            int counter = 1;
            while (activeClients.containsKey(clientNick)) {
                clientNick = originalNick + "_" + counter;
                counter++;
            }
            if (!clientNick.equals(originalNick)) {
                out.println(">> Lưu ý: Tên [" + originalNick + "] đã có người dùng. Tên của bạn được đổi thành: [" + clientNick + "]");
            }

            // Đăng ký client vào bảng activeClients (chưa vào phòng: room = null)
            ClientInfo clientInfo = new ClientInfo(clientNick, out, null);
            activeClients.put(clientNick, clientInfo);

            System.out.println("[+] Client kết nối: " + clientNick + " (Port: " + socket.getPort() + ")");
            notifyDashboards("[KẾT NỐI] " + clientNick + " vừa kết nối tới Server.");
            out.println("\n>> Xin chào [" + clientNick + "]! Đã kết nối thành công tới Server.");

            // 2. Vòng lặp chính: Chọn phòng và trò chuyện
            while (true) {
                // Hiển thị danh sách phòng do server cung cấp
                out.println(getRoomListMenu());

                String roomChoice = in.readLine();
                if (roomChoice == null) {
                    return; // Client ngắt kết nối đột ngột
                }

                roomChoice = roomChoice.trim();

                // Lệnh ngắt kết nối tại menu phòng
                if (roomChoice.equalsIgnoreCase("/exit")) {
                    out.println(">> Tạm biệt! Đã ngắt kết nối tới server.");
                    return;
                }

                // Nếu người dùng gõ /quit khi chưa vào phòng nào
                if (roomChoice.equalsIgnoreCase("/quit")) {
                    out.println(">> Bạn chưa tham gia phòng nào để rời! Vui lòng chọn một phòng trong danh sách bên dưới.");
                    continue;
                }

                if (roomChoice.isEmpty()) {
                    continue;
                }

                // Xác định phòng được chọn (hoặc đề xuất tên phòng mới)
                String selectedRoom = resolveRoom(roomChoice);
                if (selectedRoom == null) {
                    out.println(">> Lựa chọn phòng không hợp lệ, vui lòng thử lại!");
                    continue;
                }

                // Cập nhật phòng cho client
                clientInfo.room = selectedRoom;

                out.println("\n==================================================");
                out.println("   BẠN ĐÃ THAM GIA PHÒNG: [" + clientInfo.room + "]");
                out.println("   - Gõ tin nhắn và Enter để gửi đến mọi người trong phòng.");
                out.println("   - Nhập /quit để rời phòng và quay lại danh sách phòng.");
                out.println("   - Nhập /exit để ngắt kết nối hoàn toàn tới server.");
                out.println("==================================================");

                // Thông báo cho các client khác trong cùng phòng và báo cho Dashboard
                broadcastToRoom("[Hệ thống]: " + clientNick + " đã tham gia phòng!", clientInfo.room, clientNick);
                notifyDashboards("[VÀO PHÒNG] " + clientNick + " đã tham gia phòng [" + clientInfo.room + "].");
                System.out.println("[Room " + clientInfo.room + "] " + clientNick + " đã vào phòng.");

                // 3. Vòng lặp chat bên trong phòng
                boolean inRoom = true;
                while (inRoom) {
                    String message = in.readLine();
                    if (message == null) {
                        return; // Ngắt kết nối đột ngột
                    }

                    message = message.trim();
                    if (message.isEmpty()) {
                        continue;
                    }

                    // Xử lý lệnh /quit: Rời phòng, quay lại danh sách phòng
                    if (message.equalsIgnoreCase("/quit")) {
                        String oldRoom = clientInfo.room;
                        clientInfo.room = null; // Đặt lại trạng thái chưa vào phòng
                        broadcastToRoom("[Hệ thống]: " + clientNick + " đã rời phòng.", oldRoom, clientNick);
                        notifyDashboards("[RỜI PHÒNG] " + clientNick + " đã rời phòng [" + oldRoom + "].");
                        System.out.println("[Room " + oldRoom + "] " + clientNick + " đã rời phòng (/quit).");
                        out.println(">> Bạn đã rời phòng [" + oldRoom + "].");
                        inRoom = false; // Thoát vòng lặp phòng chat, quay lại menu phòng
                    }
                    // Xử lý lệnh /exit: Ngắt kết nối tới Server
                    else if (message.equalsIgnoreCase("/exit")) {
                        String oldRoom = clientInfo.room;
                        clientInfo.room = null; // Đặt null để finally không lặp thông báo
                        broadcastToRoom("[Hệ thống]: " + clientNick + " đã ngắt kết nối.", oldRoom, clientNick);
                        notifyDashboards("[NGẮT KẾT NỐI] " + clientNick + " đã ngắt kết nối khỏi Server.");
                        System.out.println("[Room " + oldRoom + "] " + clientNick + " đã ngắt kết nối (/exit).");
                        out.println(">> Tạm biệt! Đã ngắt kết nối tới server.");
                        return; // Thoát run, vào khối finally dọn dẹp
                    }
                    // Tin nhắn bình thường: Gửi đến các client khác TRONG CÙNG PHÒNG ĐÓ
                    else {
                        broadcastToRoom("[" + clientNick + "]: " + message, clientInfo.room, clientNick);
                        notifyDashboards("[CHAT - " + clientInfo.room + "] [" + clientNick + "]: " + message);
                        System.out.println("[Room " + clientInfo.room + "] [" + clientNick + "]: " + message);
                    }
                }
            }

        } catch (IOException e) {
            System.err.println("Lỗi kết nối từ client (" + clientNick + "): " + e.getMessage());
        } finally {
            // Dọn dẹp tài nguyên khi client thoát hoặc ngắt đột ngột (Kế thừa từ lab4_tcp)
            if (clientNick != null) {
                ClientInfo info = activeClients.remove(clientNick);
                if (info != null && info.room != null) {
                    broadcastToRoom("[Hệ thống]: " + clientNick + " đã rời phòng do ngắt kết nối.", info.room, clientNick);
                    notifyDashboards("[MẤT KẾT NỐI] " + clientNick + " đã rời phòng do mất kết nối.");
                }
                System.out.println("[-] Client đã thoát: " + clientNick);
            }
        }
    }

    /**
     * Gửi tin nhắn tới tất cả client khác trong CÙNG PHÒNG đó
     * (Nâng cấp từ hàm broadcast() toàn cục của lab4_tcp thành broadcast theo phòng)
     */
    private static void broadcastToRoom(String message, String room, String senderNick) {
        if (room == null) return;
        for (var entry : activeClients.values()) {
            // Điều kiện: Không gửi lại cho người gửi VÀ phải ở cùng phòng
            if (!entry.nick.equals(senderNick) && room.equalsIgnoreCase(entry.room)) {
                try {
                    entry.out.println(message);
                } catch (Exception ignored) {}
            }
        }
    }

    /**
     * Trả về giao diện menu danh sách phòng
     */
    private static String getRoomListMenu() {
        StringBuilder sb = new StringBuilder();
        sb.append("\n================ DANH SÁCH CÁC PHÒNG CHAT ================\n");
        synchronized (roomList) {
            for (int i = 0; i < roomList.size(); i++) {
                sb.append("  ").append(i + 1).append(". ").append(roomList.get(i)).append("\n");
            }
        }
        sb.append("----------------------------------------------------------\n");
        sb.append("(*) Nhập số thứ tự (1, 2,...) hoặc tên phòng để tham gia.\n");
        sb.append("(*) Hoặc gõ một tên mới để tự đề xuất phòng chat mới!\n");
        sb.append("(*) Nhập /exit để ngắt kết nối tới server.\n");
        sb.append("Lựa chọn của bạn: ");
        return sb.toString();
    }

    /**
     * Xử lý lựa chọn phòng: Cho phép chọn theo số, tên phòng có sẵn hoặc đề xuất phòng mới
     */
    private static String resolveRoom(String input) {
        if (input == null || input.trim().isEmpty()) {
            return null;
        }
        String clean = input.trim();

        synchronized (roomList) {
            // 1. Kiểm tra chọn theo số thứ tự
            try {
                int idx = Integer.parseInt(clean);
                if (idx >= 1 && idx <= roomList.size()) {
                    return roomList.get(idx - 1);
                }
            } catch (NumberFormatException ignored) {}

            // 2. Kiểm tra chọn theo tên phòng có sẵn
            for (String r : roomList) {
                if (r.equalsIgnoreCase(clean)) {
                    return r;
                }
            }

            // 3. Đề xuất phòng mới: Tự động thêm vào danh sách phòng của server
            roomList.add(clean);
            System.out.println("[*] Server đã tạo thêm phòng chat mới: " + clean);
            notifyDashboards("[PHÒNG MỚI] Phòng chat mới [" + clean + "] vừa được tạo.");
            return clean;
        }
    }
}
