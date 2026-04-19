package com.it3030.smartcampus.member2.service;

import com.it3030.smartcampus.member2.dto.ResourceResponse;
import com.it3030.smartcampus.member2.model.Resource;
import com.it3030.smartcampus.member2.repository.ResourceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ResourceService {

    @Autowired
    private ResourceRepository resourceRepository;

    public List<ResourceResponse> getAllAvailableResources() {
        return resourceRepository.findByAvailable(true).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public Resource getResourceById(Long id) {
        return resourceRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Resource not found with id: " + id));
    }

    public Resource createResource(Resource resource) {
        return resourceRepository.save(resource);
    }

    public Resource updateResource(Long id, Resource updatedData) {
        Resource existing = getResourceById(id);
        existing.setName(updatedData.getName());
        existing.setType(updatedData.getType());
        existing.setAvailable(updatedData.getAvailable());
        return resourceRepository.save(existing);
    }

    public void deleteResource(Long id) {
        resourceRepository.deleteById(id);
    }

    private ResourceResponse mapToResponse(Resource resource) {
        ResourceResponse response = new ResourceResponse();
        response.setId(resource.getId());
        response.setName(resource.getName());
        response.setType(resource.getType());
        response.setAvailable(resource.getAvailable());
        return response;
    }
}
