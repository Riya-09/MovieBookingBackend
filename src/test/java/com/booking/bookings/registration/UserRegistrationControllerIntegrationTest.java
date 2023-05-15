package test.java.com.booking.bookings.registration;

import com.booking.App;
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
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = App.class)
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@WithMockUser
public class UserRegistrationControllerIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;


    @BeforeEach
    public void before() {
        userRepository.deleteAll();
    }

    @AfterEach
    public void after() {
        userRepository.deleteAll();
    }


    @Test
    public void should_save_user_detail() throws Exception {
        final String requestJson = "{\n" +
                "\t\"name\":\"dolly\",\n" +
                "\t\"email\":\"dolly@gmail.com\",\n" +
                "\t\"mobileNumber\":\"2934567890\",\n" +
                "\t\"password\":\"Dolly@1234\",\n" +
                "\t\"reenterPassword\":\"Dolly@1234\"\n" +
                "}";

        mockMvc.perform(post("/register")
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(requestJson))
                .andExpect(status().isCreated())
                .andExpect(content().json("{\n" +
                        "    \"id\": 3,\n" +
                        "    \"username\": \"dolly\",\n" +
                        "    \"email\": \"dolly@gmail.com\",\n" +
                        "    \"mobileNumber\": 2934567890,\n" +
                        "    \"role\": \"CUSTOMER\"\n" +
                        "}"));

        assertThat(userRepository.findAll().size(), is(1));
    }

    @Test
    public void should_not_save_user_detail_when_already_registered() throws Exception {
        User user = new User("dolly","Dolly@1234","dolly@gmail.com",2934567890l,Roles.CUSTOMER);
        userRepository.save(user);

        final String requestJson = "{\n" +
                "\t\"name\":\"dolly\",\n" +
                "\t\"email\":\"dolly@gmail.com\",\n" +
                "\t\"mobileNumber\":\"2934567890\",\n" +
                "\t\"password\":\"Dolly@1234\",\n" +
                "\t\"reenterPassword\":\"Dolly@1234\"\n" +
                "}";

        mockMvc.perform(post("/register")
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(requestJson))
                .andExpect(status().isBadRequest())
                .andReturn();
    }

    @Test
    public void should_not_save_user_detail_when_password_mismatch() throws Exception {
        final String requestJson = "{\n" +
                "\t\"name\":\"dolly\",\n" +
                "\t\"email\":\"dolly@gmail.com\",\n" +
                "\t\"mobileNumber\":\"2934567890\",\n" +
                "\t\"password\":\"Dolly@1234\",\n" +
                "\t\"reenterPassword\":\"Dolly@12345\"\n" +
                "}";

        mockMvc.perform(post("/register")
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(requestJson))
                .andExpect(status().isBadRequest())
                .andReturn();
    }
}