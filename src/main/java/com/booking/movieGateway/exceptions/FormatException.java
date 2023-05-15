package main.java.com.booking.movieGateway.exceptions;

public class FormatException extends Exception {

    public FormatException(String argumentName) {
        super(argumentName + " has an illegal format");
    }
}
