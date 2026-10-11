package Muc_4_2_TcpCommand;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

/**
 * Mục thực hành 4.2: TCP Command Client
 * Kết nối tới TCP server, nhận lệnh từ bàn phím, gửi lên server và hiển thị phản hồi.
 */
public class TcpCommandClient {

    public static void main(String[] args) {
        // Nhận host và port từ tham số dòng lệnh (nếu không truyền thì lấy mặc định localhost:5000)
        String host = args.length > 0 ? args[0] : "localhost";
        int port = 5000;

        if (args.length > 1) {
            try {
                port = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                System.err.println("Lỗi: Port phải là số nguyên hợp lệ.");
                return;
            }
        }

        // Mở kết nối Socket tới Server với try-with-resources để tự động đóng tài nguyên khi xong
        try (Socket socket = new Socket(host, port);
             // Luồng đọc từ bàn phím người dùng
             BufferedReader console = new BufferedReader(
                     new InputStreamReader(System.in, StandardCharsets.UTF_8));
             // Luồng đọc phản hồi từ server
             BufferedReader in = new BufferedReader(
                     new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
             // Luồng gửi dữ liệu lên server
             PrintWriter out = new PrintWriter(
                     new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true)) {

            System.out.println("Đã kết nối tới Server (" + host + ":" + port + ").");
            System.out.println("Các lệnh hợp lệ: PING, TIME, UPPER <văn bản>, QUIT");

            String request;
            // Đọc từng dòng người dùng nhập từ bàn phím
            while ((request = console.readLine()) != null) {
                // Gửi chuỗi yêu cầu lên server
                out.println(request);

                // Đợi đọc phản hồi một dòng từ server
                String response = in.readLine();
                if (response == null) {
                    System.out.println("Server đã đóng kết nối.");
                    break;
                }

                System.out.println("Server: " + response);

                // Nếu người dùng vừa gõ lệnh QUIT thì thoát vòng lặp
                if (request.trim().equalsIgnoreCase("QUIT")) {
                    System.out.println("Đã ngắt kết nối theo yêu cầu.");
                    break;
                }
            }

        } catch (IOException e) {
            System.err.println("Lỗi kết nối tới server: " + e.getMessage());
        }
    }
}
