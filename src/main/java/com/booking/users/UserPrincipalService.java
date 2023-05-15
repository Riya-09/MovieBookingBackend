package main.java.com.booking.users;

import com.booking.exceptions.IncorrectCurrentPasswordException;
import com.booking.exceptions.SamePasswordException;
import com.booking.users.profile.PasswordHistory;
import com.booking.users.profile.PasswordHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;

@Service
public class UserPrincipalService implements UserDetailsService {
    private final UserRepository userRepository;
    private final PasswordHistoryService passwordHistoryService;


    @Autowired
    public UserPrincipalService(UserRepository userRepository, PasswordHistoryService passwordHistoryService) {
        this.userRepository = userRepository;
        this.passwordHistoryService = passwordHistoryService;

    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User savedUser = findUserByEmail(email);
        return new UserPrincipal(savedUser);
    }

    public User findUserByEmail(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    public void saveUserDetails(User user) {
        userRepository.save(user);
    }

    public void updateUserPassword(String emailId, String currentPassword, String newPassword) throws IncorrectCurrentPasswordException, SamePasswordException {
        User user = findUserByEmail(emailId);
        String currentPasswordFromDB = user.getPassword();
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();


        if (!passwordEncoder.matches(currentPassword, currentPasswordFromDB)) {
            throw new IncorrectCurrentPasswordException("Current password is not correct");
        }

        PasswordHistory passwordHistory = user.getPasswordHistory();
        if (passwordHistory!=null) {
            System.out.println("Found User password history");
            updatePasswordForUserWithPasswordHistory(passwordHistory, currentPasswordFromDB, newPassword, user);
            return;
        }

        System.out.println("NO PASSWORD HISTORY");
        updatePasswordForUserWithoutPasswordHistory(currentPasswordFromDB, newPassword, user);

    }

    private void updatePasswordForUserWithoutPasswordHistory(String currentPassword, String newPassword, User user) {
        PasswordHistory userPasswordHistory = new PasswordHistory(currentPassword, "_");
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        user.setPassword(passwordEncoder.encode(newPassword));
        userPasswordHistory.setUser(user);

        passwordHistoryService.saveUserPasswordHistory(userPasswordHistory);
    }

    private void updatePasswordForUserWithPasswordHistory(PasswordHistory userPasswordHistory, String currentPassword, String newPassword, User user) throws SamePasswordException {
        String oldPassword = userPasswordHistory.getOldPassword();
        String olderPassword = userPasswordHistory.getOlderPassword();
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        if (passwordEncoder.matches(newPassword,currentPassword) || passwordEncoder.matches(newPassword, oldPassword) || passwordEncoder.matches(newPassword, olderPassword)) {
            throw new SamePasswordException("password can't be same as last 3 passwords");
        }

        userPasswordHistory.setOlderPassword(oldPassword);
        userPasswordHistory.setOldPassword(currentPassword);

        user.setPassword(passwordEncoder.encode(newPassword));
        userPasswordHistory.setUser(user);
        passwordHistoryService.saveUserPasswordHistory(userPasswordHistory);
    }

}
