package bai4_danhba;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class ContactServer {
    public static final int PORT = 1102;
    public static final String SERVICE_NAME = "ContactService";

    public static void main(String[] args) {
        try {
            ContactService service = new ContactServiceImpl();
            Registry registry;
            try {
                registry = LocateRegistry.createRegistry(PORT);
            } catch (Exception e) {
                registry = LocateRegistry.getRegistry(PORT);
            }
            registry.rebind(SERVICE_NAME, service);
            System.out.println("[ContactServer] Da khoi dong tren port " + PORT + " voi service: " + SERVICE_NAME);
        } catch (Exception e) {
            System.err.println("[ContactServer] Loi khoi dong: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}
