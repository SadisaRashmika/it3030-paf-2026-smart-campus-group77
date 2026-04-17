package com.it3030.smartcampus.model;

import jakarta.persistence.*;

/**
 * Represents a bookable campus resource (e.g., a lab, lecture hall, projector).
 * Provided by Member 1 (Facilities). Included here as a stub so that Member 2
 * (Bookings) can compile independently while Member 1 finalises their feature branch.
 */
@Entity
@Table(name = "resources")
public class Resource {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column
    private String location;

    @Column
    private String type; // e.g. LAB, PROJECTOR, HALL

    @Column
    private String description;

    // ---- Getters & Setters ----

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
