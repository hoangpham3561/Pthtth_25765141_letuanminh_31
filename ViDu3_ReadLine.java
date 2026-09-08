import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Ví dụ 3 — Nhập chuỗi (dòng) từ InputStream
 * InputStream (byte) → InputStreamReader (ký tự) → BufferedReader → readLine()
 */
public class ViDu3_ReadLine {
    public static void main(String[] args) {
        InputStream is = System.in;
        InputStreamReader isr = new InputStreamReader(is);
        BufferedReader br = new BufferedReader(isr);

        try {
            while (true) {
                String line = br.readLine(); // đọc 1 dòng văn bản
                if (line == null) {
                    break;
                }
                System.out.println(line);
            }
        } catch (IOException e) {
            System.out.println(e);
        }
    }
}
