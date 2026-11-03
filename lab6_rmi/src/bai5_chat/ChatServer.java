package bai5_chat;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class ChatServer {
    public static final int PORT = 1103;
    public static final String SERVICE_NAME = "ChatService";

    public static void main(String[] args) {
        try {
            ChatService service = new ChatServiceImpl();
            Registry registry;
            try {
                registry = LocateRegistry.createRegistry(PORT);
            } catch (Exception e) {
                registry = LocateRegistry.getRegistry(PORT);
            }
            registry.rebind(SERVICE_NAME, service);
            System.out.println("[ChatServer] Da khoi dong tren port " + PORT + " voi service: " + SERVICE_NAME);
        } catch (Exception e) {
            System.err.println("[ChatServer] Loi khoi dong: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}
