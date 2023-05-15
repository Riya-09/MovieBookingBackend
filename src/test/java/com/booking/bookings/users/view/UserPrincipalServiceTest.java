package test.java.com.booking.bookings.users.view;

import com.booking.exceptions.IncorrectCurrentPasswordException;
import com.booking.exceptions.SamePasswordException;
import com.booking.registration.Roles;
import com.booking.users.User;
import com.booking.users.UserPrincipalService;
import com.booking.users.UserRepository;
import com.booking.users.profile.PasswordHistory;
import com.booking.users.profile.PasswordHistoryService;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class UserPrincipalServiceTest {
    public static final String TEST_USER_1_EMAIL = "test-user-1@gmail.com";
    private UserRepository userRepository;
    private PasswordHistoryService passwordHistoryService;
    private UserPrincipalService userPrincipalService;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        passwordHistoryService = mock(PasswordHistoryService.class);
        userPrincipalService = new UserPrincipalService(userRepository, passwordHistoryService);
    }

    @Test
    void shouldThrowIncorrectCurrentPasswordExceptionIfCurrentPasswordIsIncorrect() {
        User user = getUser();

        when(userRepository.findByEmail(TEST_USER_1_EMAIL)).thenReturn(java.util.Optional.of(user));

        assertThrows(IncorrectCurrentPasswordException.class, () ->
                userPrincipalService.updateUserPassword(TEST_USER_1_EMAIL, "password1", "new_password"));
    }


    @Test
    void shouldThrowSamePasswordExceptionIfNewPasswordIsSameAsLastThreePasswords() {

        User user = getUser();
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encryptedOldPassword = passwordEncoder.encode("old_password");
        String encryptedOlderPassword=passwordEncoder.encode("older_password");
        String encryptedCurrentPassword = passwordEncoder.encode("password");
        PasswordHistory passwordHistory = new PasswordHistory( encryptedOldPassword, encryptedOlderPassword);
        user.setPassword(encryptedCurrentPassword);
        user.setPasswordHistory(passwordHistory);

        when(userRepository.findByEmail(TEST_USER_1_EMAIL)).thenReturn(java.util.Optional.of(user));

        assertThrows(SamePasswordException.class, () ->
                userPrincipalService.updateUserPassword(TEST_USER_1_EMAIL, "password", "old_password"));

    }

    @Test
    void shouldUpdatePasswordSuccessfullyForUserWithoutPasswordHistory() throws IncorrectCurrentPasswordException, SamePasswordException {

        User user = getUser();
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encryptedCurrentPassword=passwordEncoder.encode("password");
        PasswordHistory passwordHistory = new PasswordHistory( encryptedCurrentPassword, "_");
        user.setPassword(encryptedCurrentPassword);
        passwordHistory.setUser(user);

        when(userRepository.findByEmail(TEST_USER_1_EMAIL)).thenReturn(java.util.Optional.of(user));
        userPrincipalService.updateUserPassword(TEST_USER_1_EMAIL, "password", "new_password");

        verify(passwordHistoryService, times(1)).saveUserPasswordHistory(passwordHistory);

    }

    @Test
    void shouldUpdatePasswordSuccessfullyForUserWithPasswordHistory() throws IncorrectCurrentPasswordException, SamePasswordException {

        User user = getUser();
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encryptedOldPassword=passwordEncoder.encode("old_password");
        String encryptedOlderPassword=passwordEncoder.encode("older_password");
        String encryptedCurrentPassword=passwordEncoder.encode("password");
        PasswordHistory oldPasswordHistory = new PasswordHistory(encryptedOldPassword, encryptedOlderPassword);
        user.setPasswordHistory(oldPasswordHistory);
        PasswordHistory newPasswordHistory = new PasswordHistory(encryptedCurrentPassword,encryptedOldPassword);
        user.setPassword(encryptedCurrentPassword);
        newPasswordHistory.setUser(user);

        when(userRepository.findByEmail(TEST_USER_1_EMAIL)).thenReturn(java.util.Optional.of(user));
        userPrincipalService.updateUserPassword(TEST_USER_1_EMAIL, "password", "new_password");

        verify(passwordHistoryService, times(1)).saveUserPasswordHistory(newPasswordHistory);
    }

    @Test
    void shouldReturnUserDetailsByEmailId() {
        User user = getUser();

        when(userRepository.findByEmail(TEST_USER_1_EMAIL)).thenReturn(java.util.Optional.of(user));
        userPrincipalService.findUserByEmail(TEST_USER_1_EMAIL);

        verify(userRepository).findByEmail(TEST_USER_1_EMAIL);

    }

    @NotNull
    private User getUser() {
        return new User("test-user-1", "password", TEST_USER_1_EMAIL, 1234567890L, Roles.ADMIN);
    }

}
