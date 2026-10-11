package Bai_Tap_3_DichVuNgayGio;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;

/**
 * Bài 3: Dịch vụ ngày giờ UDP Client.
 * Cho phép gửi DATE, TIME, DATETIME hoặc EXIT để dừng client.
 * Tích hợp timeout 3s để minh họa hành vi khi Server dừng giữa chừng.
 */
public class DateTimeUdpClient {

    public static void main(String[] args) {
        String host = args.length > 0 ? args[0] : "localhost";
        int port = args.length > 1 ? Integer.parseInt(args[1]) : 5004;

        try (DatagramSocket socket = new DatagramSocket();
             BufferedReader console = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8))) {

            socket.setSoTimeout(3000); // 3 giây timeout
            InetAddress serverAddress = InetAddress.getByName(host);

            System.out.println("Kết nối tới DateTimeUdpServer (" + host + ":" + port + ").");
            System.out.println("Nhập các lệnh: DATE, TIME, DATETIME (hoặc EXIT để dừng chương trình):");

            String line;
            while ((line = console.readLine()) != null) {
                String command = line.trim();
                if (command.equalsIgnoreCase("EXIT")) {
                    System.out.println("Tạm biệt!");
                    break;
                }

                byte[] sendData = command.getBytes(StandardCharsets.UTF_8);
                DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, serverAddress, port);
                socket.send(sendPacket);

                byte[] receiveBuffer = new byte[2048];
                DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);

                try {
                    socket.receive(receivePacket);
                    String reply = new String(
                            receivePacket.getData(),
                            receivePacket.getOffset(),
                            receivePacket.getLength(),
                            StandardCharsets.UTF_8
                    );
                    System.out.println("Server trả về: " + reply);
                } catch (SocketTimeoutException e) {
                    System.err.println("Timeout: Không nhận được phản hồi sau 3 giây (có thể Server đã dừng hoặc mạng bị ngắt)!");
                }
            }

        } catch (IOException e) {
            System.err.println("Lỗi Client: " + e.getMessage());
        }
    }
}
