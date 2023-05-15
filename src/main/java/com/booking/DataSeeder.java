package main.java.com.booking;

import com.booking.registration.Roles;
import com.booking.users.User;
import com.booking.users.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
public class DataSeeder {

    @Bean
    CommandLineRunner initDatabase(UserRepository repository) {
        return args -> {
            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            Pattern BCRYPT_PATTERN = Pattern.compile("\\A\\$2a?\\$\\d\\d\\$[./0-9A-Za-z]{53}");
            if (repository.findByUsername("seed-user-1").isEmpty()) {
                repository.save(new User("seed-user-1", passwordEncoder.encode("foobar"),"seed-user-1@gmail.com",1234567890l, Roles.ADMIN));
            }
            else{

                User user = repository.findByUsername("seed-user-1").get();
                String unencryptedPassword = user.getPassword();
                if (!BCRYPT_PATTERN.matcher(unencryptedPassword).matches()) {
                    user.setPassword(passwordEncoder.encode(unencryptedPassword));
                    repository.save(user);
                }

            }
            if (repository.findByUsername("seed-user-2").isEmpty()) {
                repository.save(new User("seed-user-2", passwordEncoder.encode("foobar"),"seed-user-2@gmail.com",1234567890l,Roles.ADMIN));
            }
            else{

                User user = repository.findByUsername("seed-user-2").get();
                String unencryptedPassword = user.getPassword();
                if (!BCRYPT_PATTERN.matcher(unencryptedPassword).matches()) {
                    user.setPassword(passwordEncoder.encode(unencryptedPassword));
                    repository.save(user);
                }

            }

            List<User> allUser= new ArrayList<User>();
            allUser=repository.findAll();

            int size=allUser.size();
            for(int i=0;i<size;i++)
            {

                User user = allUser.get(i);
                String unencryptedPassword = user.getPassword();
                if (!BCRYPT_PATTERN.matcher(unencryptedPassword).matches()) {
                    user.setPassword(passwordEncoder.encode(unencryptedPassword));
                    repository.save(user);
                }
            }
        };
    }
}
