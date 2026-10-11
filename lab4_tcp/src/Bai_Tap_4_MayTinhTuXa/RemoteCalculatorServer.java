package Bai_Tap_4_MayTinhTuXa;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

/**
 * Bài 4: Máy tính từ xa (TCP Remote Calculator Server).
 * Cổng: 5005
 * Cú pháp lệnh: CALC toán_tử toán_hạng_1 toán_hạng_2
 * Ví dụ: CALC + 100 200 -> OK 300
 * Các trường hợp lỗi:
 * - CALC / 10 0     -> ERR DIVIDE_BY_ZERO
 * - CALC % 10 3     -> ERR UNSUPPORTED_OPERATOR
 * - CALC + a 2      -> ERR INVALID_NUMBER
 * - Thiếu thành phần -> ERR INVALID_FORMAT
 */
public class RemoteCalculatorServer {

    private static final int PORT = 5005;

    public static void main(String[] args) {
        try (ServerSocket server = new ServerSocket(PORT)) {
            System.out.println("RemoteCalculatorServer đang chạy trên port " + PORT + "...");

            while (true) {
                Socket socket = server.accept();
                new Thread(() -> handleClient(socket)).start();
            }

        } catch (IOException e) {
            System.err.println("Lỗi Server: " + e.getMessage());
        }
    }

    private static void handleClient(Socket socket) {
        try (socket;
             BufferedReader in = new BufferedReader(new InputStreamReader(
                     socket.getInputStream(), StandardCharsets.UTF_8));
             PrintWriter out = new PrintWriter(new OutputStreamWriter(
                     socket.getOutputStream(), StandardCharsets.UTF_8), true)) {

            String request;
            while ((request = in.readLine()) != null) {
                if (request.trim().equalsIgnoreCase("QUIT")) {
                    out.println("OK BYE");
                    break;
                }

                String response = calculate(request);
                out.println(response);
            }

        } catch (IOException e) {
            System.err.println("Lỗi xử lý client: " + e.getMessage());
        }
    }

    /**
     * Hàm phân tích cú pháp và tính toán:
     */
    public static String calculate(String rawInput) {
        if (rawInput == null) {
            return "ERR INVALID_FORMAT";
        }

        // Tách chuỗi theo dấu cách (loại bỏ nhiều dấu cách liên tiếp)
        String[] parts = rawInput.trim().split("\\s+");

        // Yêu cầu phải có đúng 4 phần: "CALC", toán tử, số 1, số 2
        if (parts.length != 4 || !parts[0].equalsIgnoreCase("CALC")) {
            return "ERR INVALID_FORMAT";
        }

        String operator = parts[1];
        String strNum1 = parts[2];
        String strNum2 = parts[3];

        // 1. Kiểm tra toán tử có được hỗ trợ (+, -, *, /) không
        if (!operator.equals("+") && !operator.equals("-") && !operator.equals("*") && !operator.equals("/")) {
            return "ERR UNSUPPORTED_OPERATOR";
        }

        // 2. Kiểm tra xem 2 toán hạng có phải là số hợp lệ không
        double num1, num2;
        try {
            num1 = Double.parseDouble(strNum1);
            num2 = Double.parseDouble(strNum2);
        } catch (NumberFormatException e) {
            return "ERR INVALID_NUMBER";
        }

        // 3. Thực hiện phép tính
        double result;
        switch (operator) {
            case "+":
                result = num1 + num2;
                break;
            case "-":
                result = num1 - num2;
                break;
            case "*":
                result = num1 * num2;
                break;
            case "/":
                // Kiểm tra chia cho 0
                if (num2 == 0) {
                    return "ERR DIVIDE_BY_ZERO";
                }
                result = num1 / num2;
                break;
            default:
                return "ERR UNSUPPORTED_OPERATOR";
        }

        // Nếu kết quả là số nguyên chẵn thì in ra số nguyên, ngược lại in số thực
        if (result == (long) result) {
            return "OK " + (long) result;
        } else {
            return "OK " + result;
        }
    }
}
