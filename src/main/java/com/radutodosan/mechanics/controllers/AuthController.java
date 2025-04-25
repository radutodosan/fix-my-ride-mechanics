package com.radutodosan.mechanics.controllers;

import com.radutodosan.mechanics.dtos.*;
import com.radutodosan.mechanics.entities.Mechanic;
import com.radutodosan.mechanics.services.MechanicService;
import com.radutodosan.mechanics.utils.JwtUtil;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

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
    public ResponseEntity<ApiResponseDTO<JwtResponse>> login(@RequestBody LoginRequestDTO loginRequest, HttpServletResponse response) {
        try {
            Mechanic mechanic = mechanicService.authenticate(loginRequest);

            String accessToken = jwtUtil.generateAccessToken(mechanic.getUsername());
            ResponseCookie responseCookie = jwtUtil.generateResponseCookie(mechanic.getUsername());

            response.addHeader(HttpHeaders.SET_COOKIE, responseCookie.toString());

            JwtResponse jwtResponse = new JwtResponse(accessToken, mechanic);
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

    @PostMapping("/refresh-token")
    public ResponseEntity<ApiResponseDTO<Map<String, String>>> refreshAccessToken(
            @CookieValue(name = "refreshToken", required = false) String refreshToken) {

        if (refreshToken == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponseDTO<>(false, "Refresh token missing", null));
        }

        if (!jwtUtil.validateToken(refreshToken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponseDTO<>(false, "Invalid or expired refresh token", null));
        }

        String username = jwtUtil.extractUsername(refreshToken);
        String newAccessToken = jwtUtil.generateAccessToken(username);

        Map<String, String> tokenData = Map.of(
                "accessToken", newAccessToken
        );

        return ResponseEntity.ok(new ApiResponseDTO<>(true, "Access token refreshed successfully", tokenData));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponseDTO<?>> logout(HttpServletResponse response) {
        // 🧹 Creăm un Cookie de refreshToken golit
        ResponseCookie deleteCookie = ResponseCookie.from("refreshToken", "")
                .httpOnly(true)
                .secure(true) // true dacă ai HTTPS
                .path("/auth/refresh-token")
                .maxAge(0) // ⚡️ Expiră imediat
                .sameSite("Strict")
                .build();

        // 📨 Adăugăm în header pentru a forța browserul să șteargă cookie-ul
        response.addHeader(HttpHeaders.SET_COOKIE, deleteCookie.toString());

        return ResponseEntity.ok(new ApiResponseDTO<>(true, "Logged out successfully", null));
    }



}
