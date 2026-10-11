package Muc_4_4_UdpEcho;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

/**
 * Mục thực hành 4.4: UDP Echo Server.
 * Sử dụng DatagramSocket lắng nghe tại cổng 5001.
 * Điểm lưu ý quan trọng đối với newbie:
 * - UDP là giao thức không hướng kết nối (connectionless), không cần ServerSocket hay accept().
 * - Dữ liệu truyền đi dưới dạng các gói tin độc lập (DatagramPacket).
 * - Cần đọc đúng khoảng byte thực sự nhận được thông qua offset và length của packet.
 */
public class UdpEchoServer {

    private static final int DEFAULT_PORT = 5001;
    private static final int BUFFER_SIZE = 4096;

    public static void main(String[] args) {
        int port = args.length > 0 ? Integer.parseInt(args[0]) : DEFAULT_PORT;
        // Vùng đệm chứa dữ liệu nhận được
        byte[] buffer = new byte[BUFFER_SIZE];

        try (DatagramSocket socket = new DatagramSocket(port)) {
            System.out.println("UDP server đang lắng nghe trên cổng " + port + "...");

            while (true) {
                // Tạo gói tin DatagramPacket để hứng dữ liệu từ bất kỳ client nào gửi đến
                DatagramPacket request = new DatagramPacket(buffer, buffer.length);
                socket.receive(request); // Lệnh này sẽ chờ cho đến khi có gói tin đến

                // Chuyển mảng byte nhận được thành chuỗi UTF-8 dựa trên offset và length thực tế
                String message = new String(
                        request.getData(),
                        request.getOffset(),
                        request.getLength(),
                        StandardCharsets.UTF_8
                );

                System.out.println("Nhận từ [" + request.getAddress() + ":" + request.getPort() + "]: " + message);

                // Chuẩn bị nội dung phản hồi: thêm tiền tố "ACK " và in hoa nội dung
                String text = "ACK " + message.toUpperCase(Locale.ROOT);
                byte[] responseData = text.getBytes(StandardCharsets.UTF_8);

                // Tạo gói tin phản hồi gửi ngược lại địa chỉ IP và Port của người gửi
                DatagramPacket response = new DatagramPacket(
                        responseData,
                        responseData.length,
                        request.getAddress(),
                        request.getPort()
                );

                socket.send(response);
            }

        } catch (IOException e) {
            System.err.println("Lỗi UDP Server: " + e.getMessage());
        }
    }
}
