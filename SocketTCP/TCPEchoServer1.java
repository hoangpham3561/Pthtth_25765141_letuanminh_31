import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Slide 133-135 — PTCPEchoServer (Server TCP Echo phuc vu song song)
 * Phan 1: chap nhan ket noi. Phan 2: moi client mot thread RequestProcessing.
 */
public class TCPEchoServer1 {
    public final static int serverPort = 6969;

    public static void main(String[] args) {
        try {
            ServerSocket ss = new ServerSocket(serverPort);
            System.out.println("Server da duoc tao");

            while (true) {
                try {
                    // Chap nhan ket noi, tao thread xu ly rieng cho client
                    Socket s = ss.accept();
                    RequestProcessing rp = new RequestProcessing(s);
                    rp.start();
                } catch (IOException ie) {
                    System.out.println("Connection Error: " + ie);
                }
            }
        } catch (IOException ie) {
            System.out.println("Server Creation Error: " + ie);
        }
    }
}
