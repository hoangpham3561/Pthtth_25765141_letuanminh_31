package Bai_Tap_4_MayTinhTuXa;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

/**
 * Bài 4: TCP Remote Calculator Client
 */
public class RemoteCalculatorClient {

    public static void main(String[] args) {
        String host = args.length > 0 ? args[0] : "localhost";
        int port = args.length > 1 ? Integer.parseInt(args[1]) : 5005;

        try (Socket socket = new Socket(host, port);
             BufferedReader console = new BufferedReader(
                     new InputStreamReader(System.in, StandardCharsets.UTF_8));
             BufferedReader in = new BufferedReader(
                     new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
             PrintWriter out = new PrintWriter(
                     new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true)) {

            System.out.println("Đã kết nối tới RemoteCalculatorServer (" + host + ":" + port + ").");
            System.out.println("Cú pháp: CALC <toán tử> <số 1> <số 2>");
            System.out.println("Ví dụ: CALC + 100 200, CALC / 10 2, hoặc gõ QUIT để thoát.");

            String line;
            while ((line = console.readLine()) != null) {
                out.println(line);

                String response = in.readLine();
                if (response == null) {
                    System.out.println("Server đã đóng kết nối.");
                    break;
                }

                System.out.println("Server phản hồi: " + response);

                if (line.trim().equalsIgnoreCase("QUIT")) {
                    break;
                }
            }

        } catch (IOException e) {
            System.err.println("Lỗi kết nối client: " + e.getMessage());
        }
    }
}
