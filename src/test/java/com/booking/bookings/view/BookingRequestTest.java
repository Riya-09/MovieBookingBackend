package test.java.com.booking.bookings.view;

import com.booking.customers.repository.Customer;
import org.junit.jupiter.api.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;

import static com.booking.shows.respository.Constants.MAX_NO_OF_SEATS_PER_BOOKING;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class BookingRequestTest {
    @Test
    public void should_not_allow_seat_booking_for_more_than_maximum() {
        final var showId = 1L;
        final Customer customer = new Customer("customer 1", "992212399");
        int greaterThanMaxSeats = Integer.parseInt(MAX_NO_OF_SEATS_PER_BOOKING) + 1;
        String[] seats={"A1","A2","A3","A4","A2","A3","A5","A6","A7","A8","A8","A10","b1","b2","b3","b4","b5","b6","b7","b8","b9"};
        ArrayList<String> seatsToBeBooked=new ArrayList<String>();
        for(String seat: seats){
            seatsToBeBooked.add(seat);
        }
        seatsToBeBooked.addAll(Arrays.asList(seats));
        final BookingRequest bookingRequest = new BookingRequest(Date.valueOf("2020-11-06"), showId, customer, greaterThanMaxSeats,seatsToBeBooked);
        final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        final Set<ConstraintViolation<BookingRequest>> violations = validator.validate(bookingRequest);
        assertThat(violations.iterator().next().getMessage(), is("Maximum " + MAX_NO_OF_SEATS_PER_BOOKING + " seats allowed per booking"));
    }
}
