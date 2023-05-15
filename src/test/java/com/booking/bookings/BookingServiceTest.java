package test.java.com.booking.bookings;

import com.booking.bookings.repository.Booking;
import com.booking.bookings.repository.BookingRepository;
import com.booking.customers.repository.Customer;
import com.booking.customers.repository.CustomerRepository;
import com.booking.exceptions.NoOfSeatsAndSelectedNumberOfSeatsMismatchException;
import com.booking.exceptions.NoSeatAvailableException;
import com.booking.exceptions.RequestedSeatsAlreadyBookedException;
import com.booking.exceptions.ShowAlreadyStartedException;
import com.booking.registration.Roles;
import com.booking.shows.respository.Show;
import com.booking.shows.respository.ShowRepository;
import com.booking.slots.repository.Slot;
import com.booking.slots.repository.SlotRepository;
import com.booking.users.User;
import com.booking.users.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Optional;

import static com.booking.shows.respository.Constants.TOTAL_NO_OF_SEATS;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class BookingServiceTest {
    public static final Long TEST_SHOW_ID = 1L;
    private BookingRepository bookingRepository;
    private BookingService bookingService;
    private Date bookingDate;
    private Show show;
    private Customer customer;
    private CustomerRepository customerRepository;
    private ShowRepository showRepository;
    private SlotRepository slotRepository;
    private UserRepository userRepository;
    private Slot slot;
    private ArrayList<String> seat;

    @BeforeEach
    public void beforeEach() throws NoSeatAvailableException {
        bookingRepository = mock(BookingRepository.class);
        customerRepository = mock(CustomerRepository.class);
        showRepository = mock(ShowRepository.class);
        slotRepository = mock(SlotRepository.class);
        userRepository = mock(UserRepository.class);
        userRepository.deleteAll();
        bookingDate = Date.valueOf("2020-06-01");
        slot = new Slot("13:00-16:00", Time.valueOf("13:00:00"), Time.valueOf("16:00:00"));
        show = new Show(bookingDate, slot, BigDecimal.valueOf(250), "1");
        customer = new Customer("Customer name", "9090909090");
        bookingService = new BookingService(bookingRepository, customerRepository, showRepository, slotRepository);
        seat = new ArrayList<>();
        seat.add("A1");
        seat.add("A2");
    }

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
    }

    @Test
    public void should_save_booking() throws NoSeatAvailableException, NoOfSeatsAndSelectedNumberOfSeatsMismatchException, RequestedSeatsAlreadyBookedException, ShowAlreadyStartedException {
        createNewUser(Roles.ADMIN);
        int noOfSeats = 2;
        Date bookingDate = Date.valueOf(LocalDate.now());
        Show show = new Show(bookingDate, slot, BigDecimal.valueOf(250), "1");
        Booking booking = new Booking(bookingDate, show, customer, noOfSeats, BigDecimal.valueOf(500));
        Booking mockBooking = mock(Booking.class);

        when(showRepository.findByIdAndDate(TEST_SHOW_ID, bookingDate)).thenReturn(show);
        when(slotRepository.findById(show.getSlot().getId())).thenReturn(Optional.ofNullable(slot));
        when(bookingRepository.save(booking)).thenReturn(mockBooking);
        Booking actualBooking = bookingService.book(customer, TEST_SHOW_ID, bookingDate, noOfSeats, Roles.ADMIN, seat);

        verify(bookingRepository).save(booking);
        assertThat(actualBooking, is(equalTo(mockBooking)));
    }

    @Test
    public void should_save_customer_who_requests_booking() throws NoSeatAvailableException, NoOfSeatsAndSelectedNumberOfSeatsMismatchException, RequestedSeatsAlreadyBookedException, ShowAlreadyStartedException {
        createNewUser(Roles.ADMIN);
        Date bookingDate = Date.valueOf(LocalDate.now());
        Show show = new Show(bookingDate, slot, BigDecimal.valueOf(250), "1");

        when(showRepository.findByIdAndDate(TEST_SHOW_ID, bookingDate)).thenReturn(show);
        when(slotRepository.findById(show.getSlot().getId())).thenReturn(Optional.of(slot));
        bookingService.book(customer, 1L, bookingDate, 2, Roles.ADMIN, seat);

        verify(customerRepository).save(customer);
    }

    @Test
    public void should_not_book_seat_when_seats_are_not_available() {
        createNewUser(Roles.ADMIN);
        Date bookingDate = Date.valueOf(LocalDate.now());
        Slot slot = new Slot("13:00-16:00", Time.valueOf("13:00:00"), Time.valueOf("16:00:00"));
        Show show = new Show(bookingDate, slot, BigDecimal.valueOf(250), "1");
        when(bookingRepository.bookedSeatsByShow(show.getId())).thenReturn(TOTAL_NO_OF_SEATS);
        when(showRepository.findByIdAndDate(TEST_SHOW_ID, bookingDate)).thenReturn(show);
        when(slotRepository.findById(show.getSlot().getId())).thenReturn(Optional.ofNullable(slot));

        assertThrows(NoSeatAvailableException.class, () -> bookingService.book(customer, TEST_SHOW_ID, bookingDate, 102, Roles.ADMIN, null));
        verifyZeroInteractions(customerRepository);
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    public void should_not_book_seat_when_show_is_not_found() {
        createNewUser(Roles.ADMIN);
        when(showRepository.findByIdAndDate(TEST_SHOW_ID, bookingDate)).thenReturn(null);
        when(slotRepository.findById(show.getSlot().getId())).thenReturn(Optional.ofNullable(slot));

        final var emptyResultDataAccessException =
                assertThrows(EmptyResultDataAccessException.class,
                        () -> bookingService.book(customer, TEST_SHOW_ID, bookingDate, 2, Roles.ADMIN, seat));

        assertThat(emptyResultDataAccessException.getMessage(), is(equalTo("Show not found")));
        assertThat(emptyResultDataAccessException.getExpectedSize(), is(equalTo(1)));

        verifyZeroInteractions(customerRepository);
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Transactional
    private User createNewUser(Roles role) {
        return userRepository.save(new User("test-user", "password", "test-user@gmail.com", 1234567890L, role));
    }

    @Test
    public void Should_ThrowException_If_NumberOfSeats_NotEqualTo_SizeOfBookCases() throws NoOfSeatsAndSelectedNumberOfSeatsMismatchException, NoSeatAvailableException, RequestedSeatsAlreadyBookedException {
        seat.add("A3");
        Show show = new Show(bookingDate, slot, BigDecimal.valueOf(250), "1");

        when(showRepository.findByIdAndDate(TEST_SHOW_ID, bookingDate)).thenReturn(show);

        final var numberOfSeatMismatch =
                assertThrows(NoOfSeatsAndSelectedNumberOfSeatsMismatchException.class,
                        () -> bookingService.book(customer, TEST_SHOW_ID, bookingDate, 2, Roles.CUSTOMER, seat));
        assertThat(numberOfSeatMismatch.getMessage(), is(equalTo("Number of seats and seats selected should be equal")));
    }

}
