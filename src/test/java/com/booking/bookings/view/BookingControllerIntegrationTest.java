package test.java.com.booking.bookings.view;

import com.booking.App;
import com.booking.bookings.repository.BookingRepository;
import com.booking.customers.repository.CustomerRepository;
import com.booking.movieGateway.MovieGateway;
import com.booking.movieGateway.exceptions.FormatException;
import com.booking.movieGateway.models.Movie;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Time;
import java.time.Duration;
import java.time.LocalDate;
import java.time.ZoneId;

import static com.booking.shows.respository.Constants.MAX_NO_OF_SEATS_PER_BOOKING;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = App.class)
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@WithMockUser
public class BookingControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private ShowRepository showRepository;

    @Autowired
    private SlotRepository slotRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private UserRepository userRepository;

    @MockBean
    private MovieGateway movieGateway;
    private Show showOne;
    private Show showTwo;

    @BeforeEach
    public void beforeEach() throws IOException, FormatException {
        userRepository.deleteAll();
        bookingRepository.deleteAll();
        showRepository.deleteAll();
        slotRepository.deleteAll();
        customerRepository.deleteAll();

        when(movieGateway.getMovieFromId("movie_1"))
                .thenReturn(
                        new Movie(
                                "movie_1",
                                "Movie name",
                                Duration.ofHours(1).plusMinutes(30),
                                "Movie description",
                                "7.5", "genre", "poster", "rated")
                );
        Slot slotOne = slotRepository.save(new Slot("Test slot", Time.valueOf("09:30:00"), Time.valueOf("12:00:00")));
        showOne = showRepository.save(new Show(Date.valueOf(LocalDate.now(ZoneId.of("Asia/Kolkata"))), slotOne, new BigDecimal("249.99"), "movie_1"));
        showTwo = showRepository.save(new Show(Date.valueOf(LocalDate.now(ZoneId.of("Asia/Kolkata")).minusDays(1)), slotOne, new BigDecimal("249.99"), "movie_1"));
    }

    @AfterEach
    public void afterEach() {
        userRepository.deleteAll();
        bookingRepository.deleteAll();
        showRepository.deleteAll();
        slotRepository.deleteAll();
        customerRepository.deleteAll();

    }

    @Test
    public void should_save_booking_and_customer_detail() throws Exception {
        createNewUser(Roles.ADMIN);
        Date date = Date.valueOf(LocalDate.now(ZoneId.of("Asia/Kolkata")));
        final String requestJson = "{" +
                "\"date\": \"" + date + "\"," +
                "\"showId\": " + showOne.getId() + "," +
                "\"customer\": " + "{\"name\": \"Customer 1\", \"phoneNumber\": \"9922334455\"}," +
                "\"noOfSeats\": 5," +
                "\"bookCases\": [\n" +
                "        \"J1\",\n" +
                "        \"J2\",\n" +
                "        \"J3\",\n" +
                "        \"J4\",\n" +
                "        \"J10\"\n" +
                "    ]" +
                "}";


        mockMvc.perform(post("/bookings")
                        .with(httpBasic("test-user@gmail.com", "password"))
                        .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                        .content(requestJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(content().json("{" +
                        "\"customerName\":\"Customer 1\"," +
                        "\"showDate\": \"" + date + "\"," +
                        "\"startTime\":\"09:30:00\"," +
                        "\"amountPaid\":1249.95," +
                        "\"noOfSeats\":5," +
                        "\"bookSeats\": [\n" +
                        "        \"J1\",\n" +
                        "        \"J2\",\n" +
                        "        \"J3\",\n" +
                        "        \"J4\",\n" +
                        "        \"J10\"\n" +
                        "    ]" +
                        "}"
                ));

        assertThat(customerRepository.findAll().size(), is(1));
        assertThat(bookingRepository.findAll().size(), is(1));
    }

    @Test
    public void should_not_book_when_seats_booking_is_greater_than_allowed() throws Exception {
        createNewUser(Roles.CUSTOMER);
        String[] bookcases = new String[]{"A1", "A2", "A3", "A4", "A5", "A6", "A7", "A8", "A9", "A10", "B1", "B2", "B3", "B4", "B5", "B6"};
        final String moreThanAllowedSeatsRequestJson = "{" +
                "\"date\": \"2020-06-01\"," +
                "\"showId\": " + showOne.getId() + "," +
                "\"customer\": " + "{\"name\": \"Customer 1\", \"phoneNumber\": \"9922334455\"}," +
                "\"noOfSeats\": " + (Integer.parseInt(MAX_NO_OF_SEATS_PER_BOOKING) + 1) +
                "\"bookcases\": " + bookcases +
                "}";


        mockMvc.perform(post("/bookings")
                        .with(httpBasic("test-user@gmail.com", "password"))
                        .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                        .content(moreThanAllowedSeatsRequestJson))
                .andExpect(status().isBadRequest())
                .andReturn();
    }

    @Test
    public void should_not_book_when_max_capacity_for_seats_exceeds() throws Exception {
        createNewUser(Roles.ADMIN);
        Date date = Date.valueOf(LocalDate.now(ZoneId.of("Asia/Kolkata")));
        setupBookingSeatsForSameShow();

        final String overCapacityRequest = "{" +
                "\"date\": \"" + date + "\"," +
                "\"showId\": " + showOne.getId() + "," +
                "\"customer\": " + "{\"name\": \"Customer 1\", \"phoneNumber\": \"9922334455\"}," +
                "\"noOfSeats\": 11" + "," +
                "\"bookCases\": [\n" +
                "        \"J1\",\n" +
                "        \"J2\",\n" +
                "        \"J3\",\n" +
                "        \"J4\",\n" +
                "        \"J5\",\n" +
                "        \"J6\",\n" +
                "        \"J7\",\n" +
                "        \"J8\",\n" +
                "        \"J9\",\n" +
                "        \"J10\",\n" +
                "        \"J11\"\n" +
                "    ]" +
                "}";

        mockMvc.perform(post("/bookings")
                        .with(httpBasic("test-user@gmail.com", "password"))
                        .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                        .content(overCapacityRequest))
                .andExpect(status().is5xxServerError())
                .andReturn();

    }

    private void setupBookingSeatsForSameShow() throws Exception {
        Date date = Date.valueOf(LocalDate.now(ZoneId.of("Asia/Kolkata")));
        final String successRequest = "{" +
                "\"date\": \"" + date + "\"," +
                "\"showId\": " + showOne.getId() + "," +
                "\"customer\": " + "{\"name\": \"Customer 1\", \"phoneNumber\": \"9922334455\"}," +
                "\"noOfSeats\": " + MAX_NO_OF_SEATS_PER_BOOKING + "," +
                "\"bookCases\": [\n" +
                "        \"A1\",\n" +
                "        \"A2\",\n" +
                "        \"A3\",\n" +
                "        \"A4\",\n" +
                "        \"A5\",\n" +
                "        \"A6\",\n" +
                "        \"A7\",\n" +
                "        \"A8\",\n" +
                "        \"A9\",\n" +
                "        \"A10\",\n" +
                "        \"B1\",\n" +
                "        \"B2\",\n" +
                "        \"B3\",\n" +
                "        \"B4\",\n" +
                "        \"B5\"\n" +
                "    ]" +
                "}";

        mockMvc.perform(post("/bookings")
                        .with(httpBasic("test-user@gmail.com", "password"))
                        .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                        .content(successRequest))
                .andExpect(status().isCreated())
                .andReturn();
        final String successRequest2 = "{" +
                "\"date\": \"" + date + "\"," +
                "\"showId\": " + showOne.getId() + "," +
                "\"customer\": " + "{\"name\": \"Customer 1\", \"phoneNumber\": \"9922334455\"}," +
                "\"noOfSeats\": " + MAX_NO_OF_SEATS_PER_BOOKING + "," +
                " \"bookCases\": [\n" +
                "        \"B6\",\n" +
                "        \"B7\",\n" +
                "        \"B8\",\n" +
                "        \"B9\",\n" +
                "        \"B10\",\n" +
                "        \"C1\",\n" +
                "        \"C2\",\n" +
                "        \"C3\",\n" +
                "        \"C4\",\n" +
                "        \"C5\",\n" +
                "        \"C6\",\n" +
                "        \"C7\",\n" +
                "        \"C8\",\n" +
                "        \"C9\",\n" +
                "        \"C10\"\n" +
                "    ]" +
                "}";
        mockMvc.perform(post("/bookings")
                        .with(httpBasic("test-user@gmail.com", "password"))
                        .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                        .content(successRequest2))
                .andExpect(status().isCreated())
                .andReturn();

        final String successRequest4 = "{" +
                "\"date\": \"" + date + "\"," +
                "\"showId\": " + showOne.getId() + "," +
                "\"customer\": " + "{\"name\": \"Customer 1\", \"phoneNumber\": \"9922334455\"}," +
                "\"noOfSeats\": " + MAX_NO_OF_SEATS_PER_BOOKING + "," +
                "\"bookCases\": [\n" +
                "        \"D1\",\n" +
                "        \"D2\",\n" +
                "        \"D3\",\n" +
                "        \"D4\",\n" +
                "        \"D5\",\n" +
                "        \"D6\",\n" +
                "        \"D7\",\n" +
                "        \"D8\",\n" +
                "        \"D9\",\n" +
                "        \"D10\",\n" +
                "        \"E1\",\n" +
                "        \"E2\",\n" +
                "        \"E3\",\n" +
                "        \"E4\",\n" +
                "        \"E5\"\n" +
                "        \n" +
                "    ]" +
                "}";
        mockMvc.perform(post("/bookings")
                        .with(httpBasic("test-user@gmail.com", "password"))
                        .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                        .content(successRequest4))
                .andExpect(status().isCreated())
                .andReturn();

        final String successRequest5 = "{" +
                "\"date\": \"" + date + "\"," +
                "\"showId\": " + showOne.getId() + "," +
                "\"customer\": " + "{\"name\": \"Customer 1\", \"phoneNumber\": \"9922334455\"}," +
                "\"noOfSeats\": " + MAX_NO_OF_SEATS_PER_BOOKING + "," +
                "\"bookCases\": [\n" +
                "        \"E6\",\n" +
                "        \"E7\",\n" +
                "        \"E8\",\n" +
                "        \"E9\",\n" +
                "        \"E10\",\n" +
                "        \"F1\",\n" +
                "        \"F2\",\n" +
                "        \"F3\",\n" +
                "        \"F4\",\n" +
                "        \"F5\",\n" +
                "        \"F6\",\n" +
                "        \"F7\",\n" +
                "        \"F8\",\n" +
                "        \"F9\",\n" +
                "        \"F10\"\n" +
                "    ]" +
                "}";
        mockMvc.perform(post("/bookings")
                        .with(httpBasic("test-user@gmail.com", "password"))
                        .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                        .content(successRequest5))
                .andExpect(status().isCreated())
                .andReturn();

        final String successRequest6 = "{" +
                "\"date\": \"" + date + "\"," +
                "\"showId\": " + showOne.getId() + "," +
                "\"customer\": " + "{\"name\": \"Customer 1\", \"phoneNumber\": \"9922334455\"}," +
                "\"noOfSeats\": " + MAX_NO_OF_SEATS_PER_BOOKING + "," +
                "\"bookCases\": [\n" +
                "        \"G1\",\n" +
                "        \"G2\",\n" +
                "        \"G3\",\n" +
                "        \"G4\",\n" +
                "        \"G5\",\n" +
                "        \"G6\",\n" +
                "        \"G7\",\n" +
                "        \"G8\",\n" +
                "        \"G9\",\n" +
                "        \"G10\",\n" +
                "        \"H1\",\n" +
                "        \"H2\",\n" +
                "        \"H3\",\n" +
                "        \"H4\",\n" +
                "        \"H5\"\n" +
                "    ]" +

                "}";
        mockMvc.perform(post("/bookings")
                        .with(httpBasic("test-user@gmail.com", "password"))
                        .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                        .content(successRequest6))
                .andExpect(status().isCreated())
                .andReturn();

        final String successRequest3 = "{" +
                "\"date\": \"" + date + "\"," +
                "\"showId\": " + showOne.getId() + "," +
                "\"customer\": " + "{\"name\": \"Customer 1\", \"phoneNumber\": \"9922334455\"}," +
                "\"noOfSeats\": " + MAX_NO_OF_SEATS_PER_BOOKING + "," +
                " \"bookCases\": [\n" +
                "        \"H6\",\n" +
                "        \"H7\",\n" +
                "        \"H8\",\n" +
                "        \"H9\",\n" +
                "        \"H10\",\n" +
                "        \"I1\",\n" +
                "        \"I2\",\n" +
                "        \"I3\",\n" +
                "        \"I4\",\n" +
                "        \"I5\",\n" +
                "        \"I6\",\n" +
                "        \"I7\",\n" +
                "        \"I8\",\n" +
                "        \"I9\",\n" +
                "        \"I10\"\n" +
                "    ]" +
                "}";
        mockMvc.perform(post("/bookings")
                        .with(httpBasic("test-user@gmail.com", "password"))
                        .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                        .content(successRequest3))
                .andExpect(status().isCreated())
                .andReturn();

    }

    @Test
    void shouldThrowShowAlreadyStartedExceptionIfCustomerTriesToBookAfterStartTime() throws Exception {
        createNewUser(Roles.CUSTOMER);
        Date date = Date.valueOf(LocalDate.now(ZoneId.of("Asia/Kolkata")).minusDays(1));
        final String requestJson = "{" +
                "\"date\": \"" + date + "\"," +
                "\"showId\": " + showTwo.getId() + "," +
                "\"customer\": " + "{\"name\": \"Customer 1\", \"phoneNumber\": \"9922334455\"}," +
                "\"noOfSeats\": 5" + "," +
                "\"bookCases\": [\n" +
                "        \"J1\",\n" +
                "        \"J2\",\n" +
                "        \"J3\",\n" +
                "        \"J4\",\n" +
                "        \"J10\"\n" +
                "    ]" +
                "}";


        mockMvc.perform(post("/bookings")
                        .with(httpBasic("test-user@gmail.com", "password"))
                        .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                        .content(requestJson))
                .andExpect(status().isForbidden());
    }

    @Transactional
    private User createNewUser(Roles role) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        return userRepository.save(new User("test-user", passwordEncoder.encode("password"), "test-user@gmail.com", 1234567890L, role));
    }
}
