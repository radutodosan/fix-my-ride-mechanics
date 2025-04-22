package com.radutodosan.mechanics.controllers;

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
@RequestMapping("mechanics/change-info")
@RequiredArgsConstructor
public class ChangeInformationController {

    private final MechanicRepository mechanicRepository;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody ChangePasswordRequestDTO request) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Mechanic user = mechanicRepository.findByUsername(username).orElseThrow(() ->
                new UsernameNotFoundException("User not found"));

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            return ResponseEntity.badRequest().body("Current password is incorrect");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        mechanicRepository.save(user);
        return ResponseEntity.ok("Password changed successfully");
    }

    @PostMapping("/change-email")
    public ResponseEntity<?> changeEmail(@RequestBody ChangeEmailRequestDTO request) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Mechanic user = mechanicRepository.findByUsername(username).orElseThrow(() ->
                new UsernameNotFoundException("User not found"));

        // check if password matches
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            return ResponseEntity.badRequest().body("Password is incorrect");
        }

        // check if email is already used
        if (mechanicRepository.existsByEmail(request.getNewEmail())) {
            return ResponseEntity.badRequest().body("Email already in use");
        }

        user.setEmail(request.getNewEmail());
        mechanicRepository.save(user);
        return ResponseEntity.ok("Email changed successfully");
    }

}
