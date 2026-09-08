import static java.lang.Thread.sleep;

import java.io.IOException;
import java.io.InputStream;

/**
 * Ví dụ 2 — Dùng available() để kiểm tra dữ liệu sẵn có
 * Có data thì đọc vào buffer; không có thì in "." và sleep 100ms.
 */
public class ViDu2_Available {
    public static void main(String[] args) throws InterruptedException {
        InputStream is = System.in;
        while (true) {
            try {
                // Kiểm tra số byte sẵn có mà không bị nghẽn
                if (is.available() > 0) {
                    byte[] buffer = new byte[is.available()];
                    int bytesRead = is.read(buffer); // đọc vào mảng byte
                    if (bytesRead == -1) {
                        break;
                    }
                    String str = new String(buffer, 0, bytesRead);
                    System.out.println(str);
                } else {
                    // Không có dữ liệu: in "." và nghỉ 100ms để giảm tải CPU
                    System.out.print(".");
                    sleep(100);
                }
            } catch (IOException e) {
                System.out.println(e);
            }
        }
    }
}
