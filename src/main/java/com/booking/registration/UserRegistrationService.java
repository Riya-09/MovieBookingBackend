package main.java.com.booking.registration;

import com.booking.exceptions.PasswordMisMatchException;
import com.booking.exceptions.UserAlreadyRegisteredException;
import com.booking.users.User;
import com.booking.users.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Optional;
import org.springframework.security.crypto.password.PasswordEncoder;

@Service
public class UserRegistrationService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserRegistrationService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder=passwordEncoder;
    }

    public User create(String name, String email, Long mobileNumber, String  password, String reenterPassword) throws UserAlreadyRegisteredException, PasswordMisMatchException {
        Optional<User> user = userRepository.findByEmail(email);
        if(!user.isEmpty())
            throw new UserAlreadyRegisteredException(user.get().getEmail());
        if(!password.equals(reenterPassword))
            throw new PasswordMisMatchException("Password::"+password+",ReenterPassword::"+reenterPassword);

        User user1=new User(name,passwordEncoder.encode(password),email,mobileNumber, Roles.CUSTOMER);
        System.out.println(user1);
        userRepository.save(user1);
        return user1;
    }
}
