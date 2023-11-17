package kefas.backend.GeoByte.auth;

import kefas.backend.GeoByte.config.LocalMemStorage;
import kefas.backend.GeoByte.config.security.JwtService;
import kefas.backend.GeoByte.config.security.UserPrincipal;
import kefas.backend.GeoByte.entity.Users;
import kefas.backend.GeoByte.enums.Role;
import kefas.backend.GeoByte.exception.InvalidEmailException;
import kefas.backend.GeoByte.exception.UserAlreadyExistException;
import kefas.backend.GeoByte.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
  private final UserRepository repository;
  private final PasswordEncoder passwordEncoder;
  private final JwtService jwtService;
  private final AuthenticationManager authenticationManager;
  private final LocalMemStorage memStorage;

  public AuthenticationResponse register(RegisterRequest request) {

    String email = request.getEmail();
    if(!isValidEmail(email)) {
      throw new InvalidEmailException("Invalid email format: " + email);
    }

    if(repository.findByEmail(request.getEmail()).isPresent()) {
      throw new UserAlreadyExistException("User with email " + request.getEmail() + " already exists");
    }
    var user = Users.builder()
            .firstname(request.getFirstname())
            .lastname(request.getLastname())
            .email(request.getEmail())
            .password(passwordEncoder.encode(request.getPassword()))
            .role(Role.ADMIN)
            .build();
    Users savedUser = repository.save(user);
    var jwtToken = jwtService.generateToken(new UserPrincipal(savedUser));
    return AuthenticationResponse.builder()
            .token(jwtToken)
            .build();
  }


  public AuthenticationResponse authenticate(AuthenticationRequest request) {
    Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                    request.getEmail(),
                    request.getPassword()
            )
    );
    var user = repository.findByEmail(request.getEmail())
        .orElseThrow();
    var jwtToken = jwtService.generateToken(new UserPrincipal(user));
    SecurityContextHolder.getContext().setAuthentication(authentication);
    return AuthenticationResponse.builder()
        .token(jwtToken)
        .build();
  }

  public String logout(String headerToken) {
    String email = null;
    if (headerToken.startsWith("Bearer ")) {
      headerToken = headerToken.replace("Bearer ", "").replace("\\s", "");
      email = jwtService.extractUsername(headerToken);
    }
    blacklistToken(email, headerToken);
    return "Logout successful";
  }

  private boolean isValidEmail(String email) {
    String emailRegexPattern = "^[^@]+@[^@.]+\\.[^@.]+$";
    return email.matches(emailRegexPattern);
  }

  public void blacklistToken(String email, String token) {
    Date expiryDate = jwtService.extractExpiration(token);
    int expiryTimeInSeconds = (int) ((expiryDate.getTime() - new Date().getTime())/1000);
    memStorage.setBlacklist(email, token, expiryTimeInSeconds);
  }

}
