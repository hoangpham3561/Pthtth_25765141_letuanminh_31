package Bai_Tap_2_DoiChuSoThanhChu;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

/**
 * Bài 2: TCP Server đổi chữ số thành chữ tiếng Việt.
 * Cổng: 5002
 * Giao thức:
 * - Client gửi ký tự từ '0' đến '9' -> Server trả về "không", "một", ..., "chín".
 * - Nếu không phải đúng duy nhất 1 chữ số -> Trả về "ERR INVALID_DIGIT".
 * - Cho phép gửi nhiều yêu cầu cho đến khi nhận "QUIT" -> Trả về "OK BYE" và đóng kết nối.
 * - Ca biên: 0, 9, chuỗi rỗng, 10, ký tự 'a', dữ liệu có khoảng trắng.
 */
public class DigitToWordServer {

    public static final int DEFAULT_PORT = 5002;
    private static final String[] DIGIT_WORDS = {
            "không", "một", "hai", "ba", "bốn",
            "năm", "sáu", "bảy", "tám", "chín"
    };

    public static void main(String[] args) {
        int port = args.length > 0 ? Integer.parseInt(args[0]) : DEFAULT_PORT;

        try (ServerSocket server = new ServerSocket(port)) {
            System.out.println("DigitToWordServer đang lắng nghe trên cổng " + port + "...");

            while (true) {
                Socket socket = server.accept();
                System.out.println("Client đã kết nối: " + socket.getRemoteSocketAddress());

                // Xử lý client trong luồng mới để có thể phục vụ nhiều client
                new Thread(() -> handleClient(socket)).start();
            }

        } catch (IOException e) {
            System.err.println("Lỗi Server: " + e.getMessage());
        }
    }

    private static void handleClient(Socket socket) {
        try (socket;
             BufferedReader in = new BufferedReader(new InputStreamReader(
                     socket.getInputStream(), StandardCharsets.UTF_8));
             PrintWriter out = new PrintWriter(new OutputStreamWriter(
                     socket.getOutputStream(), StandardCharsets.UTF_8), true)) {

            String request;
            while ((request = in.readLine()) != null) {
                // Kiểm tra lệnh QUIT
                if (request.trim().equalsIgnoreCase("QUIT")) {
                    out.println("OK BYE");
                    break;
                }

                // Xử lý chuyển đổi chữ số
                String response = convertDigitToWord(request);
                out.println(response);
            }

        } catch (IOException e) {
            System.err.println("Lỗi phiên làm việc: " + e.getMessage());
        } finally {
            System.out.println("Client đã ngắt kết nối.");
        }
    }

    /**
     * Quy tắc chuyển đổi:
     * Dữ liệu phải có độ dài đúng bằng 1 và là ký tự số từ '0' đến '9'.
     * Bất kỳ trường hợp nào khác (chuỗi rỗng, có khoảng trắng, nhiều hơn 1 ký tự, ký tự chữ 'a')
     * đều trả về ERR INVALID_DIGIT.
     */
    public static String convertDigitToWord(String input) {
        if (input == null || input.length() != 1) {
            return "ERR INVALID_DIGIT";
        }

        char ch = input.charAt(0);
        if (ch >= '0' && ch <= '9') {
            int digit = ch - '0';
            return DIGIT_WORDS[digit];
        }

        return "ERR INVALID_DIGIT";
    }
}
