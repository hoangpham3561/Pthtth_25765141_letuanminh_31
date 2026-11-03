package bai3_songuyento;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class PrimeServer {
    public static final int PORT = 1101;
    public static final String SERVICE_NAME = "PrimeService";

    public static void main(String[] args) {
        try {
            PrimeService service = new PrimeServiceImpl();
            Registry registry;
            try {
                registry = LocateRegistry.createRegistry(PORT);
            } catch (Exception e) {
                registry = LocateRegistry.getRegistry(PORT);
            }
            registry.rebind(SERVICE_NAME, service);
            System.out.println("[PrimeServer] Da khoi dong tren port " + PORT + " voi service: " + SERVICE_NAME);
        } catch (Exception e) {
            System.err.println("[PrimeServer] Loi khoi dong: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}
