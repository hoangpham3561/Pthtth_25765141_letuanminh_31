# CHƯƠNG TRÌNH CHAT NHÓM QUA TCP THEO PHÒNG (KÈM DASHBOARD GIÁM SÁT)
> **Môn học**: Phát triển Hệ thống Tích hợp & Phân tán (PTHTTH) - FIT.IUH  
> **Kiến trúc**: 3 thành phần độc lập (`server/`, `client/`, `dashboard/`)  
> **Trình độ**: Dành cho người mới học (Newbie Level) - Code trong sáng, dễ hiểu, chú thích đầy đủ.

---

## 1. CẤU TRÚC DỰ ÁN

```text
Test_exam/
├── server/
│   └── ChatServer.java      # Server TCP quản lý phòng chat, kết nối đa client và gửi log tới Dashboard
├── client/
│   └── ChatClient.java      # Client giao diện Console với 2 luồng độc lập (nhận tin và gửi tin)
├── dashboard/
│   └── ChatDashboard.java   # Trung tâm giám sát hệ thống (GUI Swing đẹp mắt & Realtime Logs)
├── test_chat.sh             # Script tự động hóa kiểm thử nhiều Client cùng lúc
└── README.md                # Tài liệu hướng dẫn sử dụng và giải thích kiến trúc
```

---

## 2. CHỨC NĂNG CỦA TỪNG FILE

### 1. File `server/ChatServer.java` (Máy chủ)
- Lắng nghe cổng **`5006`** (hoặc cổng truyền qua tham số dòng lệnh).
- Quản lý danh sách các phòng mặc định (`Study`, `Game`, `TraiNghiem`) và tự tạo phòng mới khi người dùng đề xuất.
- Phân phát tin nhắn: **Chỉ các client trong CÙNG phòng mới nhận được tin nhắn của nhau**.
- Xử lý các lệnh điều khiển:
  - `/quit`: Rời phòng, quay lại menu chọn phòng.
  - `/exit`: Ngắt kết nối khỏi Server an toàn.
- Tự động đẩy thông báo sự kiện (Realtime Logs) và số liệu thống kê phòng tới **Dashboard**.

### 2. File `client/ChatClient.java` (Người dùng trò chuyện)
- Nhập Tên người dùng trước khi kết nối tới Server.
- Mô hình 2 luồng (`Receive Thread` và `Main Thread`).
- Chọn phòng theo số thứ tự (`1`, `2`, `3`) hoặc gõ tên phòng.
- Trò chuyện với các thành viên khác trong phòng, chuyển phòng bằng `/quit`, thoát bằng `/exit`.

### 3. File `dashboard/ChatDashboard.java` (Trung tâm giám sát hệ thống)
- **Giao diện đồ họa Swing (GUI)** trực quan:
  - Bảng thống kê số lượng phòng và danh sách người dùng online trong từng phòng.
  - Bảng nhật ký sự kiện thời gian thực (Realtime Logs): ai kết nối, ai vào phòng, ai chat, ai rời phòng.
  - Nút **Làm mới thống kê** (Refresh).
  - Tính năng **Gửi thông báo toàn hệ thống** (Admin Broadcast): Cho phép quản trị viên phát loa thông báo tới TẤT CẢ các phòng chat!
- Tự động fallback sang chế độ Console nếu chạy trên môi trường không có màn hình (Terminal/SSH).

---

## 3. HƯỚNG DẪN BIÊN DỊCH VÀ CHẠY THỰC NGHIỆM

Mở Terminal tại thư mục `Test_exam`:

### Bước 1: Biên dịch toàn bộ 3 file
```bash
javac server/ChatServer.java client/ChatClient.java dashboard/ChatDashboard.java
```

---

### Bước 2: Khởi động Server (Terminal 1)
```bash
java -cp server ChatServer
```
*(Server chạy trên cổng mặc định 5006)*

---

### Bước 3: Khởi động Dashboard giám sát (Terminal 2)
```bash
java -cp dashboard ChatDashboard
```
*(Một cửa sổ đồ họa giao diện Dashboard sẽ mở lên, hiển thị trạng thái kết nối tới Server và sẵn sàng theo dõi các hoạt động chat)*

---

### Bước 4: Khởi động Client 1 (Terminal 3)
```bash
java -cp client ChatClient
```
- Nhập tên: `An`
- Chọn phòng: gõ `1` (vào phòng `Study`)
- Nhắn tin: `Chào cả nhà!`
👉 *Nhìn sang cửa sổ Dashboard: Bạn sẽ thấy Dashboard lập tức ghi nhận sự kiện An kết nối, vào phòng Study và tin nhắn chat realtime!*

---

### Bước 5: Khởi động Client 2 (Terminal 4)
```bash
java -cp client ChatClient
```
- Nhập tên: `Binh`
- Chọn phòng: gõ `1` (vào phòng `Study`)
- `An` và `Binh` chat với nhau trong phòng `Study`.

---

### Bước 6: Khởi động Client 3 (Terminal 5)
```bash
java -cp client ChatClient
```
- Nhập tên: `Cuong`
- Chọn phòng: gõ `2` (vào phòng `Game`)
- `Cuong` ở phòng Game được cách ly, không nhận tin nhắn của phòng Study.

---

### Bước 7: Thử nghiệm tính năng Admin từ Dashboard
- Tại cửa sổ Dashboard, nhập vào ô thông báo dưới cùng: `Server sẽ bảo trì sau 10 phút nữa` và bấm nút **Gửi toàn hệ thống**.
👉 *Tất cả các Client (cả An, Bình ở phòng Study và Cường ở phòng Game) đều nhận được thông báo từ hệ thống!*

---

## 4. HƯỚNG DẪN CHẠY THEO TỪNG THƯ MỤC RIÊNG

- **Chạy Server**:
  ```bash
  cd server
  javac ChatServer.java
  java ChatServer
  ```

- **Chạy Dashboard**:
  ```bash
  cd dashboard
  javac ChatDashboard.java
  java ChatDashboard
  ```

- **Chạy Client**:
  ```bash
  cd client
  javac ChatClient.java
  java ChatClient
  ```
