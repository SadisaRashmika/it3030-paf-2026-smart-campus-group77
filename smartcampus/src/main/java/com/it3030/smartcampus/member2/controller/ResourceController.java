package com.it3030.smartcampus.member2.controller;

import com.it3030.smartcampus.member2.dto.ResourceResponse;
import com.it3030.smartcampus.member2.model.Resource;
import com.it3030.smartcampus.member2.service.ResourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/member2/resources")
public class ResourceController {

    @Autowired
    private ResourceService resourceService;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public List<ResourceResponse> getResources() {
        return resourceService.getAllAvailableResources();
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'RESOURCE_ADMINISTATOR')")
    public Resource createResource(@RequestBody Resource resource) {
        return resourceService.createResource(resource);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'RESOURCE_ADMINISTATOR')")
    public Resource updateResource(@PathVariable Long id, @RequestBody Resource resource) {
        return resourceService.updateResource(id, resource);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'RESOURCE_ADMINISTATOR')")
    public void deleteResource(@PathVariable Long id) {
        resourceService.deleteResource(id);
    }
}
