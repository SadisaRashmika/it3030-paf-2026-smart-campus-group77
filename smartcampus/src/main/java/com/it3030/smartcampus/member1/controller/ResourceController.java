package com.it3030.smartcampus.member1.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.it3030.smartcampus.member1.model.Resource;
import com.it3030.smartcampus.member1.service.ResourceService;

@RestController
@RequestMapping("/api/member1/resources")
public class ResourceController {

	private final ResourceService resourceService;

	public ResourceController(ResourceService resourceService) {
		this.resourceService = resourceService;
	}

	@GetMapping
	public List<Resource> getAllResources(@RequestParam(required = false) String type) {
		if (type != null && !type.isBlank()) {
			return resourceService.getResourcesByType(type);
		}
		return resourceService.getAllResources();
	}

	@GetMapping("/{id}")
	public Resource getResourceById(@PathVariable Long id) {
		return resourceService.getResourceById(id);
	}

	@PostMapping
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<Resource> createResource(@RequestBody Resource resource) {
		return ResponseEntity.status(HttpStatus.CREATED).body(resourceService.createResource(resource));
	}

	@PutMapping("/{id}/availability")
	@PreAuthorize("hasRole('ADMIN')")
	public Resource updateAvailability(@PathVariable Long id, @RequestParam boolean available) {
		return resourceService.updateAvailability(id, available);
	}

	@DeleteMapping("/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<Void> deleteResource(@PathVariable Long id) {
		resourceService.deleteResource(id);
		return ResponseEntity.noContent().build();
	}
}
