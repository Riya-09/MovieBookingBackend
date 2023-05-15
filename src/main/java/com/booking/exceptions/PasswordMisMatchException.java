package main.java.com.booking.exceptions;

public class PasswordMisMatchException extends Exception {
    public PasswordMisMatchException(String message) {
        super(message);
    }
}