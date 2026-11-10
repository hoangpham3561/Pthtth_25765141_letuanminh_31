# CHƯƠNG TRÌNH CHAT NHÓM QUA TCP THEO PHÒNG (ROOM CHAT)
> **Môn học**: Phát triển Hệ thống Tích hợp & Phân tán (PTHTTH) - FIT.IUH  
> **Kiến trúc**: Phân tách độc lập 2 thư mục `server/` và `client/`  
> **Trình độ**: Dành cho người mới học (Newbie Level) - Code trong sáng, dễ hiểu, chú thích chi tiết.

---

## 1. ĐỀ BÀI YÊU CẦU

Xây dựng chương trình **Chat nhóm qua TCP** trong Java:
- Mỗi client nhập **Tên** và kết nối tới server.
- Chọn **1 phòng** trong danh sách phòng do server cung cấp (ví dụ: phòng *Study*, phòng *Game*, phòng *TraiNghiem*,... sinh viên có thể tự đề xuất tên phòng) và **chỉ nhận tin nhắn từ những client khác trong phòng đó**.
- Nhập `/quit` để rời phòng và quay lại danh sách các phòng.
- Nhập `/exit` để ngắt kết nối tới server.

---

## 2. CẤU TRÚC DỰ ÁN ĐÃ PHÂN TÁCH

```text
Test_exam/
├── server/
│   └── ChatServer.java      # Server quản lý kết nối, chia phòng và phân phát tin nhắn (Broadcast)
├── client/
│   └── ChatClient.java      # Client giao diện Console với 2 luồng (Luồng nhận và Luồng gửi)
├── test_chat.sh             # Script tự động hóa kiểm thử nhiều Client cùng lúc
└── README.md                # Tài liệu hướng dẫn sử dụng và giải thích kiến trúc
```

---

## 3. NGUYÊN LÝ HOẠT ĐỘNG (DÀNH CHO NEWBIE)

### A. Phía Server (`server/ChatServer.java`)
1. **Lắng nghe kết nối**: Server mở `ServerSocket` tại cổng `5006` (hoặc cổng được truyền vào qua tham số).
2. **Quản lý đa luồng an toàn**: Sử dụng `ExecutorService pool = Executors.newFixedThreadPool(50)` (kế thừa từ `lab4_tcp`) để phục vụ đồng thời nhiều Client mà không gây nghẽn kết nối.
3. **Danh sách phòng chat**: 
   - Có sẵn 3 phòng: `Study`, `Game`, `TraiNghiem`.
   - Client nhập số `1`, `2`, `3` hoặc tên phòng để vào.
   - Hỗ trợ **sinh viên tự đề xuất phòng mới**: Nhập tên phòng mới, Server sẽ tự động tạo và đưa client vào phòng đó.
4. **Cơ chế phân tách tin nhắn theo phòng (Broadcast to Room)**:
   - Server chỉ gửi tin nhắn cho những Client đang ở **CÙNG phòng** với người gửi.
   - Không gửi lại cho chính người gửi.
   - Các client ở phòng khác tuyệt đối **không nhận được** tin nhắn.
5. **Các lệnh điều khiển**:
   - `/quit`: Rời phòng hiện tại, thông báo cho các bạn cùng phòng, gửi lại danh sách phòng để chọn phòng mới.
   - `/exit`: Ngắt kết nối khỏi Server, đóng socket an toàn.

### B. Phía Client (`client/ChatClient.java`)
1. **Nhập Tên**: Đọc tên từ bàn phím console `System.in`, kiểm tra không được để trống.
2. **Kết nối Socket**: Mở kết nối TCP tới `localhost:5006`.
3. **Mô hình 2 luồng song song**:
   - **Luồng nhận (Receive Thread)**: Chạy ngầm `isDaemon(true)` liên tục nhận tin nhắn từ Server và in ra console.
   - **Luồng gửi (Main Thread)**: Nhập liệu từ bàn phím và gửi lên Server.

---

## 4. HƯỚNG DẪN BIÊN DỊCH VÀ CHẠY THỰC NGHIỆM

Bạn có thể chạy theo 2 cách cực kỳ tiện lợi:

### CÁCH 1: Mở Terminal riêng cho từng thư mục (Dễ hiểu nhất)

#### Bước 1: Khởi động Server (Terminal 1)
```bash
cd server
javac ChatServer.java
java ChatServer
```
*(Server chạy trên cổng mặc định 5006. Có thể truyền cổng khác: `java ChatServer 5007`)*

#### Bước 2: Khởi động Client 1 (Terminal 2)
```bash
cd client
javac ChatClient.java
java ChatClient
```
- Nhập tên: `An`
- Chọn phòng: nhập `1` (vào phòng `Study`)
- Nhắn tin trò chuyện!

#### Bước 3: Khởi động Client 2 (Terminal 3)
```bash
cd client
java ChatClient
```
- Nhập tên: `Binh`
- Chọn phòng: nhập `1` (vào phòng `Study`)
- `An` và `Binh` chat với nhau trong phòng `Study`!

#### Bước 4: Khởi động Client 3 (Terminal 4)
```bash
cd client
java ChatClient
```
- Nhập tên: `Cuong`
- Chọn phòng: nhập `2` (vào phòng `Game`)
- `Cuong` ở phòng `Game` hoàn toàn không nhận được tin nhắn của phòng `Study`!

---

### CÁCH 2: Chạy trực tiếp từ thư mục gốc `Test_exam`

```bash
# 1. Biên dịch cả Server và Client
javac server/ChatServer.java client/ChatClient.java

# 2. Chạy Server tại Terminal 1
java -cp server ChatServer

# 3. Chạy Client tại các Terminal khác
java -cp client ChatClient
```

---

## 5. THỬ NGHIỆM CÁC LỆNH ĐIỀU KHIỂN

### 1. Thử lệnh `/quit` (Chuyển phòng)
- Tại màn hình của `An`, gõ:
  ```text
  /quit
  ```
- **Kết quả**:
  - `Binh` nhận được thông báo: `[Hệ thống]: An đã rời phòng.`
  - `An` nhận lại danh sách phòng và có thể chọn phòng `2` (`Game`) để qua chat với `Cuong`!

### 2. Thử lệnh tự đề xuất tên phòng mới
- Tại menu chọn phòng, gõ:
  ```text
  BongDa
  ```
- **Kết quả**: Server tự động tạo phòng mới `BongDa` và đưa Client vào phòng.

### 3. Thử lệnh `/exit` (Ngắt kết nối)
- Gõ:
  ```text
  /exit
  ```
- **Kết quả**: Client gửi lời tạm biệt, đóng socket và ngắt kết nối an toàn khỏi Server.

---

## 6. KIỂM THỬ TỰ ĐỘNG BẰNG SCRIPT

Chạy file script kiểm thử tự động toàn bộ luồng:
```bash
chmod +x test_chat.sh
./test_chat.sh
```
Script sẽ biên dịch, khởi động server, cho 3 client tham gia các phòng khác nhau, chat chéo, đổi phòng bằng `/quit`, thoát bằng `/exit` và in kết quả kiểm tra.
