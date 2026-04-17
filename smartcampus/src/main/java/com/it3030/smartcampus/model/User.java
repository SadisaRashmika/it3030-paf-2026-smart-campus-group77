package com.it3030.smartcampus.model;

import jakarta.persistence.*;

/**
 * Represents an authenticated campus user.
 * Provided by Member 4 (Auth/OAuth). Included here as a stub so that Member 2
 * (Bookings) can compile independently while Member 4 finalises their feature branch.
 */
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String role; // e.g. USER, ADMIN

    // ---- Getters & Setters ----

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
}
