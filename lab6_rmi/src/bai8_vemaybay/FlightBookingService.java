package bai8_vemaybay;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface FlightBookingService extends Remote {
    boolean bookTicket(String flightNumber, int seats) throws RemoteException;
    int availableSeats(String flightNumber) throws RemoteException;
    boolean cancelBooking(String flightNumber, int seats) throws RemoteException;
    List<Flight> getAllFlights() throws RemoteException;
}
