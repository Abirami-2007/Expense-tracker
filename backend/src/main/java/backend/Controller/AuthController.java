package backend.Controller;
import backend.Model.User;
import backend.Repository.UserRepository;
import backend.Service.UserService;
import backend.Utility.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtUtil JWTUtil;

    @PostMapping("/register")
    public ResponseEntity<String> Registeruser(@RequestBody Map<String, String> body) {


        String email = body.get("email");
        String password = body.get("password");

        if (email == null || password == null) {
            return ResponseEntity.badRequest().body("Email and Password are required.");
        }

        if (userRepository.findByEmail(email).isPresent()) {
            return new ResponseEntity<>("Email already exist", HttpStatus.CONFLICT);
        }

        userService.createUser(User.builder().email(email).password(password).build());
        return new ResponseEntity<>("Successfully Registered", HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginuser(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        String password = body.get("password");

        User user = userRepository.findByEmail(email)
                .orElse(null);

        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("User not found.");
        }

        if (!passwordEncoder.matches(password, user.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid Password.");
        }

        // Generate JWT
        String token = JWTUtil.generateToken(email);
        return ResponseEntity.ok(Map.of("token",token));
    }

}
