package Muc_4_1_KhaoSatDiaChiMang;

import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Mục thực hành 4.1: Khảo sát địa chỉ mạng bằng InetAddress.
 * Giúp người mới học (newbie) hiểu cách phân giải tên miền (DNS) thành địa chỉ IP,
 * kiểm tra xem đó là IPv4 hay IPv6, và xem các đặc tính của IP (loopback, site local).
 */
public class HostInspector {

    public static void main(String[] args) {
        String hostName;
        if (args.length == 1) {
            hostName = args[0];
        } else {
            System.out.println("[GỢI Ý] Bấm 'Run Main' trực tiếp -> Tự động khảo sát 'localhost'.");
            System.out.println("  (Để khảo sát tên miền khác qua terminal: java -cp out Muc_4_1_KhaoSatDiaChiMang.HostInspector <hostname>)\n");
            hostName = "localhost";
        }

        try {
            // Bước 2: Dùng InetAddress.getAllByName để lấy danh sách tất cả các địa chỉ IP của hostName
            InetAddress[] addresses = InetAddress.getAllByName(hostName);
            System.out.println("Host: " + hostName);

            // Bước 3: Duyệt qua từng địa chỉ IP tìm được và in thông tin chi tiết
            for (InetAddress address : addresses) {
                System.out.println("- IP: " + address.getHostAddress());

                // Yêu cầu bổ sung của đề bài: Phân biệt IPv4 hay IPv6
                if (address instanceof Inet4Address) {
                    System.out.println("  Kiểu IP: IPv4");
                } else if (address instanceof Inet6Address) {
                    System.out.println("  Kiểu IP: IPv6");
                } else {
                    System.out.println("  Kiểu IP: Không xác định");
                }

                // Tên đầy đủ được phân giải ngược (canonical hostname)
                System.out.println("  Canonical: " + address.getCanonicalHostName());

                // Kiểm tra xem có phải địa chỉ loopback (chạy trên cùng máy, ví dụ 127.0.0.1 hay ::1) không
                System.out.println("  Loopback: " + address.isLoopbackAddress());

                // Kiểm tra xem có phải địa chỉ mạng nội bộ (private LAN, ví dụ 192.168.x.x, 10.x.x.x) không
                System.out.println("  Site local: " + address.isSiteLocalAddress());
            }

        } catch (UnknownHostException e) {
            // Trường hợp không tìm thấy host (ví dụ gõ sai tên miền hoặc không có kết nối mạng)
            System.err.println("Không phân giải được host: " + hostName);
        }
    }
}
