package com.radutodosan.mechanics.controllers;

import com.radutodosan.mechanics.dtos.ApiResponseDTO;
import com.radutodosan.mechanics.dtos.MechanicDetailsDTO;
import com.radutodosan.mechanics.entities.Mechanic;
import com.radutodosan.mechanics.services.MechanicService;
import com.radutodosan.mechanics.repositories.MechanicRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/mechanics")
@RequiredArgsConstructor
public class MechanicValidationController {

    private final MechanicService mechanicService;
    private final MechanicRepository mechanicRepository;

    @GetMapping("/exists/{username}")
    public ResponseEntity<?> mechanicExists(@PathVariable String username) {
        boolean exists = mechanicService.existsByUsername(username);

        ApiResponseDTO<Boolean> response = new ApiResponseDTO<>(
                true,
                exists ? "Mechanic exists" : "Mechanic does not exist",
                exists
        );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllMechanics() {
        List<Mechanic> mechanics = mechanicRepository.findAll();
        List<MechanicDetailsDTO> mechanicDetails = mechanics.stream()
                .map(mechanic -> new MechanicDetailsDTO(mechanic.getUsername(), mechanic.getEmail(), mechanic.getPictureUrl()))
                .toList();

        ApiResponseDTO<List<MechanicDetailsDTO>> response = new ApiResponseDTO<>(
                true,
                "Mechanics retrieved successfully",
                mechanicDetails
        );
        return ResponseEntity.ok(response);
    }
}
