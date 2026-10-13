import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.GridLayout;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

/**
 * ============================================================================
 * TRUNG TÂM GIÁM SÁT HỆ THỐNG CHAT TCP (DASHBOARD)
 * ============================================================================
 * Đặc điểm kỹ thuật (Dành cho Newbie):
 * 1. Sử dụng Java Swing (GUI) trực quan, có sẵn trong JDK, không cần thư viện ngoài.
 * 2. Kết nối tới ChatServer qua TCP Socket với tư cách là Dashboard Admin.
 * 3. Hiển thị thông tin giám sát thời gian thực (Realtime):
 *    - Danh sách phòng chat và số lượng thành viên trong từng phòng.
 *    - Nhật ký sự kiện toàn hệ thống (ai vào phòng, ai chat, ai rời phòng).
 * 4. Cho phép Admin phát thông báo broadcast tới TẤT CẢ các phòng chat.
 * 5. Hỗ trợ tự động fallback sang chế độ Console nếu môi trường không có giao diện.
 * ============================================================================
 */
public class ChatDashboard {

    private static final String DEFAULT_HOST = "localhost";
    private static final int DEFAULT_PORT = 5006;

    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;

    // Các thành phần giao diện Swing
    private JFrame frame;
    private JLabel statusLabel;
    private JTextArea statsArea;   // Hiển thị thống kê các phòng & user online
    private JTextArea logArea;     // Hiển thị nhật ký tin nhắn & sự kiện realtime
    private JTextField broadcastField;

    public static void main(String[] args) {
        String host = DEFAULT_HOST;
        int port = DEFAULT_PORT;

        if (args.length >= 1) host = args[0];
        if (args.length >= 2) {
            try {
                port = Integer.parseInt(args[1]);
            } catch (NumberFormatException ignored) {}
        }

        ChatDashboard dashboard = new ChatDashboard();

        final String serverHost = host;
        final int serverPort = port;

        // Kiểm tra xem máy có hỗ trợ giao diện đồ họa GUI hay không
        if (GraphicsEnvironment.isHeadless()) {
            System.out.println(">> Môi trường không hỗ trợ GUI. Đang chạy Dashboard ở chế độ Console...");
            dashboard.runConsoleDashboard(serverHost, serverPort);
        } else {
            SwingUtilities.invokeLater(() -> dashboard.createAndShowGUI(serverHost, serverPort));
        }
    }

