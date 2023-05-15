package main.java.com.booking.exceptions;

public class SamePasswordException extends Exception {
    public SamePasswordException(String message) {
        super(message);
    }
}
