package bai2_tinhtong;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class CalculatorServer {
    public static final int PORT = 1100;
    public static final String SERVICE_NAME = "CalculatorService";

    public static void main(String[] args) {
        try {
            CalculatorService service = new CalculatorServiceImpl();
            Registry registry;
            try {
                registry = LocateRegistry.createRegistry(PORT);
            } catch (Exception e) {
                registry = LocateRegistry.getRegistry(PORT);
            }
            registry.rebind(SERVICE_NAME, service);
            System.out.println("[CalculatorServer] Da khoi dong tren port " + PORT + " voi service: " + SERVICE_NAME);
        } catch (Exception e) {
            System.err.println("[CalculatorServer] Loi khoi dong: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}
