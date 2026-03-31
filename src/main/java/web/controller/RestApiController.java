package web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import web.model.Role;
import web.model.User;
import web.service.RoleService;
import web.service.UserService;

import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api")
public class RestApiController {

    private final UserService userService;
    private final RoleService roleService;

    @Autowired
    public RestApiController(UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }


    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.getAllUsersWithRoles();
        return ResponseEntity.ok(users);
    }


    @GetMapping("/users/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        try {
            User user = userService.getUserByIdWithRoles(id);
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }


    @GetMapping("/user/current")
    public ResponseEntity<User> getCurrentUser() {
        try {
            User user = userService.getCurrentUser();
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }


    @GetMapping("/roles")
    public ResponseEntity<List<Role>> getAllRoles() {
        List<Role> roles = roleService.getAllRoles();
        return ResponseEntity.ok(roles);
    }


    @PostMapping("/users")
    public ResponseEntity<?> createUser(@RequestBody Map<String, Object> payload) {
        try {
            User user = new User();
            user.setName((String) payload.get("name"));
            user.setSurname((String) payload.get("surname"));
            user.setAge((Integer) payload.get("age"));
            user.setEmail((String) payload.get("email"));
            user.setPassword((String) payload.get("password"));

            // Обработка ролей
            if (payload.containsKey("roleIds")) {
                @SuppressWarnings("unchecked")
                List<Integer> roleIds = (List<Integer>) payload.get("roleIds");
                Set<Role> roles = roleService.getRolesByIds(roleIds.stream().map(Long::valueOf).toList());
                user.setRoles(roles);
            }

            userService.saveUser(user);
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("message", "User created successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }


    @PutMapping("/users/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody Map<String, Object> payload) {
        try {
            User existingUser = userService.getUserByIdWithRoles(id);

            if (payload.containsKey("name")) existingUser.setName((String) payload.get("name"));
            if (payload.containsKey("surname")) existingUser.setSurname((String) payload.get("surname"));
            if (payload.containsKey("age")) existingUser.setAge((Integer) payload.get("age"));
            if (payload.containsKey("email")) existingUser.setEmail((String) payload.get("email"));
            if (payload.containsKey("password") && !((String) payload.get("password")).isEmpty()) {
                existingUser.setPassword((String) payload.get("password"));
            }

            // Обновление ролей
            if (payload.containsKey("roleIds")) {
                @SuppressWarnings("unchecked")
                List<Integer> roleIds = (List<Integer>) payload.get("roleIds");
                Set<Role> roles = roleService.getRolesByIds(roleIds.stream().map(Long::valueOf).toList());
                existingUser.setRoles(roles);
            }

            userService.updateUser(existingUser);
            return ResponseEntity.ok(Map.of("message", "User updated successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }


    @DeleteMapping("/users/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        try {
            userService.deleteUser(id);
            return ResponseEntity.ok(Map.of("message", "User deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}

