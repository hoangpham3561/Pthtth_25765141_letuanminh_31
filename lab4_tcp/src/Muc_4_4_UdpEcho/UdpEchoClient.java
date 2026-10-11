package Muc_4_4_UdpEcho;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;

/**
 * Mục thực hành 4.4: UDP Echo Client.
 * Gửi một thông điệp datagram tới server và đợi phản hồi.
 * Đặc biệt chú ý: setSoTimeout(3000) để tránh client bị treo vô hạn nếu server không phản hồi.
 */
public class UdpEchoClient {

    public static void main(String[] args) {
        String host = args.length > 0 ? args[0] : "localhost";
        int port = 5001;
        if (args.length > 1) {
            try {
                port = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                System.err.println("Lỗi: Port phải là số nguyên.");
                return;
            }
        }

        String message = args.length > 2 ? args[2] : "xin chào UDP";

        try {
            byte[] data = message.getBytes(StandardCharsets.UTF_8);
            InetAddress serverAddress = InetAddress.getByName(host);

            // Mở một DatagramSocket ngẫu nhiên trên máy client
            try (DatagramSocket socket = new DatagramSocket()) {
                // Thiết lập thời gian chờ tối đa là 3000 ms (3 giây)
                socket.setSoTimeout(3000);

                // Tạo và gửi gói tin datagram sang server
                DatagramPacket request = new DatagramPacket(data, data.length, serverAddress, port);
                socket.send(request);
                System.out.println("Đã gửi tới " + host + ":" + port + " -> " + message);

                // Chuẩn bị vùng đệm để nhận phản hồi từ server
                byte[] buffer = new byte[4096];
                DatagramPacket response = new DatagramPacket(buffer, buffer.length);

                try {
                    // Đợi nhận phản hồi
                    socket.receive(response);
                    String responseText = new String(
                            response.getData(),
                            response.getOffset(),
                            response.getLength(),
                            StandardCharsets.UTF_8
                    );
                    System.out.println("Server phản hồi: " + responseText);

                } catch (SocketTimeoutException e) {
                    // Xảy ra khi hết 3 giây mà server chưa gửi lại dữ liệu
                    System.err.println("Hết 3 giây nhưng chưa nhận được phản hồi từ server!");
                }
            }

        } catch (IOException e) {
            System.err.println("Lỗi I/O: " + e.getMessage());
        }
    }
}
