import java.io.IOException;
import java.io.InputStream;

/**
 * Ví dụ 1 — Đọc từng byte từ InputStream (System.in)
 * Nhập ký tự từ bàn phím, in ra màn hình.
 * Gõ 'q' hoặc Ctrl+D (EOF) để thoát.
 */
public class ViDu1_InputStream {
    public static void main(String[] args) {
        InputStream is = System.in;
        while (true) {
            try {
                int ch = is.read(); // đọc 1 byte
                if (ch == -1 || ch == 'q') { // hết stream hoặc gõ q
                    break;
                }
                System.out.println((char) ch);
            } catch (IOException ioe) {
                System.out.println(ioe);
            }
        }
    }
}
