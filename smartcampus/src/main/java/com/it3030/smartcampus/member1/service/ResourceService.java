package com.it3030.smartcampus.member1.service;

import com.it3030.smartcampus.member1.dto.ResourceResponse;
import com.it3030.smartcampus.member1.model.Resource;
import com.it3030.smartcampus.member1.repository.ResourceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
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

    public Resource getResourceById(@NonNull Long id) {
        Long resourceId = Objects.requireNonNull(id, "id must not be null");
        return resourceRepository.findById(resourceId)
                .orElseThrow(() -> new IllegalArgumentException("Resource not found with id: " + id));
    }

    public Resource createResource(@NonNull Resource resource) {
        return resourceRepository.save(Objects.requireNonNull(resource, "resource must not be null"));
    }

    public Resource updateResource(@NonNull Long id, @NonNull Resource updatedData) {
        Resource existing = getResourceById(id);
        Resource newData = Objects.requireNonNull(updatedData, "updatedData must not be null");
        existing.setName(newData.getName());
        existing.setType(newData.getType());
        existing.setAvailable(newData.getAvailable());
        return resourceRepository.save(existing);
    }

    public void deleteResource(@NonNull Long id) {
        resourceRepository.deleteById(Objects.requireNonNull(id, "id must not be null"));
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
