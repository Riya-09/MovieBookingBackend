package main.java.com.booking.shows.view;

import com.booking.bookings.BookingService;
import com.booking.exceptions.NextSevenDayException;
import com.booking.exceptions.PreviousDateException;
import com.booking.handlers.models.ErrorResponse;
import com.booking.movieGateway.exceptions.FormatException;
import com.booking.movieGateway.models.Movie;
import com.booking.registration.Roles;
import com.booking.shows.ShowService;
import com.booking.shows.respository.Show;
import com.booking.shows.view.models.ShowResponse;
import com.booking.users.UserPrincipal;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.io.IOException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

@Api(tags = "Shows")
@RestController
public class ShowController {
    private final ShowService showService;
    private final BookingService bookingService;

    @Autowired
    public ShowController(ShowService showService, BookingService bookingService) {
        this.showService = showService;
        this.bookingService = bookingService;
    }


    @GetMapping("/shows")
    @ApiOperation(value = "Fetch shows")
    @ResponseStatus(code = HttpStatus.OK)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Fetched shows successfully"),
            @ApiResponse(code = 500, message = "Something failed in the server", response = ErrorResponse.class)
    })
    public List<ShowResponse> fetchAll(@Valid @RequestParam(name = "date") Date date) throws IOException, FormatException, PreviousDateException, NextSevenDayException {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        List<ShowResponse> showResponse = new ArrayList<>();
        if (securityContext != null) {
            try {
                UserPrincipal userPrincipal = (UserPrincipal) securityContext.getAuthentication().getPrincipal();
                Roles role = userPrincipal.getUser().getRole();

                for (Show show : showService.fetchAll(date, role)) {
                    Integer noOfBookedSeats = bookingService.NoOfBookedSeats(date, show);
                    showResponse.add(constructShowResponse(show, noOfBookedSeats, date));
                }
            } catch (Exception e) {
                for (Show show : showService.fetchAll(date, null)) {
                    Integer noOfBookedSeats = bookingService.NoOfBookedSeats(date, show);
                    showResponse.add(constructShowResponse(show, noOfBookedSeats, date));
                }
            }

        }
        return showResponse;
    }

    @GetMapping("/show/map")
    @ApiOperation(value = "Fetch BookedSeats")
    @ResponseStatus(code = HttpStatus.OK)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Fetched bookedSeats successfully"),
            @ApiResponse(code = 500, message = "Something failed in the server", response = ErrorResponse.class)
    })
    public List<String> fetchAllBookedSeats(@Valid @RequestParam(name = "date") Date date, @Valid @RequestParam(name = "showId") Long showId) {
        return bookingService.getListOfBookedSeats(showId, date);
    }


    private ShowResponse constructShowResponse(Show show, Integer noOfBookedSeats, @Valid Date date) throws IOException, FormatException {
        Movie movie = showService.getMovieById(show.getMovieId());
        return new ShowResponse(movie, show.getSlot(), show, noOfBookedSeats);
    }
}
