package com.it3030.smartcampus.member1.controller;

import com.it3030.smartcampus.member1.model.ResourceCategory;
import com.it3030.smartcampus.member1.service.ResourceCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/member1/categories")
public class ResourceCategoryController {

    @Autowired
    private ResourceCategoryService categoryService;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public List<ResourceCategory> getAllCategories() {
        return categoryService.getAllCategories();
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ResourceCategory> getCategoryById(@PathVariable Long id) {
        Optional<ResourceCategory> category = categoryService.getCategoryById(id);
        return category.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/name/{name}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ResourceCategory> getCategoryByName(@PathVariable String name) {
        Optional<ResourceCategory> category = categoryService.getCategoryByName(name);
        return category.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'RESOURCE_ADMINISTATOR')")
    public ResourceCategory createCategory(@RequestBody ResourceCategory category) {
        return categoryService.createCategory(category);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'RESOURCE_ADMINISTATOR')")
    public ResponseEntity<ResourceCategory> updateCategory(@PathVariable Long id, @RequestBody ResourceCategory categoryDetails) {
        try {
            ResourceCategory updatedCategory = categoryService.updateCategory(id, categoryDetails);
            return ResponseEntity.ok(updatedCategory);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'RESOURCE_ADMINISTATOR')")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        try {
            categoryService.deleteCategory(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/search")
    @PreAuthorize("isAuthenticated()")
    public List<ResourceCategory> searchCategories(@RequestParam String term) {
        return categoryService.searchCategories(term);
    }

    @GetMapping("/exists/{name}")
    @PreAuthorize("hasAnyRole('ADMIN', 'RESOURCE_ADMINISTATOR')")
    public ResponseEntity<Boolean> checkCategoryExists(@PathVariable String name) {
        boolean exists = categoryService.categoryExists(name);
        return ResponseEntity.ok(exists);
    }

    @PostMapping("/seed")
    @PreAuthorize("hasAnyRole('ADMIN', 'RESOURCE_ADMINISTATOR')")
    public ResponseEntity<String> seedDefaultCategories() {
        categoryService.seedDefaultCategories();
        return ResponseEntity.ok("Default categories seeded successfully");
    }
}
