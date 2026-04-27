package com.it3030.smartcampus.member1.service;

import com.it3030.smartcampus.member1.model.ResourceCategory;
import com.it3030.smartcampus.member1.repository.ResourceCategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ResourceCategoryService {

    @Autowired
    private ResourceCategoryRepository categoryRepository;

    public List<ResourceCategory> getAllCategories() {
        return categoryRepository.findByOrderByNameAsc();
    }

    public Optional<ResourceCategory> getCategoryById(Long id) {
        return categoryRepository.findById(id);
    }

    public Optional<ResourceCategory> getCategoryByName(String name) {
        return categoryRepository.findByName(name);
    }

    public ResourceCategory createCategory(ResourceCategory category) {
        if (categoryRepository.existsByName(category.getName())) {
            throw new IllegalArgumentException("Category with name '" + category.getName() + "' already exists");
        }
        
        category.setCreatedAt(LocalDateTime.now());
        category.setUpdatedAt(LocalDateTime.now());
        
        return categoryRepository.save(category);
    }

    public ResourceCategory updateCategory(Long id, ResourceCategory categoryDetails) {
        ResourceCategory category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found with id: " + id));

        // Check if name is being changed and if new name already exists
        if (!category.getName().equals(categoryDetails.getName()) && 
            categoryRepository.existsByName(categoryDetails.getName())) {
            throw new IllegalArgumentException("Category with name '" + categoryDetails.getName() + "' already exists");
        }

        category.setName(categoryDetails.getName());
        category.setDescription(categoryDetails.getDescription());
        category.setColor(categoryDetails.getColor());
        category.setIconName(categoryDetails.getIconName());
        category.setUpdatedAt(LocalDateTime.now());

        return categoryRepository.save(category);
    }

    public void deleteCategory(Long id) {
        ResourceCategory category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found with id: " + id));

        // Resources relationship removed - skip this check

        categoryRepository.delete(category);
    }

    public List<ResourceCategory> searchCategories(String searchTerm) {
        return categoryRepository.findByNameContainingIgnoreCase(searchTerm);
    }

    public boolean categoryExists(String name) {
        return categoryRepository.existsByName(name);
    }

    public void seedDefaultCategories() {
        if (categoryRepository.count() == 0) {
            createCategory(new ResourceCategory("Classroom", "Traditional classroom spaces for lectures and tutorials", "#3B82F6", "chalkboard"));
            createCategory(new ResourceCategory("Laboratory", "Science and computer labs for practical sessions", "#10B981", "flask"));
            createCategory(new ResourceCategory("Meeting Room", "Conference and meeting rooms for discussions", "#F59E0B", "users"));
            createCategory(new ResourceCategory("Library", "Library study areas and resource centers", "#8B5CF6", "book"));
            createCategory(new ResourceCategory("Sports Facility", "Gymnasium, sports fields, and fitness centers", "#EF4444", "heart"));
            createCategory(new ResourceCategory("Auditorium", "Large halls for presentations and events", "#6366F1", "microphone"));
            createCategory(new ResourceCategory("Computer Lab", "Specialized computer facilities", "#14B8A6", "computer"));
            createCategory(new ResourceCategory("Study Room", "Quiet study spaces for individual or group work", "#84CC16", "book-open"));
        }
    }
}
