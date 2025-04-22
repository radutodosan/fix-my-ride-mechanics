package com.radutodosan.mechanics.controllers;

import com.radutodosan.mechanics.dtos.*;
import com.radutodosan.mechanics.entities.Mechanic;
import com.radutodosan.mechanics.services.MechanicService;
import com.radutodosan.mechanics.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth/mechanics")
@RequiredArgsConstructor
public class AuthController {

    private final MechanicService mechanicService;
    private final JwtUtil jwtUtil;

    @PostMapping("/signup")
    public ResponseEntity<ApiResponseDTO<String>> signup(@RequestBody SignupRequestDTO request) {
        try {
            mechanicService.signup(request);
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
            Mechanic mechanic = mechanicService.authenticate(request);
            String token = jwtUtil.generateToken(mechanic.getUsername());
            JwtResponse jwtResponse = new JwtResponse(token, mechanic);
            return ResponseEntity.ok(new ApiResponseDTO<>(true, "Login successful", jwtResponse));
        } catch (RuntimeException e) {
            return ResponseEntity
                    .status(401)
                    .body(new ApiResponseDTO<>(false, "Login failed: " + e.getMessage(), null));
        }
    }

    @GetMapping("/me")
    public ResponseEntity<?> getMechanicFromToken(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            ApiResponseDTO<?> errorResponse = new ApiResponseDTO<>(
                    false,
                    "Mechanic is not authenticated",
                    null
            );
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }

        try {
            String username = userDetails.getUsername();
            Mechanic mechanic = mechanicService.getByUsername(username);
            MechanicDetailsDTO mechanicDetails = MechanicDetailsDTO.builder()
                    .username(mechanic.getUsername())
                    .email(mechanic.getEmail())
                    .build();
            ApiResponseDTO<?> response = new ApiResponseDTO<>(
                    true,
                    "Mechanic retrieved successfully",
                    mechanicDetails
            );
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            ApiResponseDTO<?> errorResponse = new ApiResponseDTO<>(
                    false,
                    "Mechanic not found: " + e.getMessage(),
                    null
            );
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }
    }

}
