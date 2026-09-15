import java.io.*;
import java.net.Socket;

/**
 * Slide 127 — TCPEchoClient
 * Gui tung byte '0'..'9' den Echo Server, doc va in byte echo tra ve.
 */
public class TCPEchoClient {
    public final static String serverIP = "127.0.0.1";
    public final static int serverPort = 6969;

    public static void main(String[] args) throws InterruptedException, IOException {
        Socket s = null;
        try {
            // Mo socket noi ket den server
            s = new Socket(serverIP, serverPort);
            System.out.println("Client da duoc tao");
            InputStream is = s.getInputStream();
            OutputStream os = s.getOutputStream();

            // Gui '0' -> '9' den server, moi lan cho 2s
            for (int i = '0'; i <= '9'; i++) {
                os.write(i);
                int ch = is.read();
                System.out.println((char) ch);
                Thread.sleep(2000);
            }
        } catch (IOException ie) {
            System.out.println("Error: Can NOT create socket");
        } finally {
            if (s != null) {
                s.close();
            }
        }
    }
}