    /**
     * Khởi tạo và hiển thị cửa sổ giao diện đồ họa Swing
     */
    private void createAndShowGUI(String host, int port) {
        frame = new JFrame("HỆ THỐNG CHAT TCP - TRUNG TÂM GIÁM SÁT (DASHBOARD)");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(850, 600);
        frame.setLocationRelativeTo(null); // Hiển thị giữa màn hình
        frame.setLayout(new BorderLayout(10, 10));

        // 1. THANH TIÊU ĐỀ & TRẠNG THÁI (TRÊN CÙNG)
        JPanel topPanel = new JPanel(new BorderLayout(10, 10));
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 5, 15));
        topPanel.setBackground(new Color(240, 244, 248));

        JLabel titleLabel = new JLabel("BẢNG ĐIỀU KHIỂN & GIÁM SÁT HỆ THỐNG CHAT (TCP DASHBOARD)");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setForeground(new Color(24, 43, 73));

        statusLabel = new JLabel("Trạng thái: Đang kết nối tới Server (" + host + ":" + port + ")...");
        statusLabel.setFont(new Font("Arial", Font.PLAIN, 13));
        statusLabel.setForeground(Color.GRAY);

        JButton refreshBtn = new JButton("Làm mới thống kê");
        refreshBtn.setFont(new Font("Arial", Font.BOLD, 12));
        refreshBtn.addActionListener(e -> requestStats());

        topPanel.add(titleLabel, BorderLayout.NORTH);
        topPanel.add(statusLabel, BorderLayout.CENTER);
        topPanel.add(refreshBtn, BorderLayout.EAST);

        // 2. KHU VỰC NỘI DUNG CHÍNH (CHIA ĐÔI: THỐNG KÊ & NHẬT KÝ REALTIME)
        // Ô bên trái: Thống kê các phòng
        statsArea = new JTextArea();
        statsArea.setEditable(false);
        statsArea.setFont(new Font("Monospaced", Font.PLAIN, 13));
        statsArea.setText("Đang tải danh sách phòng và số liệu...\n");
        statsArea.setBackground(new Color(250, 250, 250));
        JScrollPane statsScroll = new JScrollPane(statsArea);
        statsScroll.setBorder(BorderFactory.createTitledBorder("Thống kê phòng & Người dùng trực tuyến"));

        // Ô bên phải: Nhật ký sự kiện toàn hệ thống
        logArea = new JTextArea();
        logArea.setEditable(false);
        logArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        logArea.setBackground(new Color(255, 255, 255));
        JScrollPane logScroll = new JScrollPane(logArea);
        logScroll.setBorder(BorderFactory.createTitledBorder("Nhật ký hoạt động thời gian thực (Realtime Logs)"));

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, statsScroll, logScroll);
        splitPane.setDividerLocation(360);
        splitPane.setResizeWeight(0.4);

        // 3. KHU VỰC PHÁT THÔNG BÁO ADMIN (DƯỚI CÙNG)
        JPanel bottomPanel = new JPanel(new BorderLayout(8, 8));
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(5, 15, 10, 15));

        JLabel notifyLabel = new JLabel("Gửi thông báo Admin tới mọi phòng:");
        notifyLabel.setFont(new Font("Arial", Font.BOLD, 12));

        broadcastField = new JTextField();
        broadcastField.setFont(new Font("Arial", Font.PLAIN, 13));
        broadcastField.addActionListener(e -> sendAdminBroadcast());

        JButton sendBroadcastBtn = new JButton("Gửi toàn hệ thống");
        sendBroadcastBtn.setFont(new Font("Arial", Font.BOLD, 12));
        sendBroadcastBtn.addActionListener(e -> sendAdminBroadcast());

        JPanel inputPanel = new JPanel(new BorderLayout(6, 6));
        inputPanel.add(broadcastField, BorderLayout.CENTER);
        inputPanel.add(sendBroadcastBtn, BorderLayout.EAST);

        bottomPanel.add(notifyLabel, BorderLayout.NORTH);
        bottomPanel.add(inputPanel, BorderLayout.CENTER);

        // Ghép các thành phần vào Frame
        frame.add(topPanel, BorderLayout.NORTH);
        frame.add(splitPane, BorderLayout.CENTER);
        frame.add(bottomPanel, BorderLayout.SOUTH);

        frame.setVisible(true);

        // Bắt đầu kết nối mạng tới Server
        new Thread(() -> connectToServer(host, port)).start();
    }

    /**
     * Kết nối TCP tới Server và lắng nghe dữ liệu giám sát
     */
    private void connectToServer(String host, int port) {
        try {
            socket = new Socket(host, port);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
            out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true);

            // Gửi định danh "DASHBOARD" để Server nhận biết
            out.println("DASHBOARD");

            SwingUtilities.invokeLater(() -> {
                statusLabel.setText("Trạng thái: Đã kết nối tới Server (" + host + ":" + port + ") [Online]");
                statusLabel.setForeground(new Color(34, 139, 34)); // Màu xanh lá cây
            });

            // Vòng lặp nhận dữ liệu cập nhật từ Server
            String line;
            StringBuilder statsBuffer = null;

            while ((line = in.readLine()) != null) {
                if (line.equals("STATS_BEGIN")) {
                    statsBuffer = new StringBuilder();
                } else if (line.equals("STATS_END")) {
                    if (statsBuffer != null) {
                        final String finalStats = statsBuffer.toString();
                        SwingUtilities.invokeLater(() -> statsArea.setText(finalStats));
                        statsBuffer = null;
                    }
                } else if (statsBuffer != null) {
                    statsBuffer.append(line).append("\n");
                } else if (line.startsWith("LOG: ")) {
                    final String logContent = line.substring(5);
                    SwingUtilities.invokeLater(() -> {
                        logArea.append(logContent + "\n");
                        logArea.setCaretPosition(logArea.getDocument().getLength()); // Tự cuộn xuống dòng mới
                    });
                    // Tự động làm mới thống kê khi có người vào/ra phòng
                    if (logContent.contains("[VÀO PHÒNG]") || logContent.contains("[RỜI PHÒNG]")
                            || logContent.contains("[NGẮT KẾT NỐI]") || logContent.contains("[KẾT NỐI]")) {
                        requestStats();
                    }
                }
            }

        } catch (IOException e) {
            SwingUtilities.invokeLater(() -> {
                if (statusLabel != null) {
                    statusLabel.setText("Lỗi: Mất kết nối tới Server (" + e.getMessage() + ")");
                    statusLabel.setForeground(Color.RED);
                }
            });
        }
    }

    /**
     * Gửi yêu cầu cập nhật lại thống kê số liệu
     */
    private void requestStats() {
        if (out != null) {
            out.println("STATS");
        }
    }

    /**
     * Admin phát thông báo broadcast tới tất cả các client
     */
    private void sendAdminBroadcast() {
        if (broadcastField == null) return;
        String message = broadcastField.getText().trim();
        if (message.isEmpty()) {
            return;
        }

        if (out != null) {
            out.println("BROADCAST " + message);
            broadcastField.setText("");
        } else {
            JOptionPane.showMessageDialog(frame, "Chưa kết nối tới Server!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Chế độ Console dành cho môi trường không có màn hình GUI (Terminal / Server)
     */
    private void runConsoleDashboard(String host, int port) {
        try (Socket s = new Socket(host, port);
             BufferedReader consoleIn = new BufferedReader(new InputStreamReader(s.getInputStream(), StandardCharsets.UTF_8));
             PrintWriter consoleOut = new PrintWriter(new OutputStreamWriter(s.getOutputStream(), StandardCharsets.UTF_8), true)) {

            consoleOut.println("DASHBOARD");
            System.out.println("==================================================");
            System.out.println("   DASHBOARD GIÁM SÁT HỆ THỐNG CHAT (CONSOLE)");
            System.out.println("   Đã kết nối tới Server: " + host + ":" + port);
            System.out.println("==================================================");

            String line;
            while ((line = consoleIn.readLine()) != null) {
                System.out.println(line);
            }

        } catch (IOException e) {
            System.err.println("Lỗi kết nối Dashboard: " + e.getMessage());
        }
    }
}
