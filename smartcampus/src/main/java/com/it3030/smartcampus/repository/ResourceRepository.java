package com.it3030.smartcampus.repository;

import com.it3030.smartcampus.model.Resource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository for {@link Resource} (owned by Member 1 – Facilities).
 * Stub included here so Member 2 (Bookings) can compile independently.
 */
@Repository
public interface ResourceRepository extends JpaRepository<Resource, Long> {
    // Member 1 will add their custom queries here
}
