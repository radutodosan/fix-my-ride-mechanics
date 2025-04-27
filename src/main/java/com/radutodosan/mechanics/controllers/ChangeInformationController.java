package com.radutodosan.mechanics.controllers;

import com.radutodosan.mechanics.dtos.ApiResponseDTO;
import com.radutodosan.mechanics.dtos.ChangeEmailRequestDTO;
import com.radutodosan.mechanics.dtos.ChangePasswordRequestDTO;
import com.radutodosan.mechanics.entities.Mechanic;
import com.radutodosan.mechanics.repositories.MechanicRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("mechanics/details")
@RequiredArgsConstructor
public class ChangeInformationController {

    private final MechanicRepository mechanicRepository;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody ChangePasswordRequestDTO request) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Mechanic client = mechanicRepository.findByUsername(username).orElseThrow(() ->
                new UsernameNotFoundException("Client not found"));

        if (!passwordEncoder.matches(request.getCurrentPassword(), client.getPassword())) {
            ApiResponseDTO<?> error = new ApiResponseDTO<>(false, "Current password is incorrect", null);
            return ResponseEntity.badRequest().body(error);
        }

        client.setPassword(passwordEncoder.encode(request.getNewPassword()));
        mechanicRepository.save(client);

        ApiResponseDTO<String> success = new ApiResponseDTO<>(true, "Password changed successfully", null);
        return ResponseEntity.ok(success);
    }

    @PostMapping("/change-email")
    public ResponseEntity<?> changeEmail(@RequestBody ChangeEmailRequestDTO request) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Mechanic client = mechanicRepository.findByUsername(username).orElseThrow(() ->
                new UsernameNotFoundException("Client not found"));

        if (!passwordEncoder.matches(request.getPassword(), client.getPassword())) {
            ApiResponseDTO<?> error = new ApiResponseDTO<>(false, "Password is incorrect", null);
            return ResponseEntity.badRequest().body(error);
        }

        if (mechanicRepository.existsByEmail(request.getNewEmail())) {
            ApiResponseDTO<?> error = new ApiResponseDTO<>(false, "Email already in use", null);
            return ResponseEntity.badRequest().body(error);
        }

        client.setEmail(request.getNewEmail());
        mechanicRepository.save(client);

        ApiResponseDTO<String> success = new ApiResponseDTO<>(true, "Email changed successfully", null);
        return ResponseEntity.ok(success);
    }

}
