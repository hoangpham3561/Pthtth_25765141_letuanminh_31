import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

/**
 * ============================================================================
 * CHƯƠNG TRÌNH CHAT NHÓM QUA GIAO THỨC TCP (CLIENT)
 * ============================================================================
 * Mô hình 2 luồng (Multi-threading) cơ bản cho Newbie:
 * 1. Luồng nhận (Receive Thread): Chạy ngầm liên tục lắng nghe tin nhắn từ Server
 *    và in ra màn hình console.
 * 2. Luồng gửi (Main Thread): Chờ người dùng gõ phím và gửi dữ liệu lên Server.
 * 
 * Luồng hoạt động:
 * - Người dùng nhập Tên -> Kết nối tới Server.
 * - Server gửi danh sách phòng -> Người dùng chọn phòng (1, 2, 3... hoặc tên phòng).
 * - Vào phòng: Gõ tin nhắn để gửi cho các thành viên khác trong phòng.
 * - Nhập /quit : Rời phòng, quay lại danh sách chọn phòng.
 * - Nhập /exit : Ngắt kết nối và thoát chương trình.
 * ============================================================================
 */
public class ChatClient {

    private static final String DEFAULT_HOST = "localhost";
    // Cổng mặc định 5006 đồng bộ với ChatServer (tránh trùng cổng 5000 của macOS AirPlay)
    private static final int DEFAULT_PORT = 5006;

    public static void main(String[] args) {
        String host = DEFAULT_HOST;
        int port = DEFAULT_PORT;

        // Cho phép truyền địa chỉ Host và Cổng qua tham số dòng lệnh
        if (args.length >= 1) {
            host = args[0];
        }
        if (args.length >= 2) {
            try {
                port = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                System.out.println("Cổng không hợp lệ, dùng cổng mặc định: " + DEFAULT_PORT);
            }
        }

        try {
            // Chuẩn bị luồng đọc từ bàn phím người dùng
            BufferedReader console = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8));

            // 1. Nhập tên của Client trước khi kết nối theo đúng yêu cầu đề bài
            System.out.println("==================================================");
            System.out.println("       CHƯƠNG TRÌNH CHAT NHÓM TCP (CLIENT)");
            System.out.println("==================================================");
            System.out.print("Vui lòng nhập Tên của bạn: ");
            String clientName = console.readLine();

            while (clientName == null || clientName.trim().isEmpty()) {
                System.out.print("Tên không được để trống. Vui lòng nhập lại: ");
                clientName = console.readLine();
            }
            clientName = clientName.trim();

            // 2. Kết nối tới Chat Server
            System.out.println("Đang kết nối tới Server (" + host + ":" + port + ")...");
            Socket socket = new Socket(host, port);
            System.out.println(">> Kết nối thành công!");

            // Thiết lập luồng I/O mạng với bảng mã UTF-8
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
            PrintWriter out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true);

            // 3. Gửi tên đã nhập lên Server
            out.println(clientName);

            // 4. Luồng phụ: Liên tục nhận tin nhắn từ Server và in ra màn hình
            Thread receiveThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        String serverMessage;
                        while ((serverMessage = in.readLine()) != null) {
                            System.out.println(serverMessage);
                        }
                    } catch (IOException e) {
                        // Kết nối bị đóng hoặc gặp lỗi mạng
                    } finally {
                        System.out.println("\n>> Đã ngắt kết nối khỏi Server.");
                        try {
                            if (!socket.isClosed()) {
                                socket.close();
                            }
                        } catch (IOException ignored) {}
                        System.exit(0);
                    }
                }
            });
            // Đặt là daemon để tự kết thúc khi tiến trình chính kết thúc
            receiveThread.setDaemon(true);
            receiveThread.start();

            // 5. Luồng chính (Main Thread): Đọc dữ liệu từ bàn phím và gửi lên Server
            String userInput;
            while ((userInput = console.readLine()) != null) {
                // Gửi nội dung người dùng vừa gõ lên server
                out.println(userInput);

                // Nếu người dùng nhập /exit -> chủ động thoát chương trình
                if (userInput.trim().equalsIgnoreCase("/exit")) {
                    System.out.println("Đang ngắt kết nối tới Server...");
                    // Chờ một chút để thông điệp kịp gửi và server phản hồi
                    try {
                        Thread.sleep(300);
                    } catch (InterruptedException ignored) {}
                    break;
                }
            }

            // Đóng socket và kết thúc
            if (!socket.isClosed()) {
                socket.close();
            }
            System.out.println("Chương trình chat đã kết thúc. Tạm biệt!");

        } catch (IOException e) {
            System.err.println("Lỗi: Không thể kết nối tới Server tại " + host + ":" + port);
            System.err.println("Chi tiết: " + e.getMessage());
            System.err.println("Gợi ý: Hãy đảm bảo ChatServer đã được khởi động trước khi bật ChatClient!");
        }
    }
}
