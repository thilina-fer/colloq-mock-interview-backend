package lk.ijse.springbootbackend.config;

import lk.ijse.springbootbackend.entity.Auth;
import lk.ijse.springbootbackend.entity.Role;
import lk.ijse.springbootbackend.repo.AuthRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AdminInitializer implements CommandLineRunner {

    private final AuthRepo authRepo;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (authRepo.findByEmail("admin@colloq.lk").isEmpty()) {
            Auth admin = Auth.builder()
                    .username("admin")
                    .email("admin@colloq.lk")
                    .password(passwordEncoder.encode("Admin@123"))
                    .role(Role.ADMIN)
                    .status("ACTIVE")
                    .profileUpdated(true)
                    .build();
            authRepo.save(admin);
            System.out.println("Default Admin created: admin@colloq.lk / Admin@123");
        }
    }
}