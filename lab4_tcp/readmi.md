# BÁO CÁO THỰC HÀNH LAB 04: LẬP TRÌNH SOCKET TCP VÀ UDP TRONG JAVA

> **Môn học**: Phát triển Hệ thống Tích hợp & Phân tán (PTHTTH) - FIT.IUH  
> **Sinh viên**: Lê Tuấn Minh  
> **Mã sinh viên**: 25765141 - Lớp: 31  
> **Tài liệu nguồn**: `Lab_04_Java_Socket_TCP_UDP (v2026).pdf`  
> **Môi trường thực thi**: Java SE 25 LTS (tương thích JDK 17+), macOS Terminal (Zsh)

---

## MỤC LỤC
1. [Giới thiệu & Mục tiêu bài thực hành](#1-giới-thiệu--mục-tiêu-bài-thực-hành)
2. [Tóm tắt yêu cầu đề bài & Cải tiến kỹ thuật](#2-tóm-tắt-yêu-cầu-đề-bài--cải-tiến-kỹ-thuật)
3. [Cấu trúc thư mục dự án](#3-cấu-trúc-thư-mục-dự-án)
4. [Phân tích & Hướng dẫn chi tiết từng bài (Dành cho Newbie)](#4-phân-tích--hướng-dẫn-chi-tiết-từng-bài-dành-cho-newbie)
   - [4.1. Khảo sát địa chỉ mạng (HostInspector)](#41-khảo-sát-địa-chỉ-mạng-hostinspector)
   - [4.2. Giao thức dòng TCP (TcpCommandServer & Client)](#42-giao-thức-dòng-tcp-tcpcommandserver--client)
   - [4.3. Phục vụ đồng thời nhiều Client với Thread Pool](#43-phục-vụ-đồng-thời-nhiều-client-với-thread-pool)
   - [4.4. UDP Request - Response với Timeout](#44-udp-request---response-với-timeout)
   - [4.5. Bài tập 1: Host và URI Inspector](#45-bài-tập-1-host-và-uri-inspector)
   - [4.6. Bài tập 2: TCP đổi chữ số thành chữ (Xử lý ca biên)](#46-bài-tập-2-tcp-đổi-chữ-số-thành-chữ-xử-lý-ca-biên)
   - [4.7. Bài tập 3: Dịch vụ Ngày Giờ TCP & UDP](#47-bài-tập-3-dịch-vụ-ngày-giờ-tcp--udp)
   - [4.8. Bài tập 4: Máy tính từ xa (Remote Calculator)](#48-bài-tập-4-máy-tính-từ-xa-remote-calculator)
   - [4.9. Bài tập 5: Phòng chat TCP đa Client (Broadcast & Thread-safe)](#49-bài-tập-5-phòng-chat-tcp-đa-client-broadcast--thread-safe)
5. [Hướng dẫn biên dịch & Lệnh chạy thực nghiệm](#5-hướng-dẫn-biên-dịch--lệnh-chạy-thực-nghiệm)
6. [Bằng chứng kiểm thử (Evidence / Test Results)](#6-bằng-chứng-kiểm-thử-evidence--test-results)
7. [Kinh nghiệm & Đúc kết quan trọng cho Newbie](#7-kinh-nghiệm--đúc-kết-quan-trọng-cho-newbie)

---

## 1. GIỚI THIỆU & MỤC TIÊU BÀI THỰC HÀNH

Bài thực hành này giúp sinh viên xây dựng hoàn chỉnh các ứng dụng mạng theo mô hình **Client - Server**, nắm vững sự khác biệt giữa hai giao thức tầng giao vận cốt lõi: **TCP (Transmission Control Protocol)** và **UDP (User Datagram Protocol)** trong Java.

### Mục tiêu đạt được:
- Sử dụng thành thạo `InetAddress` và `URI` để tra cứu DNS và phân tích cấu trúc địa chỉ URL/URI.
- Hiểu rõ bản chất socket: `ServerSocket`, `Socket` (TCP) so với `DatagramSocket`, `DatagramPacket` (UDP).
- Thiết kế giao thức thông điệp mức ứng dụng (Application Layer Protocol) kết thúc bằng ký tự xuống dòng `\n`.
- Quản lý tài nguyên an toàn bằng `try-with-resources` và kiểm soát đa luồng bằng `ExecutorService` (Fixed Thread Pool).
- Xử lý mã hóa ký tự `StandardCharsets.UTF_8` để bảo toàn tiếng Việt có dấu.
- Xử lý timeout trong UDP bằng `setSoTimeout()`.

---

## 2. TÓM TẮT YÊU CẦU ĐỀ BÀI & CẢI TIẾN KỸ THUẬT

Theo tài liệu mới (v2026), bài thực hành có các điều chỉnh hiện đại hóa so với giáo trình cũ:
1. **Số cổng (Port)**: Dùng cổng từ `5000` trở lên thay vì cổng hệ thống (như port 7). Nhận số cổng qua tham số dòng lệnh `args` để tránh xung đột cổng.
2. **Mã hóa chuỗi**: Luôn dùng tường minh `StandardCharsets.UTF_8` thay vì charset mặc định của hệ điều hành.
3. **Đóng tài nguyên**: 100% sử dụng `try-with-resources` để tránh rò rỉ bộ nhớ/socket descriptor.
4. **Đa luồng (Concurrency)**: Sử dụng Thread Pool có giới hạn (`Executors.newFixedThreadPool(20)`) thay vì tạo `new Thread()` vô hạn gây sập server.
5. **UDP Timeout**: Luôn thiết lập `socket.setSoTimeout(3000)` tránh client treo vô hạn khi mất gói tin.
6. **Địa chỉ mạng**: Phân loại IPv4 và IPv6 bằng `instanceof Inet4Address` / `Inet6Address`, bỏ cách phân lớp A, B, C cũ.

---

## 3. CẤU TRÚC THƯ MỤC DỰ ÁN

```text
lab4_tcp/
├── src/
│   ├── Muc_4_1_KhaoSatDiaChiMang/
│   │   └── HostInspector.java              # Mục 4.1: Khảo sát DNS, IPv4/IPv6, Loopback
│   ├── Muc_4_2_TcpCommand/
│   │   ├── TcpCommandServer.java           # Mục 4.2: TCP Server tuần tự (PING, UPPER, TIME, QUIT)
│   │   └── TcpCommandClient.java           # Mục 4.2: TCP Client nhập lệnh từ console
│   ├── Muc_4_3_MultiClientTcp/
│   │   └── MultiClientTcpServer.java       # Mục 4.3: TCP Server đa luồng với Thread Pool (tái sử dụng serve())
│   ├── Muc_4_4_UdpEcho/
│   │   ├── UdpEchoServer.java              # Mục 4.4: UDP Server nhận gói và phản hồi
│   │   └── UdpEchoClient.java              # Mục 4.4: UDP Client có timeout 3 giây
│   ├── Bai_Tap_1_HostUriInspector/
│   │   └── HostAndUriInspector.java        # Bài 1: Phân giải Host & bóc tách URI
│   ├── Bai_Tap_2_DoiChuSoThanhChu/
│   │   ├── DigitToWordServer.java          # Bài 2: Server đổi chữ số 0-9 sang tiếng Việt
│   │   └── DigitToWordClient.java          # Bài 2: Client gửi chữ số
│   ├── Bai_Tap_3_DichVuNgayGio/
│   │   ├── DateTimeTcpServer.java          # Bài 3: Dịch vụ ngày giờ TCP (DATE, TIME, DATETIME)
│   │   ├── DateTimeTcpClient.java          # Bài 3: Client ngày giờ TCP
│   │   ├── DateTimeUdpServer.java          # Bài 3: Dịch vụ ngày giờ qua UDP
│   │   └── DateTimeUdpClient.java          # Bài 3: Client ngày giờ UDP
│   ├── Bai_Tap_4_MayTinhTuXa/
│   │   ├── RemoteCalculatorServer.java     # Bài 4: Máy tính từ xa TCP (CALC toán_tử a b)
│   │   └── RemoteCalculatorClient.java     # Bài 4: Client máy tính
│   └── Bai_Tap_5_ChatTcpNhieuClient/
│       ├── ChatServer.java                 # Bài 5: Server phòng chat TCP đa client (Broadcast)
│       └── ChatClient.java                 # Bài 5: Client chat 2 luồng độc lập
├── evidence/
│   └── test-results.txt                    # Bằng chứng kết quả chạy test thực tế
└── README.md                               # Tài liệu hướng dẫn chi tiết
```

---

## 4. PHÂN TÍCH & HƯỚNG DẪN CHI TIẾT TỪNG BÀI (DÀNH CHO NEWBIE)

### 4.1. Khảo sát địa chỉ mạng (`HostInspector`)
- **Khái niệm cho Newbie**: Máy tính trên mạng nhận diện nhau bằng IP (như `127.0.0.1` hay `142.250.4.101`). Con người nhớ bằng hostname (như `localhost` hay `google.com`). Hàm `InetAddress.getAllByName(hostname)` đóng vai trò hỏi máy chủ DNS để lấy ra tất cả IP của tên miền đó.
- **Phân loại**:
  - `address instanceof Inet4Address`: Là địa chỉ IPv4 (dài 32-bit, dạng 4 số cách nhau bởi dấu chấm).
  - `address instanceof Inet6Address`: Là địa chỉ IPv6 (dài 128-bit, dạng các nhóm hex cách nhau bởi dấu hai chấm `:`).
  - `isLoopbackAddress()`: Trả về `true` nếu là địa chỉ nội bộ chính máy mình (ví dụ `127.0.0.1` hoặc `::1`).
  - `isSiteLocalAddress()`: Trả về `true` nếu là địa chỉ mạng riêng (mạng LAN như `192.168.x.x`, `10.x.x.x`).

### 4.2. Giao thức dòng TCP (`TcpCommandServer` & `TcpCommandClient`)
- **Khái niệm cho Newbie**: TCP là luồng byte liên tục (stream). Nếu bạn gửi 2 câu liên tiếp, TCP có thể gộp chung lại. Do đó lập trình viên phải tự đặt ra **ranh giới thông điệp** (message delimiter). Trong bài này, quy định mỗi thông điệp kết thúc bằng dấu xuống dòng `\n`.
- **Cơ chế đọc/ghi**:
  - `BufferedReader.readLine()`: Đọc từng dòng cho đến khi gặp `\n`.
  - `PrintWriter.println()` với tham số `autoFlush = true`: Tự động đẩy dữ liệu đi ngay lập tức.
- **Tập lệnh**:
  - `PING` ➜ Trả về `OK PONG`
  - `TIME` ➜ Trả về thời gian hiện tại
  - `UPPER <nội dung>` ➜ Đổi chữ thường sang CHỮ HOA
  - `QUIT` ➜ Trả về `OK BYE` và đóng socket

### 4.3. Phục vụ đồng thời nhiều Client với Thread Pool (`MultiClientTcpServer`)
- **Vấn đề của Server tuần tự**: Nếu Client 1 đang kết nối mà không gửi `QUIT`, Server sẽ bị kẹt mãi ở Client 1, Client 2 kết nối vào phải xếp hàng chờ vô hạn.
- **Giải pháp Newbie**: Sử dụng `ExecutorService pool = Executors.newFixedThreadPool(20)`. Khi có kết nối mới `server.accept()`, ta đưa tác vụ xử lý vào Thread Pool bằng `pool.submit(...)`. Các luồng trong pool sẽ phục vụ song song, không ai phải đợi ai.
- **Tại sao không dùng `new Thread()` vô hạn?** Vì nếu có 10.000 client cùng kết nối, tạo 10.000 luồng sẽ làm cạn kiệt RAM và sập hệ thống (Crash JVM). Fixed Thread Pool giới hạn cố định số luồng giúp hệ thống luôn an toàn.

### 4.4. UDP Request - Response với Timeout (`UdpEchoServer` & `UdpEchoClient`)
- **Khác biệt của UDP**:
  - Không có kết nối trước (không có `ServerSocket`, không có `accept()`).
  - Dữ liệu gửi đi từng gói độc lập (`DatagramPacket`).
- **Điểm Newbie rất dễ sai**:
  - Khi đọc chuỗi từ packet, **bắt buộc** phải dùng:
    ```java
    String text = new String(packet.getData(), packet.getOffset(), packet.getLength(), StandardCharsets.UTF_8);
    ```
    Nếu chỉ dùng `new String(packet.getData())` thì bạn sẽ đọc thừa toàn bộ các byte trống rác còn lại trong mảng đệm 4096 byte!
- **Timeout**:
  - UDP không thông báo nếu bên kia bị chết hoặc mất gói tin. Nếu không đặt timeout, client sẽ đứng đợi mãi mãi tại `socket.receive()`.
  - Thiết lập `socket.setSoTimeout(3000)`: Sau 3 giây không có phản hồi, Java tự ném `SocketTimeoutException` để thông báo lỗi cho người dùng.

---

### 4.5. Bài tập 1: Host và URI Inspector (`HostAndUriInspector`)
- Nhận 2 đối số riêng biệt: `args[0]` là hostname, `args[1]` là URI.
- Sử dụng class `java.net.URI` để phân tích:
  - Scheme, Host, Port (nếu không khai báo thì trả về -1), Path, Query, Fragment.
- Bắt lỗi an toàn khi URI không đúng định dạng (`URISyntaxException`).

### 4.6. Bài tập 2: TCP đổi chữ số thành chữ (`DigitToWordServer`)
- Server lắng nghe cổng 5002.
- **Quy tắc bắt lỗi chặt chẽ (Validation)**:
  - Chỉ chấp nhận chuỗi có đúng 1 ký tự và ký tự đó từ `'0'` đến `'9'`.
  - Mọi trường hợp ca biên như: chuỗi rỗng `""`, số `10`, chữ cái `'a'`, số kèm khoảng trắng `" 5 "` đều bị trả về: `ERR INVALID_DIGIT`.
  - Khi gửi `QUIT` ➜ trả về `OK BYE` và đóng phiên.

### 4.7. Bài tập 3: Dịch vụ Ngày Giờ TCP & UDP (`DateTimeTcpServer`, `DateTimeUdpServer`)
- Định dạng chuẩn bằng `DateTimeFormatter`:
  - `DATE`: `dd/MM/yyyy`
  - `TIME`: `HH:mm:ss`
  - `DATETIME`: `dd/MM/yyyy HH:mm:ss`
- **So sánh TCP và UDP**:
  - Với **TCP**: Là giao thức có trạng thái phiên (Stateful/Connection-oriented). Khi Server tắt giữa chừng, Client nhận ngay ngoại lệ `IOException` (Connection reset). Cần lệnh `QUIT` để kết thúc phiên kết nối.
  - Với **UDP**: Là giao thức phi trạng thái (Stateless/Connectionless). Khi Server tắt, Client không hề hay biết và vẫn gửi gói tin đi bình thường; sau đó Client phát hiện sự cố nhờ cơ chế `SocketTimeoutException` sau 3 giây. Không cần lệnh `QUIT`.

### 4.8. Bài tập 4: Máy tính từ xa (`RemoteCalculatorServer`)
- Cú pháp: `CALC <toán tử> <số 1> <số 2>` (Ví dụ: `CALC + 100 200`).
- Tách chuỗi bằng regex `split("\\s+")` để xử lý cả trường hợp có nhiều dấu cách.
- Bắt chính xác 4 loại mã lỗi:
  1. `ERR INVALID_FORMAT`: Khi thiếu thành phần hoặc sai tiền tố.
  2. `ERR UNSUPPORTED_OPERATOR`: Khi toán tử không thuộc `+`, `-`, `*`, `/` (ví dụ `%`).
  3. `ERR INVALID_NUMBER`: Khi toán hạng không thể chuyển thành số (ví dụ `a`).
  4. `ERR DIVIDE_BY_ZERO`: Khi thực hiện phép chia cho số 0.

### 4.9. Bài tập 5: Phòng chat TCP đa Client (`ChatServer` & `ChatClient`)
- **Quản lý danh sách an toàn luồng**:
  - Dùng `ConcurrentHashMap<String, PrintWriter>` để lưu cặp `nickname -> luồng ghi của client`. Khi nhiều client kết nối/ngắt kết nối cùng lúc sẽ không bị lỗi xung đột dữ liệu (ConcurrentModificationException).
- **Quy trình hoạt động**:
  1. Khi mới kết nối, client bắt buộc phải đặt nickname bằng lệnh `NICK <tên>`. Nếu tên đã có người dùng, server trả về `ERR NICK_TAKEN`.
  2. Khi gửi `MSG <nội dung>`, server duyệt qua Map và broadcast tin nhắn đến tất cả các client khác.
  3. Khi gõ `USERS`, server trả về danh sách các user đang trực tuyến.
  4. Khi gõ `QUIT` hoặc ngắt đột ngột (tắt cửa sổ Terminal), server xóa client khỏi danh sách và gửi thông báo rời phòng cho mọi người.
- **Client đa luồng**:
  - Client gồm 2 luồng: 1 luồng nền liên tục lắng nghe tin nhắn broadcast từ server để in ra màn hình; 1 luồng chính đọc phím người dùng gõ.

---

## 5. HƯỚNG DẪN BIÊN DỊCH & LỆNH CHẠY THỰC NGHIỆM

Mở Terminal tại thư mục `lab4_tcp`:

### Bước 1: Biên dịch toàn bộ dự án
```bash
javac -d out src/*/*.java
```

### Bước 2: Chạy kiểm tra các bài

#### 1. Khảo sát địa chỉ mạng (Mục 4.1 & Bài 1)
```bash
# Kiểm tra localhost (Mục 4.1)
java -cp out Muc_4_1_KhaoSatDiaChiMang.HostInspector localhost

# Kiểm tra bài 1 (Host & URI)
java -cp out Bai_Tap_1_HostUriInspector.HostAndUriInspector localhost "https://iuh.edu.vn:8080/tuyen-sinh?khoa=cntt#muc1"
```

#### 2. Thử nghiệm TCP Server & Client (Mục 4.2 & 4.3)
- **Terminal 1 (Server)**:
  ```bash
  java -cp out Muc_4_3_MultiClientTcp.MultiClientTcpServer 5050
  ```
- **Terminal 2 (Client)**:
  ```bash
  java -cp out Muc_4_2_TcpCommand.TcpCommandClient localhost 5050
  ```

#### 3. Thử nghiệm UDP Echo (Mục 4.4)
- **Terminal 1 (Server)**:
  ```bash
  java -cp out Muc_4_4_UdpEcho.UdpEchoServer 5001
  ```
- **Terminal 2 (Client)**:
  ```bash
  java -cp out Muc_4_4_UdpEcho.UdpEchoClient localhost 5001 "xin chào UDP"
  ```

#### 4. Thử nghiệm Bài 2 (Đổi số sang chữ)
- **Server**:
  ```bash
  java -cp out Bai_Tap_2_DoiChuSoThanhChu.DigitToWordServer
  ```
- **Client**:
  ```bash
  java -cp out Bai_Tap_2_DoiChuSoThanhChu.DigitToWordClient localhost 5002
  ```

#### 5. Thử nghiệm Bài 4 (Máy tính từ xa)
- **Server**:
  ```bash
  java -cp out Bai_Tap_4_MayTinhTuXa.RemoteCalculatorServer
  ```
- **Client**:
  ```bash
  java -cp out Bai_Tap_4_MayTinhTuXa.RemoteCalculatorClient localhost 5005
  ```

#### 6. Thử nghiệm Bài 5 (Phòng Chat nhiều người)
- **Terminal 1**:
  ```bash
  java -cp out Bai_Tap_5_ChatTcpNhieuClient.ChatServer
  ```
- **Terminal 2 (User 1)**:
  ```bash
  java -cp out Bai_Tap_5_ChatTcpNhieuClient.ChatClient localhost 5006
  ```

---

## 6. BẰNG CHỨNG KIỂM THỬ (EVIDENCE / TEST RESULTS)

Toàn bộ các ca kiểm thử đã được chạy thực tế trên máy và ghi nhận tại [`evidence/test-results.txt`](file:///Users/min/Desktop/IUH-k2/PTHTTH/Pthtth_25765141_letuanminh_31/lab4_tcp/evidence/test-results.txt):

| STT | Bài / Mục | Ca kiểm thử | Đầu vào | Kết quả thực tế | Trạng thái |
| :---: | :--- | :--- | :--- | :--- | :---: |
| 1 | **4.1 HostInspector** | Localhost & Loopback | `localhost` | Hiển thị IPv4 (`127.0.0.1`), IPv6 (`::1`), `Loopback: true` | **PASS** |
| 2 | **4.1 HostInspector** | Tên miền không tồn tại | `host-khong-ton-tai.invalid` | Thông báo lỗi tiếng Việt, không văng stacktrace | **PASS** |
| 3 | **Bài 1 URI Inspector** | Bóc tách URI hợp lệ | `https://localhost:5000/api?user=minh#sec1` | Bóc tách đúng Scheme, Host, Port 5000, Path, Query, Fragment | **PASS** |
| 4 | **Bài 1 URI Inspector** | URI có khoảng trắng | `http://bad url with space` | Bắt lỗi `URISyntaxException: Illegal character in authority` | **PASS** |
| 5 | **4.2 TCP Server** | Lệnh PING / UPPER / TIME | `PING`, `UPPER test`, `TIME` | `OK PONG`, `OK TEST`, `OK 2026-10-11T...` | **PASS** |
| 6 | **4.3 Multi-Client** | 3 client đồng thời | 3 kết nối song song | Cả 3 client nhận phản hồi đồng thời, không block | **PASS** |
| 7 | **4.4 UDP Echo** | Tiếng Việt có dấu | `"xin chào UDP"` | Nhận phản hồi đúng dấu: `"ACK XIN CHÀO UDP"` | **PASS** |
| 8 | **4.4 UDP Timeout** | Server tắt / Sai cổng | Gửi tới port `5999` | Thông báo sau 3 giây: `"Hết 3 giây nhưng chưa nhận được phản hồi"` | **PASS** |
| 9 | **Bài 2 Ca biên** | Số hợp lệ | `0`, `9` | `không`, `chín` | **PASS** |
| 10 | **Bài 2 Ca biên** | Chuỗi rỗng, số 10, chữ a, space | `""`, `10`, `a`, `" 5 "` | Tất cả đều trả về `ERR INVALID_DIGIT` | **PASS** |
| 11 | **Bài 3 Ngày giờ** | Khuôn dạng ngày giờ | `DATE`, `TIME`, `DATETIME` | `OK 11/10/2026`, `OK 15:41:17`, `OK 11/10/2026 15:41:17` | **PASS** |
| 12 | **Bài 4 Máy tính** | Phép chia 0 | `CALC / 10 0` | `ERR DIVIDE_BY_ZERO` | **PASS** |
| 13 | **Bài 4 Máy tính** | Sai toán tử | `CALC % 10 3` | `ERR UNSUPPORTED_OPERATOR` | **PASS** |
| 14 | **Bài 4 Máy tính** | Sai kiểu số | `CALC + a 2` | `ERR INVALID_NUMBER` | **PASS** |
| 15 | **Bài 5 Chat** | Nick trùng lặp | Trùng nickname `Alice` | `ERR NICK_TAKEN. Vui lòng chọn tên khác!` | **PASS** |
| 16 | **Bài 5 Chat** | Broadcast tin nhắn | Alice nhắn `MSG Xin chao` | Bob lập tức nhận được `[Alice]: Xin chao` | **PASS** |

---

## 7. KINH NGHIỆM & ĐÚC KẾT QUAN TRỌNG CHO NEWBIE

1. **Tránh xung đột cổng trên macOS**: Trên macOS, cổng `5000` mặc định được hệ thống dùng cho AirPlay Receiver. Do đó, hãy viết code nhận port linh hoạt qua `args` hoặc sử dụng các cổng như `5002`, `5003`, `5050`, `6000` để chạy thực hành.
2. **Luôn sử dụng `StandardCharsets.UTF_8`**: Tránh dùng `getBytes()` hay `InputStreamReader` không chỉ định charset, vì nếu chạy giữa các máy Windows và Mac có thể bị lỗi font chữ tiếng Việt.
3. **Luôn dùng `try-with-resources`**: Cú pháp `try (Socket socket = ...) { ... }` giúp socket tự động đóng ngay cả khi có ngoại lệ xảy ra, tránh tối đa tình trạng "rò rỉ tài nguyên mạng" (socket leak).
4. **Hiểu bản chất khác nhau giữa TCP và UDP**:
   - Dùng **TCP** khi cần **độ tin cậy tuyệt đối** (như Chat, Đăng nhập, Truyền file, Tính toán API).
   - Dùng **UDP** khi cần **tốc độ cao, độ trễ thấp** và chấp nhận mất một số gói tin (như Live stream, Voice call, Game trực tuyến, Khám phá dịch vụ mạng Discovery).
