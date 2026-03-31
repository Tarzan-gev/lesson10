package web.service;

import org.springframework.stereotype.Service;
import web.model.User;

import java.util.List;
import java.util.Optional;

@Service
public interface UserService {
    List<User> getAllUsers();

    User getUserById(Long id);

    void saveUser(User user);

    void updateUser(User user);

    void deleteUser(Long id);

    User getUserByIdWithRoles(Long id);

    List<User> getAllUsersWithRoles();

    Optional<User> findByEmail(String email);


    void updateUser(Long id, String name, String surname, String email);
}