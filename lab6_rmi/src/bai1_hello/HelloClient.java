package bai1_hello;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class HelloClient {
    public static void main(String[] args) {
        try {
            Registry registry = LocateRegistry.getRegistry("localhost", HelloServer.PORT);
            HelloService service = (HelloService) registry.lookup(HelloServer.SERVICE_NAME);
            String response = service.sayHello();
            System.out.println("[HelloClient] Ket qua tu Server: " + response);

            if (args.length > 0 && args[0].equalsIgnoreCase("test")) {
                if ("Hello, World!".equals(response)) {
                    System.out.println("[HelloClient] Test PASS!");
                    System.exit(0);
                } else {
                    System.err.println("[HelloClient] Test FAIL: Sai ket qua tra ve!");
                    System.exit(1);
                }
            }
        } catch (Exception e) {
            System.err.println("[HelloClient] Loi ket noi: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}
