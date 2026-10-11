package Bai_Tap_3_DichVuNgayGio;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Bài 3: Dịch vụ ngày giờ TCP.
 * Hỗ trợ các lệnh:
 * - DATE     -> trả về ngày theo định dạng dd/MM/yyyy
 * - TIME     -> trả về giờ theo định dạng HH:mm:ss
 * - DATETIME -> trả về ngày giờ theo định dạng dd/MM/yyyy HH:mm:ss
 * - QUIT     -> đóng kết nối với thông báo OK BYE
 */
public class DateTimeTcpServer {

    private static final int PORT = 5003;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    public static void main(String[] args) {
        try (ServerSocket server = new ServerSocket(PORT)) {
            System.out.println("DateTimeTcpServer đang chạy trên cổng " + PORT + "...");

            while (true) {
                Socket socket = server.accept();
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

            String command;
            while ((command = in.readLine()) != null) {
                String trimmed = command.trim().toUpperCase();

                if (trimmed.equals("QUIT")) {
                    out.println("OK BYE");
                    break;
                }

                String response = executeCommand(trimmed);
                out.println(response);
            }

        } catch (IOException e) {
            System.err.println("Lỗi giao tiếp TCP: " + e.getMessage());
        }
    }

    public static String executeCommand(String command) {
        LocalDateTime now = LocalDateTime.now();
        switch (command) {
            case "DATE":
                return "OK " + now.format(DATE_FORMATTER);
            case "TIME":
                return "OK " + now.format(TIME_FORMATTER);
            case "DATETIME":
                return "OK " + now.format(DATETIME_FORMATTER);
            default:
                return "ERR UNKNOWN_COMMAND";
        }
    }
}
