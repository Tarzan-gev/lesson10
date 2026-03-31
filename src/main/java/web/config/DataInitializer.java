package web.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import web.model.Role;
import web.model.User;
import web.repository.RoleRepository;
import web.repository.UserRepository;

import java.util.Set;

@Configuration
public class DataInitializer {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository,
                           RoleRepository roleRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Bean
    @Transactional
    public CommandLineRunner dataLoader() {
        return args -> {


            Role userRole = roleRepository.findByName("USER")
                    .orElseGet(() -> {
                        System.out.println("Создаём роль: USER");
                        Role role = new Role("USER");
                        return roleRepository.save(role);
                    });

            Role adminRole = roleRepository.findByName("ADMIN")
                    .orElseGet(() -> {
                        System.out.println("Создаём роль: ADMIN");
                        Role role = new Role("ADMIN");
                        return roleRepository.save(role);
                    });

            System.out.println("Роли созданы/найдены. ID USER: " + userRole.getId() +
                    ", ID ADMIN: " + adminRole.getId());

            // Создаём пользователя, если его нет
            if (userRepository.findByEmail("user@example.com").isEmpty()) {
                User user = new User("Test",20, "User", "user@example.com",
                        passwordEncoder.encode("password"));
                user.setRoles(Set.of(userRole));
                userRepository.save(user);
                System.out.println(" Создан пользователь: user@example.com");
            } else {
                System.out.println(" Пользователь user@example.com уже существует");
            }


            if (userRepository.findByEmail("admin@example.com").isEmpty()) {
                User admin = new User("Admin", 30, "Admin", "admin@example.com",
                        passwordEncoder.encode("admin"));
                admin.setRoles(Set.of(adminRole, userRole));
                userRepository.save(admin);
                System.out.println(" Создан администратор: admin@example.com");
            } else {
                System.out.println("ℹ Администратор admin@example.com уже существует");
            }

            System.out.println("=== Инициализация данных завершена ===");
        };
    }
}