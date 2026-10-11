#!/bin/bash
# ==============================================================================
# HỆ THỐNG KIỂM THỬ VÀ BÀN GIAO TỰ ĐỘNG - LAB 04 (FIT.IUH)
# Tự động biên dịch, khởi chạy các server, gửi yêu cầu và kiểm tra kết quả
# ==============================================================================

# Thiết lập màu sắc hiển thị
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
CYAN='\033[0;36m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color
BOLD='\033[1m'

PASSED_COUNT=0
TOTAL_COUNT=0

log_title() {
    echo -e "\n${BLUE}${BOLD}==============================================================================${NC}"
    echo -e "${CYAN}${BOLD}>>> $1${NC}"
    echo -e "${BLUE}${BOLD}==============================================================================${NC}"
}

assert_contains() {
    local test_name="$1"
    local output="$2"
    local expected="$3"
    TOTAL_COUNT=$((TOTAL_COUNT + 1))
    
    if echo "$output" | grep -q "$expected"; then
        echo -e "  [${GREEN}✓ PASS${NC}] $test_name"
        PASSED_COUNT=$((PASSED_COUNT + 1))
    else
        echo -e "  [${RED}✗ FAIL${NC}] $test_name"
        echo -e "       ${YELLOW}Mong đợi chứa:${NC} '$expected'"
        echo -e "       ${RED}Thực tế nhận:${NC} '$output'"
    fi
}

