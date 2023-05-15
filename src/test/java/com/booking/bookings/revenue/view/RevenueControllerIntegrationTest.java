package test.java.com.booking.bookings.revenue.view;

import com.booking.App;
import com.booking.bookings.repository.Booking;
import com.booking.bookings.repository.BookingRepository;
import com.booking.customers.repository.Customer;
import com.booking.customers.repository.CustomerRepository;
import com.booking.shows.respository.Show;
import com.booking.shows.respository.ShowRepository;
import com.booking.slots.repository.Slot;
import com.booking.slots.repository.SlotRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = App.class)
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@WithMockUser
public class RevenueControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ShowRepository showRepository;

    @Autowired
    private SlotRepository slotRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @BeforeEach
    public void before() {
        bookingRepository.deleteAll();
        showRepository.deleteAll();
        slotRepository.deleteAll();
        customerRepository.deleteAll();
    }

    @AfterEach
    public void after() {
        bookingRepository.deleteAll();
        showRepository.deleteAll();
        slotRepository.deleteAll();
        customerRepository.deleteAll();
    }

    @Test
    public void should_get_revenue_correctly() throws Exception {
        final var slotOne =
                slotRepository.save(new Slot("Test slot one", Time.valueOf("09:30:00"), Time.valueOf("12:00:00")));
        final var slotTwo =
                slotRepository.save(new Slot("Test slot two", Time.valueOf("13:30:00"), Time.valueOf("16:00:00")));

        final Show showOne =
                showRepository.save(new Show(Date.valueOf("2020-01-01"), slotOne, BigDecimal.valueOf(200), "movie_1"));
        final Show showTwo =
                showRepository.save(new Show(Date.valueOf("2020-01-01"), slotTwo, BigDecimal.valueOf(150), "movie_1"));
        final Show showThree =
                showRepository.save(new Show(Date.valueOf("2020-01-02"), slotTwo, BigDecimal.valueOf(250), "movie_1"));
        final var customer = customerRepository.save(new Customer("Name", "9999999999"));

        final String seatOne="A1,A2";
        bookingRepository.save(
                new Booking(Date.valueOf(LocalDate.now()), showOne, customer, 2, BigDecimal.valueOf(400),seatOne)
        );
        final String seatTwo="B1,B2,B3";
        bookingRepository.save(
                new Booking(Date.valueOf(LocalDate.now()), showTwo, customer, 3, BigDecimal.valueOf(450),seatTwo)
        );
        final String seatThree="C3";
        bookingRepository.save(
                new Booking(Date.valueOf(LocalDate.now()), showThree, customer, 1, BigDecimal.valueOf(250),seatThree)
        );

        mockMvc.perform(get("/revenue?date=2020-01-01"))
                .andExpect(status().isOk())
                .andExpect(content().string("850.00"));
    }

    @Test
    public void should_get_zero_revenue_for_no_bookings() throws Exception {
        final var slotOne =
                slotRepository.save(new Slot("Test slot one", Time.valueOf("09:30:00"), Time.valueOf("12:00:00")));
        final var slotTwo =
                slotRepository.save(new Slot("Test slot two", Time.valueOf("13:30:00"), Time.valueOf("16:00:00")));

        showRepository.save(new Show(Date.valueOf("2020-01-01"), slotOne, BigDecimal.valueOf(200), "movie_1"));
        showRepository.save(new Show(Date.valueOf("2020-01-01"), slotTwo, BigDecimal.valueOf(150), "movie_1"));
        final Show showThree =
                showRepository.save(new Show(Date.valueOf("2020-01-02"), slotTwo, BigDecimal.valueOf(250), "movie_1"));
        final var customer = customerRepository.save(new Customer("Name", "9999999999"));


        bookingRepository.save(
                new Booking(Date.valueOf("2019-12-31"), showThree, customer, 1, BigDecimal.valueOf(250), "A1,A2"));


        mockMvc.perform(get("/revenue?date=2020-01-01"))
                .andExpect(status().isOk())
                .andExpect(content().string("0"));
    }
}
