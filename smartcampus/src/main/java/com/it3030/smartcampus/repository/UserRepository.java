package com.it3030.smartcampus.repository;

import com.it3030.smartcampus.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for {@link User} (owned by Member 4 – Auth/OAuth).
 * Stub included here so Member 2 (Bookings) can compile independently.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    // Member 4 will add their custom queries here
}
