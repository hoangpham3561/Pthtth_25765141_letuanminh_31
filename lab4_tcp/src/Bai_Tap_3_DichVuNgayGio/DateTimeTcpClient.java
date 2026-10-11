package Bai_Tap_3_DichVuNgayGio;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

/**
 * Bài 3: Dịch vụ ngày giờ TCP Client.
 * Kết nối tới DateTimeTcpServer (mặc định localhost:5003).
 * Nhập các lệnh: DATE, TIME, DATETIME, QUIT.
 */
public class DateTimeTcpClient {

    public static void main(String[] args) {
        String host = args.length > 0 ? args[0] : "localhost";
        int port = args.length > 1 ? Integer.parseInt(args[1]) : 5003;

        try (Socket socket = new Socket(host, port);
             BufferedReader console = new BufferedReader(
                     new InputStreamReader(System.in, StandardCharsets.UTF_8));
             BufferedReader in = new BufferedReader(
                     new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
             PrintWriter out = new PrintWriter(
                     new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true)) {

            System.out.println("Đã kết nối tới DateTimeTcpServer (" + host + ":" + port + ").");
            System.out.println("Nhập các lệnh: DATE, TIME, DATETIME (hoặc QUIT để thoát):");

            String line;
            while ((line = console.readLine()) != null) {
                out.println(line);

                String response = in.readLine();
                if (response == null) {
                    System.out.println("Server đã đóng kết nối.");
                    break;
                }

                System.out.println("Server trả về: " + response);

                if (line.trim().equalsIgnoreCase("QUIT")) {
                    break;
                }
            }

        } catch (IOException e) {
            System.err.println("Lỗi kết nối tới DateTimeTcpServer: " + e.getMessage());
        }
    }
}
