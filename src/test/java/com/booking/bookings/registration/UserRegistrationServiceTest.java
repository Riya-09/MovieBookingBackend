package test.java.com.booking.bookings.registration;

import com.booking.exceptions.PasswordMisMatchException;
import com.booking.exceptions.UserAlreadyRegisteredException;
import com.booking.users.User;
import com.booking.users.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class UserRegistrationServiceTest {
    private UserRepository userRepository;
    private UserRegistrationService userRegistrationService;
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    public void beforeEach() {
        userRepository = mock(UserRepository.class);
        passwordEncoder = new BCryptPasswordEncoder();
        userRegistrationService = new UserRegistrationService(userRepository,passwordEncoder);
    }

    @Test
    public void should_save_user()throws UserAlreadyRegisteredException,PasswordMisMatchException{
        User mockUser = mock(User.class);
        User user = new User("jaya","Jaya@123","jaya@gmail.com",1234567890l,Roles.CUSTOMER);
        user.setPassword(passwordEncoder.encode("Jaya@123"));
        when(userRepository.save(user)).thenReturn(mockUser);
        User actualUser = userRegistrationService.create("jaya","jaya@gmail.com",1234567890l,"Jaya@123","Jaya@123");
        verify(userRepository).save(actualUser);
    }


    @Test
    public void should_not_register_user_when_already_registered() {
        User user = new User("jaya","jaya","jaya@gmail.com",1234567890l,Roles.CUSTOMER);
        when(userRepository.findByEmail("jaya@gmail.com")).thenReturn(Optional.of(user));
        assertThrows(UserAlreadyRegisteredException.class, () -> userRegistrationService.create("jaya","jaya@gmail.com",1234567890l,"jaya","jaya"));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    public void should_not_register_user_when_password_mismatched() {
        assertThrows(PasswordMisMatchException.class, () -> userRegistrationService.create("jaya","jaya@gmail.com",1234567890l,"jaya","jaya1"));
        verify(userRepository, never()).save(any(User.class));
    }
}