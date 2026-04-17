package com.it3030.smartcampus.member1.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.it3030.smartcampus.member1.model.Resource;
import com.it3030.smartcampus.member1.repository.ResourceRepository;

@Service
@Transactional
public class ResourceService {

	private final ResourceRepository resourceRepository;

	public ResourceService(ResourceRepository resourceRepository) {
		this.resourceRepository = resourceRepository;
	}

	public List<Resource> getAllResources() {
		return resourceRepository.findAll();
	}

	public List<Resource> getResourcesByType(String type) {
		return resourceRepository.findByType(type);
	}

	public Resource getResourceById(Long id) {
		return resourceRepository.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("Resource not found with id: " + id));
	}

	public Resource createResource(Resource resource) {
		return resourceRepository.save(resource);
	}

	public Resource updateAvailability(Long id, boolean available) {
		Resource resource = getResourceById(id);
		resource.setAvailable(available);
		return resourceRepository.save(resource);
	}

	public void deleteResource(Long id) {
		resourceRepository.deleteById(id);
	}
}
