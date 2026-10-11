package Bai_Tap_5_ChatTcpNhieuClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

/**
 * Bài 5: Chat TCP Client đa luồng.
 * Luồng 1 (Thread đọc): Lắng nghe tin nhắn từ Server và in ra màn hình.
 * Luồng 2 (Main Thread): Đọc từ bàn phím người dùng và gửi đến Server.
 */
public class ChatClient {

    public static void main(String[] args) {
        String host = args.length > 0 ? args[0] : "localhost";
        int port = args.length > 1 ? Integer.parseInt(args[1]) : 5006;

        try {
            Socket socket = new Socket(host, port);
            System.out.println("Đã kết nối tới Chat Server (" + host + ":" + port + ").");

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
            PrintWriter out = new PrintWriter(
                    new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true);
            BufferedReader console = new BufferedReader(
                    new InputStreamReader(System.in, StandardCharsets.UTF_8));

            // Luồng phụ: liên tục nhận tin nhắn từ Server và in ra màn hình
            Thread receiveThread = new Thread(() -> {
                try {
                    String serverMsg;
                    while ((serverMsg = in.readLine()) != null) {
                        System.out.println(serverMsg);
                    }
                } catch (IOException e) {
                    System.out.println("Kết nối tới server đã kết thúc.");
                }
            });
            receiveThread.setDaemon(true); // Tự tắt khi chương trình chính kết thúc
            receiveThread.start();

            // Luồng chính: đọc phím người dùng gõ và gửi
            String userInput;
            while ((userInput = console.readLine()) != null) {
                out.println(userInput);
                if (userInput.trim().equalsIgnoreCase("QUIT")) {
                    break;
                }
            }

            socket.close();
            System.out.println("Đã thoát ứng dụng chat.");

        } catch (IOException e) {
            System.err.println("Không thể kết nối tới Chat Server: " + e.getMessage());
        }
    }
}
