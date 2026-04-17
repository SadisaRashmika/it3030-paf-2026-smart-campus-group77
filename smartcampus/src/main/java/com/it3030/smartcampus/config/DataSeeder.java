package com.it3030.smartcampus.config;

import com.it3030.smartcampus.model.Resource;
import com.it3030.smartcampus.model.User;
import com.it3030.smartcampus.repository.ResourceRepository;
import com.it3030.smartcampus.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class DataSeeder {

    @Bean
    public CommandLineRunner loadData(ResourceRepository resourceRepository, UserRepository userRepository) {
        return args -> {
            if (userRepository.count() == 0) {
                User user = new User();
                user.setName("Demo User");
                user.setEmail("demo@student.campus.edu");
                user.setRole("USER");
                userRepository.save(user);

                User admin = new User();
                admin.setName("Admin User");
                admin.setEmail("admin@campus.edu");
                admin.setRole("ADMIN");
                userRepository.save(admin);
            }

            if (resourceRepository.count() == 0) {
                Resource r1 = new Resource();
                r1.setName("Lecture Hall A");
                r1.setLocation("Main Building");
                r1.setType("HALL");
                
                Resource r2 = new Resource();
                r2.setName("Computer Lab 3");
                r2.setLocation("IT Faculty");
                r2.setType("LAB");

                Resource r3 = new Resource();
                r3.setName("Projector 1");
                r3.setLocation("Storage Room");
                r3.setType("PROJECTOR");

                resourceRepository.saveAll(List.of(r1, r2, r3));
            }
        };
    }
}
