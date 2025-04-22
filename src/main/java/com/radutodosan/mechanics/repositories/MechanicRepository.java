package com.radutodosan.mechanics.repositories;

import com.radutodosan.mechanics.entities.Mechanic;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MechanicRepository extends JpaRepository<Mechanic, Long> {
    Optional<Mechanic> findByUsername(String username);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}
