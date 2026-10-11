package Bai_Tap_3_DichVuNgayGio;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Bài 3: Dịch vụ ngày giờ UDP.
 * Cổng: 5004
 * Hỗ trợ các lệnh: DATE, TIME, DATETIME.
 * Chú ý: UDP không có khái niệm phiên kết nối (connectionless), nên không cần lệnh QUIT.
 */
public class DateTimeUdpServer {

    private static final int PORT = 5004;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    public static void main(String[] args) {
        byte[] buffer = new byte[2048];

        try (DatagramSocket socket = new DatagramSocket(PORT)) {
            System.out.println("DateTimeUdpServer đang chạy trên cổng " + PORT + "...");

            while (true) {
                DatagramPacket requestPacket = new DatagramPacket(buffer, buffer.length);
                socket.receive(requestPacket);

                String command = new String(
                        requestPacket.getData(),
                        requestPacket.getOffset(),
                        requestPacket.getLength(),
                        StandardCharsets.UTF_8
                ).trim().toUpperCase();

                System.out.println("Nhận lệnh từ " + requestPacket.getAddress() + ":" + requestPacket.getPort() + " -> " + command);

                String responseText = executeCommand(command);
                byte[] responseBytes = responseText.getBytes(StandardCharsets.UTF_8);

                DatagramPacket responsePacket = new DatagramPacket(
                        responseBytes,
                        responseBytes.length,
                        requestPacket.getAddress(),
                        requestPacket.getPort()
                );

                socket.send(responsePacket);
            }

        } catch (IOException e) {
            System.err.println("Lỗi UDP Server: " + e.getMessage());
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
