package bai7_dientich;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class GeometryServiceImpl extends UnicastRemoteObject implements GeometryService {
    public GeometryServiceImpl() throws RemoteException {
        super();
    }

    @Override
    public double rectangleArea(double width, double height) throws RemoteException {
        if (width <= 0 || height <= 0) {
            throw new IllegalArgumentException("Chieu rong va chieu dai phai lon hon 0!");
        }
        return width * height;
    }

    @Override
    public double circleArea(double radius) throws RemoteException {
        if (radius <= 0) {
            throw new IllegalArgumentException("Ban kinh phai lon hon 0!");
        }
        return Math.PI * radius * radius;
    }

    @Override
    public double triangleArea(double base, double height) throws RemoteException {
        if (base <= 0 || height <= 0) {
            throw new IllegalArgumentException("Day va chieu cao phai lon hon 0!");
        }
        return 0.5 * base * height;
    }
}
