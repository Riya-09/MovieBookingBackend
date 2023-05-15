package test.java.com.booking.bookings.shows.view;

import com.booking.App;
import com.booking.bookings.repository.BookingRepository;
import com.booking.movieGateway.MovieGateway;
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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Time;
import java.time.Duration;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = App.class)
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@WithMockUser
public class ShowControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ShowRepository showRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private SlotRepository slotRepository;

    @MockBean
    private MovieGateway movieGateway;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    public void before() {
        showRepository.deleteAll();
        slotRepository.deleteAll();
        userRepository.deleteAll();
    }

    @AfterEach
    public void after() {
        showRepository.deleteAll();
        slotRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    public void retrieveAllExistingShowsForAdmin() throws Exception {
        createNewUser(Roles.ADMIN);
        when(movieGateway.getMovieFromId("movie_1"))
                .thenReturn(
                        new Movie(
                                "movie_1",
                                "Movie name",
                                Duration.ofHours(1).plusMinutes(30),
                                "Movie plot",
                                "7.5", "genre", "poster", "rated")
                );
        final Slot slotOne = slotRepository.save(new Slot("Test slot one", Time.valueOf("09:30:00"), Time.valueOf("12:00:00")));
        final Slot slotTwo = slotRepository.save(new Slot("Test slot two", Time.valueOf("13:30:00"), Time.valueOf("16:00:00")));
        final Show showOne = showRepository.save(new Show(Date.valueOf("2020-01-01"), slotOne, new BigDecimal("249.99"), "movie_1"));
        final Show showTwo = showRepository.save(new Show(Date.valueOf("2020-01-01"), slotTwo, new BigDecimal("299.99"), "movie_1"));
        showRepository.save(new Show(Date.valueOf("2020-01-02"), slotOne, new BigDecimal("249.99"), "movie_1"));

        mockMvc.perform(get("/shows?date=2020-01-01")
                        .with(httpBasic("test-user@gmail.com", "password")))
                .andExpect(status().isOk())
                .andExpect(content().json(
                        "[" +
                                "{'id':" + showOne.getId() + ",'date':'2020-01-01','cost':249.99," +
                                "'slot':{'id':" + slotOne.getId() + ",'name':'Test slot one','startTime':'9:30 AM','endTime':'12:00 PM'}," +
                                "'movie':{'id':'movie_1','name':'Movie name','duration':'1h 30m','plot':'Movie plot'}}," +
                                "{'id':" + showTwo.getId() + ",'date':'2020-01-01','cost':299.99," +
                                "'slot':{'id':" + slotTwo.getId() + ",'name':'Test slot two','startTime':'1:30 PM','endTime':'4:00 PM'}," +
                                "'movie':{'id':'movie_1','name':'Movie name','duration':'1h 30m','plot':'Movie plot'}}" +
                                "]"));
    }

    @Test
    public void shouldThrowPreviousDateExceptionIfCustomerTriesToAccessOldShows() throws Exception {
        createNewUser(Roles.CUSTOMER);

        mockMvc.perform(get("/shows?date=2020-01-01")
                        .with(httpBasic("test-user@gmail.com", "password")))
                .andExpect(status().isForbidden());
    }

    @Test
    public void shouldThrowNextSevenDayExceptionIfCustomerTriesToAccessFutureShows() throws Exception {
        createNewUser(Roles.CUSTOMER);

        mockMvc.perform(get("/shows?date=2022-01-01")
                        .with(httpBasic("test-user@gmail.com", "password")))
                .andExpect(status().isForbidden());
    }

    @Transactional
    private User createNewUser(Roles role) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        return userRepository.save(new User("test-user", passwordEncoder.encode("password"), "test-user@gmail.com", 1234567890L, role));
    }
}
