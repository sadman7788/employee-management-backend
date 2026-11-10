package com.neurosoft.employee_management.config;

import com.neurosoft.employee_management.entity.User;
import com.neurosoft.employee_management.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataSeeder(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.count() == 0) {
            userRepository.save(new User("admin", passwordEncoder.encode("admin123"), "ROLE_ADMIN"));
            userRepository.save(new User("hr", passwordEncoder.encode("hr123"), "ROLE_HR"));
            userRepository.save(new User("employee", passwordEncoder.encode("emp123"), "ROLE_EMPLOYEE"));
            System.out.println(">>> Default Users Created: admin, hr, employee");
        }
    }
}