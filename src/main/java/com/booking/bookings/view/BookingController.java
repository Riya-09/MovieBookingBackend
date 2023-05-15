package main.java.com.booking.bookings.view;

import com.booking.bookings.BookingService;
import com.booking.bookings.repository.Booking;
import com.booking.exceptions.NoOfSeatsAndSelectedNumberOfSeatsMismatchException;
import com.booking.exceptions.NoSeatAvailableException;
import com.booking.exceptions.RequestedSeatsAlreadyBookedException;
import com.booking.exceptions.ShowAlreadyStartedException;
import com.booking.handlers.models.ErrorResponse;
import com.booking.registration.Roles;
import com.booking.users.UserPrincipal;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;


@Api(tags = "Bookings")
@RestController
@RequestMapping("/bookings")
public class BookingController {
    private final BookingService bookingService;

    @Autowired
    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "Create a booking")
    @ResponseStatus(code = HttpStatus.CREATED)
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Created a booking successfully"),
            @ApiResponse(code = 404, message = "Record not found", response = ErrorResponse.class),
            @ApiResponse(code = 400, message = "Server cannot process request due to client error", response = ErrorResponse.class),
            @ApiResponse(code = 500, message = "Something failed in the server", response = ErrorResponse.class)
    })
    public BookingConfirmationResponse book(@Valid @RequestBody BookingRequest bookingRequest) throws NoSeatAvailableException, ShowAlreadyStartedException, RequestedSeatsAlreadyBookedException, NoOfSeatsAndSelectedNumberOfSeatsMismatchException {
        UserPrincipal userPrincipal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Roles role = userPrincipal.getUser().getRole();
        Booking booking = bookingService.book(bookingRequest.getCustomer(), bookingRequest.getShowId(), bookingRequest.getDate(), bookingRequest.getNoOfSeats(), role, bookingRequest.getBookcases());
        return booking.constructBookingConfirmation();
    }

}
