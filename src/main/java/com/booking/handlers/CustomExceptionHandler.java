package main.java.com.booking.handlers;

import com.booking.exceptions.*;
import com.booking.handlers.models.ErrorResponse;
import com.fasterxml.jackson.databind.exc.InvalidDefinitionException;
import org.jetbrains.annotations.NotNull;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.singletonList;

@RestControllerAdvice
public class CustomExceptionHandler extends ResponseEntityExceptionHandler {

    private final ArrayList<String> emptyDetails = new ArrayList<>();

    @NotNull
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, @NotNull HttpHeaders headers, @NotNull HttpStatus status, @NotNull WebRequest request) {
        List<String> details = new ArrayList<>();
        for (ObjectError error : ex.getBindingResult().getAllErrors()) {
            if (error instanceof FieldError)
                details.add((((FieldError) error).getField() + " " + error.getDefaultMessage()));
            else details.add(error.getDefaultMessage());
        }
        ErrorResponse error = new ErrorResponse("Validation Failed", details);
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = {IncorrectCurrentPasswordException.class, SamePasswordException.class})
    public ResponseEntity<ErrorResponse> handleIncorrectCurrentPasswordException(Exception exception) {
        ErrorResponse error = new ErrorResponse("Password Validation Failed", singletonList(exception.getMessage()));
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidDefinitionException.class)
    public final ResponseEntity<ErrorResponse> handleException(InvalidDefinitionException ex) {
        if (ex.getCause() instanceof EnumValidationException)
            return handleEnumValidationException((EnumValidationException) ex.getCause());

        ErrorResponse errorResponse = new ErrorResponse("Something has gone wrong", singletonList(ex.getMessage()));
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = {NextSevenDayException.class,ShowAlreadyStartedException.class,PreviousDateException.class})
    public final ResponseEntity<ErrorResponse> handleException(Exception ex) {

        ErrorResponse errorResponse = new ErrorResponse("Validation Failed", singletonList(ex.getMessage()));
        return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(EnumValidationException.class)
    public ResponseEntity<ErrorResponse> handleEnumValidationException(EnumValidationException ex) {
        ErrorResponse error = new ErrorResponse("Validation Failed", singletonList(ex.getMessage()));
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(EmptyResultDataAccessException.class)
    public ResponseEntity<ErrorResponse> handleEmptyResultException(EmptyResultDataAccessException e) {
        ErrorResponse error = new ErrorResponse("Record not found", new ArrayList<>() {{
            add(e.getMessage());
        }});
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAnyException() {
        ErrorResponse error = new ErrorResponse("Something went wrong", emptyDetails);
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(value = {UserAlreadyRegisteredException.class})
    public ResponseEntity<ErrorResponse> handleUserAlreadyRegisteredException(Exception exception) {
        ErrorResponse error = new ErrorResponse("User email is already registered.", singletonList(exception.getMessage()));
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = {PasswordMisMatchException.class})
    public ResponseEntity<ErrorResponse> handleMismatchPasswordException(Exception exception) {
        ErrorResponse error = new ErrorResponse("Password mismatched", singletonList(exception.getMessage()));
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = {RequestedSeatsAlreadyBookedException.class})
    public ResponseEntity<ErrorResponse> handleRequestedSeatsAlreadyBookedException(Exception exception) {
        ErrorResponse error = new ErrorResponse("One or more of the seats you have selected are already booked, kindly select again", singletonList(exception.getMessage()));
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = {NoOfSeatsAndSelectedNumberOfSeatsMismatchException.class})
    public ResponseEntity<ErrorResponse> NoOfSeatsAndSelectedNumberOfSeatsMismatchException(Exception exception) {
        ErrorResponse error = new ErrorResponse("Number of Seats and the number of seats requested to be booked are not equal", singletonList(exception.getMessage()));
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }


}
