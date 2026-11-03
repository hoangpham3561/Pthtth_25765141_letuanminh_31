package bai5_chat;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;

public class ChatClient {
    public static void main(String[] args) {
        try {
            Registry registry = LocateRegistry.getRegistry("localhost", ChatServer.PORT);
            ChatService service = (ChatService) registry.lookup(ChatServer.SERVICE_NAME);

            if (args.length > 0 && args[0].equalsIgnoreCase("test")) {
                service.sendMessage("Hello from Automated Test");
                String reply = service.getLastReply();
                System.out.println("[ChatClient] Phan hoi nhan duoc: " + reply);

                if (reply != null && reply.contains("Hello from Automated Test")) {
                    System.out.println("[ChatClient] Test PASS!");
                    System.exit(0);
                } else {
                    System.err.println("[ChatClient] Test FAIL!");
                    System.exit(1);
                }
                return;
            }

            Scanner sc = new Scanner(System.in);
            System.out.println("=== ỨNG DỤNG CHAT 1-1 RMI (CLIENT - SERVER) ===");
            System.out.println("(Nhập 'exit' để thoát)");
            while (true) {
                System.out.print("\nClient > ");
                String msg = sc.nextLine();
                if ("exit".equalsIgnoreCase(msg.trim())) break;

                service.sendMessage(msg);
                String reply = service.getLastReply();
                System.out.println("Server > " + reply);
            }
        } catch (Exception e) {
            System.err.println("[ChatClient] Lỗi: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}
