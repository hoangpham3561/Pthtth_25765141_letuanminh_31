import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Slide 131 — STCPEchoServer (Server TCP Echo phuc vu tuan tu)
 * Tai mot thoi diem chi phuc vu 1 client; client khac phai cho.
 */
public class TCPEchoServer {
    public final static int serverPort = 6969;

    public static void main(String[] args) {
        try {
            // Tao ServerSocket lang nghe tren cong 6969
            ServerSocket ss = new ServerSocket(serverPort);
            System.out.println("Server da duoc tao");

            while (true) {
                try {
                    // Chap nhan 1 ket noi, phuc vu xong moi nhan client tiep theo
                    Socket s = ss.accept();
                    OutputStream os = s.getOutputStream();
                    InputStream is = s.getInputStream();

                    while (true) {
                        int ch = is.read();
                        if (ch == -1) {
                            break; // client dong ket noi
                        }
                        System.out.println((char) ch);
                        os.write(ch); // echo lai dung byte vua nhan
                    }
                    s.close();
                } catch (IOException ie) {
                    System.out.println("Connection Error: " + ie);
                }
            }
        } catch (IOException ie) {
            System.out.println("Server Creation Error: " + ie);
        }
    }
}
