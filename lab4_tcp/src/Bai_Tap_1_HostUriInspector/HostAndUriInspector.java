package Bai_Tap_1_HostUriInspector;

import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;

/**
 * Bài 1: Host và URI Inspector
 * Yêu cầu:
 * 1. Nhận hostname và URI qua args (2 tham số riêng biệt).
 * 2. In toàn bộ IP, loại IPv4 hoặc IPv6, loopback và site local.
 * 3. Dùng java.net.URI để in scheme, host, port, path, query, fragment.
 * 4. Xử lý thiếu tham số, URI sai cú pháp, hostname không phân giải được.
 */
public class HostAndUriInspector {

    public static void main(String[] args) {
        String hostName;
        String uriString;

        // Nếu người dùng truyền đủ 2 tham số qua dòng lệnh (theo đề bài)
        if (args.length >= 2) {
            hostName = args[0];
            uriString = args[1];
        } else {
            // Khi bấm nút 'Run Main' trên IDE (args rỗng), tự động dùng dữ liệu mẫu để chạy ngay không bị treo
            System.out.println("[GỢI Ý] Bạn đang bấm 'Run Main' trực tiếp không kèm tham số args.");
            System.out.println("  -> Chương trình sẽ tự động chạy với dữ liệu mẫu:");
            System.out.println("     Hostname: localhost");
            System.out.println("     URI:      https://iuh.edu.vn:8080/tuyen-sinh?khoa=cntt#muc1");
            System.out.println("  (Để truyền hostname và URI tùy ý qua terminal, gõ:)");
            System.out.println("  java -cp out Bai_Tap_1_HostUriInspector.HostAndUriInspector <hostname> <uri>\n");

            hostName = "localhost";
            uriString = "https://iuh.edu.vn:8080/tuyen-sinh?khoa=cntt#muc1";
        }

        System.out.println("==========================================");
        System.out.println("PHẦN 1: KHẢO SÁT HOSTNAME (" + hostName + ")");
        System.out.println("==========================================");

        try {
            // Lấy tất cả địa chỉ IP gắn với hostname
            InetAddress[] addresses = InetAddress.getAllByName(hostName);
            for (int i = 0; i < addresses.length; i++) {
                InetAddress addr = addresses[i];
                System.out.println("Địa chỉ " + (i + 1) + ":");
                System.out.println("  - IP: " + addr.getHostAddress());

                // Nhận diện kiểu IPv4 hoặc IPv6
                if (addr instanceof Inet4Address) {
                    System.out.println("  - Kiểu: IPv4");
                } else if (addr instanceof Inet6Address) {
                    System.out.println("  - Kiểu: IPv6");
                } else {
                    System.out.println("  - Kiểu: Không rõ");
                }

                System.out.println("  - Canonical Hostname: " + addr.getCanonicalHostName());
                System.out.println("  - Là Loopback? " + addr.isLoopbackAddress());
                System.out.println("  - Là Site Local (Private)? " + addr.isSiteLocalAddress());
            }
        } catch (UnknownHostException e) {
            System.err.println("Lỗi: Không thể phân giải hostname '" + hostName + "' (" + e.getMessage() + ")");
        }

        System.out.println("\n==========================================");
        System.out.println("PHẦN 2: PHÂN TÍCH THÀNH PHẦN URI (" + uriString + ")");
        System.out.println("==========================================");

        try {
            // Sử dụng class java.net.URI để phân tích các thành phần
            URI uri = new URI(uriString);

            System.out.println("  - Scheme (Giao thức): " + (uri.getScheme() != null ? uri.getScheme() : "Không có"));
            System.out.println("  - Host (Máy chủ):     " + (uri.getHost() != null ? uri.getHost() : "Không có"));
            System.out.println("  - Port (Cổng):        " + (uri.getPort() != -1 ? uri.getPort() : "Mặc định (không chỉ định)"));
            System.out.println("  - Path (Đường dẫn):   " + (uri.getPath() != null ? uri.getPath() : "Không có"));
            System.out.println("  - Query (Tham số):    " + (uri.getQuery() != null ? uri.getQuery() : "Không có"));
            System.out.println("  - Fragment (Phần neo):" + (uri.getFragment() != null ? uri.getFragment() : "Không có"));

        } catch (URISyntaxException e) {
            System.err.println("Lỗi cú pháp URI: " + e.getReason() + " tại vị trí: " + e.getIndex());
        }
    }
}
