package com.multiservicio.controller;

import com.multiservicio.dto.AuthResponse;
import com.multiservicio.dto.LoginRequest;
import com.multiservicio.dto.RegisterRequest;
import com.multiservicio.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> registrar(@Valid @RequestBody RegisterRequest request) {
        AuthResponse response = authService.registrar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @GetMapping("/test")
    public String test() {
        return "OK FUNCIONA";
    }

    @GetMapping("/login-test")
    public ResponseEntity<AuthResponse> loginTest() {
        LoginRequest request = new LoginRequest();
        request.setUsername("admin");
        request.setPassword("1234");
        return ResponseEntity.ok(authService.login(request));
    }
}
