package com.it3030.smartcampus.member1.service;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.it3030.smartcampus.member1.model.Resource;
import com.it3030.smartcampus.member1.repository.ResourceRepository;

@Component
public class ResourceDataInitializer implements CommandLineRunner {

	private final ResourceRepository resourceRepository;

	public ResourceDataInitializer(ResourceRepository resourceRepository) {
		this.resourceRepository = resourceRepository;
	}

	@Override
	public void run(String... args) throws Exception {
		if (resourceRepository.count() == 0) {
			resourceRepository.save(new Resource("Main Auditorium", "HALL", true));
			resourceRepository.save(new Resource("Computer Lab 01", "LAB", true));
			resourceRepository.save(new Resource("Seminar Room A", "ROOM", true));
			resourceRepository.save(new Resource("Projector - Mobile Unit 1", "EQUIPMENT", true));
			resourceRepository.save(new Resource("Physics Lab", "LAB", true));
			System.out.println("Resource data pre-populated.");
		}
	}
}
