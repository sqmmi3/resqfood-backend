package be.ucll.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import be.ucll.config.util.JwtService;
import be.ucll.dto.JwtResponse;
import be.ucll.dto.LoginRequest;

@RestController
@RequestMapping("/auth")
public class AuthController {
  private final AuthenticationManager authManager;

  private final JwtService jwtService;

  public AuthController(AuthenticationManager authManager, JwtService jwtService) {
    this.authManager = authManager;
    this.jwtService = jwtService;
  }

  @PostMapping("/login")
  public ResponseEntity<JwtResponse> login(@RequestBody LoginRequest request) {
    Authentication auth = authManager.authenticate(
        new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
    );

    SecurityContextHolder.getContext().setAuthentication(auth);

    String token = jwtService.generateToken(auth.getName());

    return ResponseEntity.ok(new JwtResponse(token));
  }
}
