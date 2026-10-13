#!/bin/bash
# ==============================================================================
# Script tự động kiểm thử chương trình Chat Nhóm TCP (Tách Server và Client)
# Kiểm tra đầy đủ các yêu cầu đề bài:
# 1. Client nhập Tên và kết nối tới server.
# 2. Chọn 1 phòng trong danh sách (Study, Game, TraiNghiem...).
# 3. Chỉ nhận tin nhắn từ các client khác trong CÙNG phòng đó (cách ly phòng).
# 4. Nhập /quit để rời phòng và quay lại danh sách phòng.
# 5. Nhập /exit để ngắt kết nối tới server.
# ==============================================================================

PORT=5099
SERVER_LOG="server_test.log"
CLIENT1_LOG="client1_an.log"
CLIENT2_LOG="client2_binh.log"
CLIENT3_LOG="client3_cuong.log"

rm -f $SERVER_LOG $CLIENT1_LOG $CLIENT2_LOG $CLIENT3_LOG

echo ">>> 1. Biên dịch mã nguồn Server và Client..."
javac server/ChatServer.java client/ChatClient.java
if [ $? -ne 0 ]; then
    echo "Lỗi biên dịch!"
    exit 1
fi
echo "Biên dịch thành công!"

echo ">>> 2. Khởi động ChatServer trên cổng $PORT..."
java -cp server ChatServer $PORT > $SERVER_LOG 2>&1 &
SERVER_PID=$!
sleep 1

# Kiểm tra xem Server đã lắng nghe chưa
if ! ps -p $SERVER_PID > /dev/null; then
    echo "Server không khởi động được!"
    cat $SERVER_LOG
    exit 1
fi
echo "Server đang chạy (PID: $SERVER_PID)"

echo ">>> 3. Chạy Client 2 (Binh vào phòng Study)..."
(
  sleep 0.5
  echo "Binh"
  sleep 0.5
  echo "1"
  sleep 2
  echo "Binh nghe ro roi nhe An!"
  sleep 3
  echo "/exit"
) | java -cp client ChatClient localhost $PORT > $CLIENT2_LOG 2>&1 &
C2_PID=$!

echo ">>> 4. Chạy Client 3 (Cuong vào phòng Game)..."
(
  sleep 0.5
  echo "Cuong"
  sleep 0.5
  echo "2"
  sleep 4
  echo "Cuong dang o phong Game!"
  sleep 2
  echo "/exit"
) | java -cp client ChatClient localhost $PORT > $CLIENT3_LOG 2>&1 &
C3_PID=$!

echo ">>> 5. Chạy Client 1 (An)..."
(
  sleep 0.2
  echo "An"
  sleep 0.5
  echo "1"
  sleep 1
  echo "Xin chao phong Study tu An!"
  sleep 1.5
  echo "/quit"
  sleep 0.5
  echo "2"
  sleep 1
  echo "An da qua phong Game!"
  sleep 1.5
  echo "/exit"
) | java -cp client ChatClient localhost $PORT > $CLIENT1_LOG 2>&1

# Chờ các client kết thúc
wait $C2_PID
wait $C3_PID
kill $SERVER_PID 2>/dev/null
wait $SERVER_PID 2>/dev/null

echo "=========================================================="
echo "KẾT QUẢ KIỂM THỬ THỰC TẾ:"
echo "=========================================================="
echo "--- LOG CLIENT 1 (An) ---"
cat $CLIENT1_LOG
echo ""
echo "--- LOG CLIENT 2 (Binh - Phòng Study) ---"
cat $CLIENT2_LOG
echo ""
echo "--- LOG CLIENT 3 (Cuong - Phòng Game) ---"
cat $CLIENT3_LOG
echo ""
echo "--- LOG SERVER ---"
cat $SERVER_LOG

rm -f $SERVER_LOG $CLIENT1_LOG $CLIENT2_LOG $CLIENT3_LOG
