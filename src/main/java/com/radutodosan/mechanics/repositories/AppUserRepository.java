package com.radutodosan.mechanics.repositories;

import com.radutodosan.mechanics.entities.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AppUserRepository extends JpaRepository<AppUser, Long> {
    Optional<AppUser> findByUsername(String userName);
    boolean existsByUsername(String userName);
    boolean existsByEmail(String email);
}