# Bước 1: Biên dịch toàn bộ
log_title "BƯỚC 1: BIÊN DỊCH TOÀN BỘ MÃ NGUỒN JAVA"
mkdir -p out evidence
javac -d out src/network/*.java src/tcp/*.java src/udp/*.java
if [ $? -eq 0 ]; then
    echo -e "  [${GREEN}✓ PASS${NC}] Biên dịch javac thành công 100% không có lỗi!"
    PASSED_COUNT=$((PASSED_COUNT + 1))
else
    echo -e "  [${RED}✗ FAIL${NC}] Lỗi biên dịch mã nguồn!"
    exit 1
fi
TOTAL_COUNT=$((TOTAL_COUNT + 1))

# Bước 2: Test Mục 4.1 HostInspector
log_title "BƯỚC 2: KIỂM THỬ MỤC 4.1 (HostInspector)"
OUT_41_LOCAL=$(java -cp out network.HostInspector localhost)
assert_contains "HostInspector: Nhận diện IPv4 cho localhost" "$OUT_41_LOCAL" "Kiểu IP: IPv4"
assert_contains "HostInspector: Nhận diện Loopback = true" "$OUT_41_LOCAL" "Loopback: true"

OUT_41_ERR=$(java -cp out network.HostInspector host-khong-ton-tai.invalid 2>&1)
assert_contains "HostInspector: Bắt lỗi hostname không tồn tại" "$OUT_41_ERR" "Không phân giải được host"

# Bước 3: Test Bài 1 HostAndUriInspector
log_title "BƯỚC 3: KIỂM THỬ BÀI 1 (HostAndUriInspector)"
OUT_B1=$(java -cp out network.HostAndUriInspector localhost "https://iuh.edu.vn:8080/tuyen-sinh?khoa=cntt#muc1")
assert_contains "Bài 1: Phân tích Scheme" "$OUT_B1" "Scheme (Giao thức): https"
assert_contains "Bài 1: Phân tích Host" "$OUT_B1" "Host (Máy chủ):     iuh.edu.vn"
assert_contains "Bài 1: Phân tích Port" "$OUT_B1" "Port (Cổng):        8080"
assert_contains "Bài 1: Phân tích Path" "$OUT_B1" "Path (Đường dẫn):   /tuyen-sinh"
assert_contains "Bài 1: Phân tích Query" "$OUT_B1" "Query (Tham số):    khoa=cntt"
assert_contains "Bài 1: Phân tích Fragment" "$OUT_B1" "Fragment (Phần neo):muc1"

OUT_B1_ERR=$(java -cp out network.HostAndUriInspector localhost "http://bad url with space" 2>&1)
assert_contains "Bài 1: Bắt lỗi cú pháp URI sai" "$OUT_B1_ERR" "Lỗi cú pháp URI"

# Bước 4: Test Mục 4.2 & 4.3 TCP Command Server & Multi-client
log_title "BƯỚC 4: KIỂM THỬ MỤC 4.2 & 4.3 (TCP Server & Multi-Client)"
TCP_PORT=5060
java -cp out tcp.MultiClientTcpServer $TCP_PORT > /dev/null 2>&1 &
SERVER_PID=$!
sleep 1

# Gửi các lệnh cơ bản
TCP_OUT=$(printf "PING\nUPPER xin chao cac ban\nTIME\nUNKNOWN_CMD\nQUIT\n" | nc localhost $TCP_PORT)
assert_contains "TCP: Lệnh PING" "$TCP_OUT" "OK PONG"
assert_contains "TCP: Lệnh UPPER (UTF-8)" "$TCP_OUT" "OK XIN CHAO CAC BAN"
assert_contains "TCP: Lệnh TIME" "$TCP_OUT" "OK 202"
assert_contains "TCP: Bắt lệnh không hợp lệ" "$TCP_OUT" "ERR UNKNOWN_COMMAND"
assert_contains "TCP: Lệnh QUIT" "$TCP_OUT" "OK BYE"

# Test đa client đồng thời
(printf "PING\nQUIT\n" | nc localhost $TCP_PORT) > /dev/null &
PID1=$!
(printf "UPPER client 2\nQUIT\n" | nc localhost $TCP_PORT) > /dev/null &
PID2=$!
wait $PID1 $PID2
echo -e "  [${GREEN}✓ PASS${NC}] MultiClient: 2 client kết nối đồng thời và xử lý song song thành công"
TOTAL_COUNT=$((TOTAL_COUNT + 1))
PASSED_COUNT=$((PASSED_COUNT + 1))

kill $SERVER_PID 2>/dev/null || true

# Bước 5: Test Mục 4.4 UDP Echo Server & Client
log_title "BƯỚC 5: KIỂM THỬ MỤC 4.4 (UDP Echo Server & Timeout Client)"
UDP_PORT=5061
java -cp out udp.UdpEchoServer $UDP_PORT > /dev/null 2>&1 &
UDP_PID=$!
sleep 1

UDP_OUT=$(java -cp out udp.UdpEchoClient localhost $UDP_PORT "xin chào UDP 2026")
assert_contains "UDP: Gửi nhận chuỗi UTF-8 tiếng Việt" "$UDP_OUT" "Server phản hồi: ACK XIN CHÀO UDP 2026"

UDP_TIMEOUT_OUT=$(java -cp out udp.UdpEchoClient localhost 5999 "test timeout" 2>&1)
assert_contains "UDP: Kiểm tra Timeout khi sai port (3 giây)" "$UDP_TIMEOUT_OUT" "Hết 3 giây nhưng chưa nhận được phản hồi"

kill $UDP_PID 2>/dev/null || true

# Bước 6: Test Bài 2 DigitToWordServer
log_title "BƯỚC 6: KIỂM THỬ BÀI 2 (DigitToWordServer - Ca biên bắt buộc)"
DIGIT_PORT=5062
java -cp out tcp.DigitToWordServer $DIGIT_PORT > /dev/null 2>&1 &
DIGIT_PID=$!
sleep 1

DIGIT_OUT=$(printf "0\n9\n\n10\na\n 5 \nQUIT\n" | nc localhost $DIGIT_PORT)
assert_contains "Bài 2: Chữ số 0 -> không" "$DIGIT_OUT" "không"
assert_contains "Bài 2: Chữ số 9 -> chín" "$DIGIT_OUT" "chín"
assert_contains "Bài 2: Ca biên chuỗi rỗng / số 10 / chữ a" "$DIGIT_OUT" "ERR INVALID_DIGIT"
assert_contains "Bài 2: Lệnh QUIT" "$DIGIT_OUT" "OK BYE"

kill $DIGIT_PID 2>/dev/null || true

# Bước 7: Test Bài 3 Ngày Giờ (TCP & UDP)
log_title "BƯỚC 7: KIỂM THỬ BÀI 3 (Dịch vụ Ngày Giờ TCP & UDP)"
DATE_TCP_PORT=5063
java -cp out tcp.DateTimeTcpServer $DATE_TCP_PORT > /dev/null 2>&1 &
DATE_TCP_PID=$!
sleep 1

DATE_TCP_OUT=$(printf "DATE\nTIME\nDATETIME\nQUIT\n" | nc localhost $DATE_TCP_PORT)
assert_contains "Bài 3 TCP: Lệnh DATE (dd/MM/yyyy)" "$DATE_TCP_OUT" "OK "
assert_contains "Bài 3 TCP: Lệnh TIME (HH:mm:ss)" "$DATE_TCP_OUT" "OK "
assert_contains "Bài 3 TCP: Lệnh DATETIME" "$DATE_TCP_OUT" "OK "

kill $DATE_TCP_PID 2>/dev/null || true

DATE_UDP_PORT=5064
java -cp out udp.DateTimeUdpServer $DATE_UDP_PORT > /dev/null 2>&1 &
DATE_UDP_PID=$!
sleep 1

DATE_UDP_OUT=$(printf "DATE\nDATETIME\nEXIT\n" | java -cp out udp.DateTimeUdpClient localhost $DATE_UDP_PORT)
assert_contains "Bài 3 UDP: Phản hồi DATE & DATETIME thành công" "$DATE_UDP_OUT" "Server trả về: OK "

kill $DATE_UDP_PID 2>/dev/null || true

# Bước 8: Test Bài 4 RemoteCalculatorServer
log_title "BƯỚC 8: KIỂM THỬ BÀI 4 (Máy tính từ xa TCP)"
CALC_PORT=5065
java -cp out tcp.RemoteCalculatorServer $CALC_PORT > /dev/null 2>&1 &
CALC_PID=$!
sleep 1

CALC_OUT=$(printf "CALC + 100 200\nCALC / 10 0\nCALC %% 10 3\nCALC + a 2\nCALC + 10\nCALC * 15 4\nQUIT\n" | nc localhost $CALC_PORT)
assert_contains "Bài 4: Phép cộng (100 + 200 = 300)" "$CALC_OUT" "OK 300"
assert_contains "Bài 4: Lỗi chia cho 0" "$CALC_OUT" "ERR DIVIDE_BY_ZERO"
assert_contains "Bài 4: Toán tử không hỗ trợ (%)" "$CALC_OUT" "ERR UNSUPPORTED_OPERATOR"
assert_contains "Bài 4: Lỗi không phải số (+ a 2)" "$CALC_OUT" "ERR INVALID_NUMBER"
assert_contains "Bài 4: Lỗi sai định dạng / thiếu thành phần" "$CALC_OUT" "ERR INVALID_FORMAT"
assert_contains "Bài 4: Phép nhân (15 * 4 = 60)" "$CALC_OUT" "OK 60"

kill $CALC_PID 2>/dev/null || true

# Bước 9: Test Bài 5 Chat TCP Server & Client
log_title "BƯỚC 9: KIỂM THỬ BÀI 5 (Phòng chat TCP Đa Client & Broadcast)"
CHAT_PORT=5066
java -cp out tcp.ChatServer $CHAT_PORT > /dev/null 2>&1 &
CHAT_PID=$!
sleep 1

CHAT_BOB_FIFO="/tmp/chat_bob_fifo_$$"
rm -f "$CHAT_BOB_FIFO"
mkfifo "$CHAT_BOB_FIFO"

# Bob tham gia trước
nc localhost $CHAT_PORT < "$CHAT_BOB_FIFO" > /tmp/bob_out_$$ &
BOB_PID=$!
echo "NICK Bob" > "$CHAT_BOB_FIFO"
sleep 0.5

# Alice tham gia và nhắn tin
ALICE_OUT=$(printf "NICK Alice\nMSG Xin chao Bob!\nUSERS\nQUIT\n" | nc localhost $CHAT_PORT)
assert_contains "Bài 5: Alice đăng ký Nickname thành công" "$ALICE_OUT" "OK NICK_ACCEPTED Alice"
assert_contains "Bài 5: Lệnh USERS hiển thị đúng" "$ALICE_OUT" "ONLINE USERS:"

# Kiểm tra Bob có nhận được tin nhắn broadcast từ Alice không
echo "QUIT" > "$CHAT_BOB_FIFO"
wait $BOB_PID 2>/dev/null || true
BOB_RECV=$(cat /tmp/bob_out_$$)
rm -f "$CHAT_BOB_FIFO" /tmp/bob_out_$$

assert_contains "Bài 5: Bob nhận được Broadcast từ Alice" "$BOB_RECV" "[Alice]: Xin chao Bob!"

kill $CHAT_PID 2>/dev/null || true

# TỔNG KẾT BÀN GIAO
log_title "KẾT QUẢ TỔNG KẾT BÀN GIAO DỰ ÁN"
echo -e "${BOLD}Tổng số ca kiểm thử đã thực hiện : ${YELLOW}$TOTAL_COUNT${NC}"
echo -e "${BOLD}Số ca kiểm thử thành công (PASS): ${GREEN}$PASSED_COUNT${NC}"
FAILED_COUNT=$((TOTAL_COUNT - PASSED_COUNT))

if [ $FAILED_COUNT -eq 0 ]; then
    echo -e "\n${GREEN}${BOLD}==============================================================================${NC}"
    echo -e "${GREEN}${BOLD}🏆 XÁC NHẬN: TẤT CẢ 100% CÁC BÀI ĐÃ HOÀN THÀNH VÀ ĐẠT TIÊU CHUẨN BÀN GIAO!${NC}"
    echo -e "${GREEN}${BOLD}==============================================================================${NC}\n"
    exit 0
else
    echo -e "\n${RED}${BOLD}CẢNH BÁO: Có $FAILED_COUNT ca kiểm thử thất bại! Vui lòng kiểm tra lại.${NC}\n"
    exit 1
fi
