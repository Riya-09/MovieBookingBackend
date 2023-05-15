package main.java.com.booking.exceptions;

public class RequestedSeatsAlreadyBookedException extends Exception {
    public RequestedSeatsAlreadyBookedException(String message) {
            super(message);
        }
}
