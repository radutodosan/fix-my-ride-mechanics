package com.radutodosan.mechanics.controllers;

import com.radutodosan.mechanics.dtos.ApiResponseDTO;
import com.radutodosan.mechanics.dtos.JwtResponse;
import com.radutodosan.mechanics.dtos.LoginRequestDTO;
import com.radutodosan.mechanics.dtos.SignupRequestDTO;
import com.radutodosan.mechanics.entities.AppUser;
import com.radutodosan.mechanics.services.AppUserService;
import com.radutodosan.mechanics.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AppUserService appUserService;
    private final JwtUtil jwtUtil;

    @PostMapping("/signup")
    public ResponseEntity<ApiResponseDTO<String>> signup(@RequestBody SignupRequestDTO request) {
        try {
            appUserService.signup(request);
            return ResponseEntity.ok(new ApiResponseDTO<>(true, "User registered successfully", null));
        } catch (RuntimeException e) {
            return ResponseEntity
                    .badRequest()
                    .body(new ApiResponseDTO<>(false, e.getMessage(), null));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponseDTO<JwtResponse>> login(@RequestBody LoginRequestDTO request) {
        try {
            AppUser user = appUserService.authenticate(request);
            String token = jwtUtil.generateToken(user.getUsername());
            JwtResponse jwtResponse = new JwtResponse(token, user.getUsername(), user.getEmail());
            return ResponseEntity.ok(new ApiResponseDTO<>(true, "Login successful", jwtResponse));
        } catch (RuntimeException e) {
            return ResponseEntity
                    .status(401)
                    .body(new ApiResponseDTO<>(false, "Login failed: " + e.getMessage(), null));
        }
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Not authenticated");
        }

        return ResponseEntity.ok(Map.of(
                "username", userDetails.getUsername()
        ));
    }




}
