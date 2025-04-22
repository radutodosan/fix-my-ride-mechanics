package com.radutodosan.mechanics.services;

import com.radutodosan.mechanics.entities.Mechanic;
import com.radutodosan.mechanics.repositories.MechanicRepository;
import com.radutodosan.mechanics.dtos.LoginRequestDTO;
import com.radutodosan.mechanics.dtos.SignupRequestDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MechanicService {

    private final MechanicRepository mechanicRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    public void signup(SignupRequestDTO signUpRequest) {
        // Check if username or email already exists
        if (mechanicRepository.existsByUsername(signUpRequest.getUsername())) {
            throw new RuntimeException("Username already taken");
        }

        if (mechanicRepository.existsByEmail(signUpRequest.getEmail())) {
            throw new RuntimeException("Email already taken");
        }

        Mechanic user = Mechanic.builder()
                .username(signUpRequest.getUsername())
                .email(signUpRequest.getEmail())
                .password(passwordEncoder.encode(signUpRequest.getPassword()))
                .build();

        mechanicRepository.save(user);
    }

    public Mechanic authenticate(LoginRequestDTO request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        return mechanicRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public Mechanic getByUsername(String username) {
        return mechanicRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Mechanic not found"));
    }

    public boolean existsByUsername(String username) {
        return mechanicRepository.existsByUsername(username);
    }


}
