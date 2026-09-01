package com.zerocode.hotelPackagesApi.component;

import com.zerocode.hotelPackagesApi.model.Authority;
import com.zerocode.hotelPackagesApi.model.Role;
import com.zerocode.hotelPackagesApi.model.User;
import com.zerocode.hotelPackagesApi.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Component
@Profile("!test")
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // Only create if not exists
        if (userRepository.findByUsername("admin").isEmpty()) {
            User adminUser = new User();
            adminUser.setUsername("admin");
            adminUser.setPassword(passwordEncoder.encode("admin123"));
            adminUser.setEnabled(true);
            adminUser.setRole(Role.ADMIN);

            Authority adminAuthority = new Authority();
            adminAuthority.setAuthority("ROLE_ADMIN");
            adminAuthority.setUser(adminUser);
            adminAuthority.setRole(Role.ADMIN);

            adminUser.setAuthorities(Collections.singletonList(adminAuthority));
            userRepository.save(adminUser);
        }

        // Similar for user role
        if (userRepository.findByUsername("user").isEmpty()) {
            User user = new User();
            user.setUsername("user");
            user.setPassword(passwordEncoder.encode("user123"));
            user.setEnabled(true);
            user.setRole(Role.CUSTOMER);

            Authority userAuthority = new Authority();
            userAuthority.setAuthority("ROLE_USER");
            userAuthority.setUser(user);
            userAuthority.setRole(Role.CUSTOMER);

            user.setAuthorities(Collections.singletonList(userAuthority));
            userRepository.save(user);
        }
    }
}