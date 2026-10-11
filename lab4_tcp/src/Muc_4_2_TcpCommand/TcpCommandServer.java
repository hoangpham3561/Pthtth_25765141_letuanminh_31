package Muc_4_2_TcpCommand;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Locale;

/**
 * Mục thực hành 4.2: TCP Server theo giao thức dòng văn bản (line-based protocol).
 * Server này lắng nghe tại cổng 5000, phục vụ tuần tự từng client (single-threaded).
 * Mỗi thông điệp gửi/nhận kết thúc bằng ký tự xuống dòng (\n) và mã hóa UTF-8.
 */
public class TcpCommandServer {

    public static final int DEFAULT_PORT = 5000;

    public static void main(String[] args) {
        int port = args.length > 0 ? Integer.parseInt(args[0]) : DEFAULT_PORT;

        // Mở ServerSocket lắng nghe tại port
        // Dùng cú pháp try-with-resources để tự động đóng socket khi tắt chương trình
        try (ServerSocket server = new ServerSocket(port)) {
            System.out.println("TCP server đang lắng nghe trên cổng " + port + "...");

            // Vòng lặp vô tận để liên tục chấp nhận kết nối từ các client
            while (true) {
                // server.accept() sẽ tạm dừng (block) cho đến khi có một client kết nối tới
                try (Socket socket = server.accept()) {
                    System.out.println("Đã kết nối với client: " + socket.getRemoteSocketAddress());
                    // Gọi hàm xử lý phiên làm việc với client vừa kết nối
                    serve(socket);
                } catch (IOException e) {
                    System.err.println("Lỗi trong phiên làm việc với client: " + e.getMessage());
                }
            }

        } catch (IOException e) {
            System.err.println("Không thể khởi động server: " + e.getMessage());
        }
    }

    /**
     * Phương thức phục vụ một kết nối socket từ client.
     * Phương thức này là public/package-private để MultiClientTcpServer ở mục 4.3 có thể tái sử dụng.
     */
    public static void serve(Socket socket) throws IOException {
        // Tạo luồng đọc (in) dữ liệu từ client với chuẩn mã hóa UTF-8
        try (BufferedReader in = new BufferedReader(new InputStreamReader(
                socket.getInputStream(), StandardCharsets.UTF_8));
             // Tạo luồng ghi (out) gửi dữ liệu về client, autoFlush = true để đẩy dữ liệu đi ngay
             PrintWriter out = new PrintWriter(new OutputStreamWriter(
                     socket.getOutputStream(), StandardCharsets.UTF_8), true)) {

            String request;
            // Đọc từng dòng mà client gửi lên (cho đến khi client ngắt kết nối hoặc readLine trả về null)
            while ((request = in.readLine()) != null) {
                // Xử lý nội dung yêu cầu để tạo ra phản hồi
                String response = process(request);

                // Gửi phản hồi lại cho client
                out.println(response);

                // Nếu client yêu cầu QUIT thì kết thúc vòng lặp để đóng kết nối socket này
                if (request.trim().equalsIgnoreCase("QUIT")) {
                    break;
                }
            }
        }
    }

    /**
     * Phương thức xử lý lệnh theo giao thức:
     * - PING -> OK PONG
     * - TIME -> OK <ngày giờ hiện tại>
     * - QUIT -> OK BYE
     * - UPPER <văn bản> -> OK <VĂN BẢN IN HOA>
     * - Lệnh khác -> ERR UNKNOWN_COMMAND
     */
    public static String process(String request) {
        String trimmed = request.trim();

        // 1. Kiểm tra lệnh PING
        if (trimmed.equalsIgnoreCase("PING")) {
            return "OK PONG";
        }

        // 2. Kiểm tra lệnh TIME
        if (trimmed.equalsIgnoreCase("TIME")) {
            return "OK " + LocalDateTime.now();
        }

        // 3. Kiểm tra lệnh QUIT
        if (trimmed.equalsIgnoreCase("QUIT")) {
            return "OK BYE";
        }

        // 4. Kiểm tra lệnh UPPER <văn bản>
        // regionMatches(true, 0, "UPPER ", 0, 6) so sánh không phân biệt hoa thường tiền tố "UPPER "
        if (trimmed.regionMatches(true, 0, "UPPER ", 0, 6)) {
            String content = trimmed.substring(6);
            return "OK " + content.toUpperCase(Locale.ROOT);
        }

        // 5. Nếu không khớp bất kỳ lệnh nào hợp lệ
        return "ERR UNKNOWN_COMMAND";
    }
}
