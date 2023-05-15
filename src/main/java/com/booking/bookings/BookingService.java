package main.java.com.booking.bookings;

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
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.*;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.stream.Collectors;

import static com.booking.shows.respository.Constants.TOTAL_NO_OF_SEATS;

@Service
public class BookingService {
    private final BookingRepository bookingRepository;
    private final CustomerRepository customerRepository;
    private final ShowRepository showRepository;
    private final SlotRepository slotRepository;

    public BookingService(BookingRepository bookingRepository, CustomerRepository customerRepository, ShowRepository showRepository, SlotRepository slotRepository) throws NoSeatAvailableException {
        this.bookingRepository = bookingRepository;
        this.customerRepository = customerRepository;
        this.showRepository = showRepository;
        this.slotRepository = slotRepository;
    }


    public Booking book(Customer customer, Long showId, Date bookingDate, int noOfSeats, Roles role, ArrayList<String> bookcases) throws NoOfSeatsAndSelectedNumberOfSeatsMismatchException, ShowAlreadyStartedException, NoSeatAvailableException,
            RequestedSeatsAlreadyBookedException {
        final var show = showRepository.findByIdAndDate(showId, bookingDate);

        if (show == null) {
            throw new EmptyResultDataAccessException("Show not found", 1);
        }

        if (availableSeats(show, bookingDate) < noOfSeats) {
            throw new NoSeatAvailableException("No seats available");
        }

        if (!validBookCases(noOfSeats, bookcases))
            throw new NoOfSeatsAndSelectedNumberOfSeatsMismatchException("Number of seats and seats selected should be equal");

        final var slot = slotRepository.findById(show.getSlot().getId())
                .orElseThrow(() -> new EmptyResultDataAccessException("Slot not found", 1));

        LocalTime currentTime = LocalTime.now(ZoneId.of("Asia/Kolkata"));
        Time startTime = slot.getStartTime();

        LocalDate currentDate = LocalDate.now(ZoneId.of("Asia/Kolkata"));
        Date showDate = show.getDate();

        if (role == Roles.CUSTOMER && ((showDate.compareTo(Date.valueOf(currentDate)) == 0 &&
                startTime.compareTo(Time.valueOf(currentTime)) < 0) || showDate.compareTo(Date.valueOf(currentDate)) < 0)) {
            throw new ShowAlreadyStartedException("Show already started. You can't book the ticket.");
        }

        HashSet<String> seatAlreadyBookedSet = new HashSet<>(getListOfBookedSeats(showId, bookingDate));
        for (String bookS : bookcases) {
            if (seatAlreadyBookedSet.contains(bookS))
                throw new RequestedSeatsAlreadyBookedException("One or more of the seats you have selected are already booked, kindly select again");
        }

        customerRepository.save(customer);
        BigDecimal amountPaid = show.costFor(noOfSeats);
        String seatsBooked = bookcases.stream().collect(
                Collectors.joining(","));

        return bookingRepository.save(new Booking(bookingDate, show, customer, noOfSeats, amountPaid, seatsBooked));
    }

    private boolean validBookCases(int noOfSeats, ArrayList<String> bookcases) {
        if (noOfSeats == bookcases.size())
            return true;
        return false;
    }

    private long availableSeats(Show show, Date date) {
        Integer seatsBooked = bookingRepository.getNoOfBookedSeat(show.getId(), date);
        if (null == seatsBooked)
            return TOTAL_NO_OF_SEATS;

        return TOTAL_NO_OF_SEATS - seatsBooked;
    }

    public Integer NoOfBookedSeats(Date date, Show show) {
        Integer noOfBookedSeats = bookingRepository.getNoOfBookedSeat(show.getId(), date);
        if (null == noOfBookedSeats)
            return 0;

        return noOfBookedSeats;
    }

    public ArrayList<String> getListOfBookedSeats(Long slotId, Date date) {
        List<Booking> listOfBookedSeats = bookingRepository.findByShowIdAndDate(slotId, date);
        ArrayList<String> bookedSeats = new ArrayList<>();
        for (Booking booking : listOfBookedSeats) {
            bookedSeats.addAll(Arrays.asList(booking.getBookcases().split(",")));
        }
        System.out.println(bookedSeats);
        return bookedSeats;
    }
}
