package com.radutodosan.mechanics.services;

import com.radutodosan.mechanics.entities.AppUser;
import com.radutodosan.mechanics.repositories.AppUserRepository;
import com.radutodosan.mechanics.dtos.LoginRequestDTO;
import com.radutodosan.mechanics.dtos.SignupRequestDTO;
import com.radutodosan.mechanics.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AppUserService {

    private final AppUserRepository appUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    public void signup(SignupRequestDTO signUpRequest) {
        // Check if username or email already exists
        if (appUserRepository.existsByUsername(signUpRequest.getUsername())) {
            throw new RuntimeException("Username already taken");
        }

        if (appUserRepository.existsByEmail(signUpRequest.getEmail())) {
            throw new RuntimeException("Email already taken");
        }

        AppUser user = AppUser.builder()
                .username(signUpRequest.getUsername())
                .email(signUpRequest.getEmail())
                .password(passwordEncoder.encode(signUpRequest.getPassword()))
                .build();

        appUserRepository.save(user);
    }

    public AppUser authenticate(LoginRequestDTO request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        return appUserRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public AppUser getByUsername(String username) {
        return appUserRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

}
