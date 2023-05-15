package test.java.com.booking.bookings.users.profile;

import com.booking.registration.Roles;
import com.booking.users.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

public class PasswordHistoryServiceTest {

    private static final String TEST_USER_1_EMAIL = "test-user-1@gmail.com";
    private PasswordHistoryRepository passwordHistoryRepository;
    private PasswordHistoryService passwordHistoryService;

    @BeforeEach
    void setUp() {
        passwordHistoryRepository = mock(PasswordHistoryRepository.class);
        passwordHistoryService = new PasswordHistoryService(passwordHistoryRepository);
    }


    @Test
    void shouldSavePasswordHistory() {
        PasswordHistory passwordHistory = new PasswordHistory("old_password", "older_password");
        passwordHistory.setUser(new User("test-user", "password", TEST_USER_1_EMAIL, 1234567890L, Roles.ADMIN));

        passwordHistoryService.saveUserPasswordHistory(passwordHistory);

        verify(passwordHistoryRepository, times(1)).save(passwordHistory);

    }




}
