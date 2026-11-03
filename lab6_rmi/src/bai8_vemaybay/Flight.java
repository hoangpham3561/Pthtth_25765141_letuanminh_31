package bai8_vemaybay;

import java.io.Serializable;

public class Flight implements Serializable {
    private static final long serialVersionUID = 1L;

    private String flightNumber;
    private String origin;
    private String destination;
    private int totalSeats;
    private int bookedSeats;

    public Flight(String flightNumber, String origin, String destination, int totalSeats) {
        this.flightNumber = flightNumber;
        this.origin = origin;
        this.destination = destination;
        this.totalSeats = totalSeats;
        this.bookedSeats = 0;
    }

    public String getFlightNumber() {
        return flightNumber;
    }

    public String getOrigin() {
        return origin;
    }

    public String getDestination() {
        return destination;
    }

    public synchronized int getTotalSeats() {
        return totalSeats;
    }

    public synchronized int getBookedSeats() {
        return bookedSeats;
    }

    public synchronized int getAvailableSeats() {
        return totalSeats - bookedSeats;
    }

    public synchronized boolean book(int seats) {
        if (seats <= 0 || getAvailableSeats() < seats) {
            return false;
        }
        bookedSeats += seats;
        return true;
    }

    public synchronized boolean cancel(int seats) {
        if (seats <= 0 || bookedSeats < seats) {
            return false;
        }
        bookedSeats -= seats;
        return true;
    }

    @Override
    public String toString() {
        return String.format("[%s] %s -> %s | Tổng ghế: %d | Đã đặt: %d | Còn trống: %d",
                flightNumber, origin, destination, totalSeats, bookedSeats, getAvailableSeats());
    }
}
