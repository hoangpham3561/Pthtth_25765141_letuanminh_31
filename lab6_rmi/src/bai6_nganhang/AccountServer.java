package bai6_nganhang;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class AccountServer {
    public static final int PORT = 1104;
    public static final String SERVICE_NAME = "AccountService";
    public static final double INITIAL_BALANCE = 1000.0;

    public static void main(String[] args) {
        try {
            AccountService service = new AccountServiceImpl(INITIAL_BALANCE);
            Registry registry;
            try {
                registry = LocateRegistry.createRegistry(PORT);
            } catch (Exception e) {
                registry = LocateRegistry.getRegistry(PORT);
            }
            registry.rebind(SERVICE_NAME, service);
            System.out.println("[AccountServer] Da khoi dong tren port " + PORT + " voi so du ban dau: " + INITIAL_BALANCE);
        } catch (Exception e) {
            System.err.println("[AccountServer] Loi khoi dong: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}
