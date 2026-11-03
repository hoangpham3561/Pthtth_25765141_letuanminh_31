package bai8_vemaybay;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class FlightBookingServiceImpl extends UnicastRemoteObject implements FlightBookingService {
    private final Map<String, Flight> flightMap;

    public FlightBookingServiceImpl() throws RemoteException {
        super();
        this.flightMap = new ConcurrentHashMap<>();
        flightMap.put("VN123", new Flight("VN123", "TP. Hồ Chí Minh", "Hà Nội", 100));
        flightMap.put("VJ456", new Flight("VJ456", "TP. Hồ Chí Minh", "Đà Nẵng", 150));
        flightMap.put("QH789", new Flight("QH789", "Hà Nội", "Phú Quốc", 80));
    }

    @Override
    public synchronized boolean bookTicket(String flightNumber, int seats) throws RemoteException {
        if (flightNumber == null) return false;
        Flight flight = flightMap.get(flightNumber.toUpperCase());
        if (flight == null) return false;
        boolean ok = flight.book(seats);
        System.out.printf("[FlightServer] Dat %d ve chuyen %s: %s (Con lai: %d)\n",
                seats, flightNumber, ok ? "Thanh cong" : "That bai", flight.getAvailableSeats());
        return ok;
    }

    @Override
    public synchronized int availableSeats(String flightNumber) throws RemoteException {
        if (flightNumber == null) return -1;
        Flight flight = flightMap.get(flightNumber.toUpperCase());
        if (flight == null) return -1;
        return flight.getAvailableSeats();
    }

    @Override
    public synchronized boolean cancelBooking(String flightNumber, int seats) throws RemoteException {
        if (flightNumber == null) return false;
        Flight flight = flightMap.get(flightNumber.toUpperCase());
        if (flight == null) return false;
        boolean ok = flight.cancel(seats);
        System.out.printf("[FlightServer] Huy %d ve chuyen %s: %s (Con lai: %d)\n",
                seats, flightNumber, ok ? "Thanh cong" : "That bai", flight.getAvailableSeats());
        return ok;
    }

    @Override
    public synchronized List<Flight> getAllFlights() throws RemoteException {
        return new ArrayList<>(flightMap.values());
    }
}
