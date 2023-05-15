package test.java.com.booking.bookings.users.view;

import com.booking.App;
import com.booking.registration.Roles;
import com.booking.users.User;
import com.booking.users.UserRepository;
import com.booking.users.profile.PasswordHistory;
import com.booking.users.profile.PasswordHistoryRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = App.class)
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
class UserControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordHistoryRepository passwordHistoryRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    public void before() {
        userRepository.deleteAll();
        passwordHistoryRepository.deleteAll();
    }

    @AfterEach
    public void after() {
        userRepository.deleteAll();
        passwordHistoryRepository.deleteAll();
    }


    @Test
    public void shouldLoginSuccessfully() throws Exception {
        createNewUser();
        mockMvc.perform(get("/login")
                .with(httpBasic("test-user@gmail.com", "password")))
                .andExpect(status().isOk());
    }

    @Test
    public void shouldThrowErrorMessageForInvalidCredentials() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldThrowErrorMessageIfCurrentPasswordIsIncorrect() throws Exception {
        createNewUser();
        final String requestJson = "{" +
                "\"currentPassword\": \"admin@123\"," +
                "\"newPassword\": \"pass@123\"" +
                "}";

        mockMvc.perform(put("/profile/user")
                .with(httpBasic("test-user@gmail.com", "password"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(status().isBadRequest());

    }

    @Test
    void shouldChangeThePasswordSuccessfully() throws Exception {
        createNewUser();
        final String requestJson = "{" +
                "\"currentPassword\": \"password\"," +
                "\"newPassword\": \"pass@123\"" +
                "}";

        mockMvc.perform(put("/profile/user")
                .with(httpBasic("test-user@gmail.com", "password"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(status().isOk());


    }

    @Test
    void shouldChangeThePasswordForUserHavingPasswordHistory() throws Exception {
        User user = new User("test-user", "password", "test-user@gmail.com", 1234567890L, Roles.ADMIN);
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encryptedPasswordOld = passwordEncoder.encode("pass@123");
        String encryptedPasswordOlder = passwordEncoder.encode("admin@123");
        String encryptedCurrentPassword = passwordEncoder.encode("password");
        PasswordHistory passwordHistory = new PasswordHistory(encryptedPasswordOld , encryptedPasswordOlder);
        user.setPassword(encryptedCurrentPassword);

        passwordHistory.setUser(user);
        passwordHistoryRepository.save(passwordHistory);
        final String requestJson = "{" +
                "\"currentPassword\": \"password\"," +
                "\"newPassword\": \"newPassword\"" +
                "}";

        mockMvc.perform(put("/profile/user")
                .with(httpBasic("test-user@gmail.com", "password"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(status().isOk());

    }

    @Test
    void shouldThrowErrorMessageIfNewPasswordIsSameAsLastThreePasswords() throws Exception {
        User user = new User("test-user", "password", "test-user@gmail.com", 1234567890L, Roles.ADMIN);
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encryptedPasswordOld =passwordEncoder.encode("pass@123");
        String encryptedPasswordOlder =passwordEncoder.encode("older_password");
        String encryptedCurrentPassword =passwordEncoder.encode("password");
        PasswordHistory passwordHistory =new PasswordHistory( encryptedPasswordOld , encryptedPasswordOlder);
        user.setPassword(encryptedCurrentPassword);
        passwordHistory.setUser(user);
        passwordHistoryRepository.save(passwordHistory);

        final String requestJson = "{" +
                "\"currentPassword\": \"password\"," +
                "\"newPassword\": \"pass@123\"" +
                "}";

        mockMvc.perform(put("/profile/user")
                .with(httpBasic("test-user@gmail.com", "password"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(status().isBadRequest());

    }

    @Test
    void shouldReturnUserDetails() throws Exception {
        createNewUser();

        mockMvc.perform(get("/user")
                .with(httpBasic("test-user@gmail.com", "password")))
                .andExpect(status().isOk());


    }

    @Transactional
    private User createNewUser() {

        return userRepository.save(new User("test-user", passwordEncoder.encode("password"), "test-user@gmail.com", 1234567890L, Roles.ADMIN));
    }
}
