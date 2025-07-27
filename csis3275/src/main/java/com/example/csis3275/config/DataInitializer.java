package com.example.csis3275.config;

import com.example.csis3275.entities.Experience;
import com.example.csis3275.entities.ExperienceInstance;
import com.example.csis3275.entities.User;
import com.example.csis3275.repositories.ExperienceInstanceRepository;
import com.example.csis3275.repositories.ExperienceRepository;
import com.example.csis3275.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class DataInitializer {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ExperienceRepository experienceRepository;

    @Autowired
    private ExperienceInstanceRepository experienceInstanceRepository;

    @PostConstruct
    public void initData() {
        if (userRepository.count() == 0) {
            createInitialUsers();
        }

        if (experienceRepository.count() == 0) {
            createInitialExperiences();
        }
    }

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");


    private void createInitialExperiences() {
        Optional<User> userOptional = userRepository.findAll().stream().findFirst();
        if (userOptional.isEmpty()) {
            System.out.println("⚠️ No users found. Cannot create experiences without a user.");
            return;
        }

        User user = userOptional.get();
        System.out.println("🎯 Creating experiences for user: " + user.getUsername());

        Experience experience1 = new Experience();
        experience1.setTitle("Walking through English Bay");
        experience1.setDescription("Enjoy a peaceful walk along the beautiful English Bay waterfront. Experience stunning sunset views, street performers, and the vibrant beach atmosphere.");
        experience1.setShortDescription("Scenic waterfront walk with sunset views");
        experience1.setCountry("Canada");
        experience1.setCity("Vancouver");
        experience1.setLocation("English Bay Beach, Vancouver, BC");
        experience1.setMaxParticipants(15);
        experience1.setPrice(25.00);
        experience1.setDuration(2);
        experience1.setUser(user);
        experience1.setInstances(new ArrayList<>());
        experienceRepository.save(experience1);

        createInstancesForExperience(experience1, 25.00);

        Experience experience2 = new Experience();
        experience2.setTitle("Stanley Park Nature Tour");
        experience2.setDescription("Explore Vancouver's crown jewel - Stanley Park. Discover ancient forests, scenic seawall, totem poles, and learn about local wildlife and history.");
        experience2.setShortDescription("Guided tour through Stanley Park's highlights");
        experience2.setCountry("Canada");
        experience2.setCity("Vancouver");
        experience2.setLocation("Stanley Park, Vancouver, BC");
        experience2.setMaxParticipants(20);
        experience2.setPrice(35.00);
        experience1.setDuration(3);
        experience2.setUser(user);
        experience2.setInstances(new ArrayList<>());
        experienceRepository.save(experience2);

        createInstancesForExperience(experience2, 35.00);

        Experience experience3 = new Experience();
        experience3.setTitle("Granville Island Food Tour");
        experience3.setDescription("Taste your way through Granville Island Public Market and local eateries. Sample artisanal foods, fresh seafood, and local delicacies while learning about Vancouver's culinary scene.");
        experience3.setShortDescription("Culinary adventure through Granville Island");
        experience3.setCountry("Canada");
        experience3.setCity("Vancouver");
        experience3.setLocation("Granville Island, Vancouver, BC");
        experience3.setMaxParticipants(12);
        experience3.setPrice(55.00);
        experience1.setDuration(4);
        experience3.setUser(user);
        experience3.setInstances(new ArrayList<>());
        experienceRepository.save(experience3);

        createInstancesForExperience(experience3, 55.00);

        System.out.println("✅ Created 3 experiences with 3 instances each!");
        System.out.println("📅 Experiences scheduled for upcoming dates and times");
    }

    private void createInstancesForExperience(Experience experience, double price) {
        LocalDateTime baseTime = LocalDateTime.now().plusDays(3); // Start 3 days from now

        ExperienceInstance instance1 = new ExperienceInstance();
        instance1.setExperience(experience);
        instance1.setStartDateTime(baseTime.withHour(19).withMinute(0).withSecond(0).format(formatter));
        instance1.setEndDateTime(baseTime.withHour(19 + instance1.getExperience().getDuration()).withMinute(0).withSecond(0).format(formatter));
        instance1.setAvailableSlots(experience.getMaxParticipants());
        instance1.setPrice(price);
        experienceInstanceRepository.save(instance1);

        // Instance 2: Saturday at 7:00 PM
        ExperienceInstance instance2 = new ExperienceInstance();
        instance2.setExperience(experience);
        instance2.setStartDateTime(baseTime.withHour(15).withMinute(0).withSecond(0).format(formatter));
        instance2.setEndDateTime(baseTime.withHour(15 + instance2.getExperience().getDuration()).withMinute(0).withSecond(0).format(formatter));
        instance2.setAvailableSlots(experience.getMaxParticipants());
        instance2.setPrice(price);
        experienceInstanceRepository.save(instance2);

        // Instance 3: Sunday at 2:00 PM (afternoon option)
        ExperienceInstance instance3 = new ExperienceInstance();
        instance3.setExperience(experience);
        instance3.setStartDateTime(baseTime.withHour(10).withMinute(0).withSecond(0).format(formatter));
        instance3.setEndDateTime(baseTime.withHour(10 + instance3.getExperience().getDuration()).withMinute(0).withSecond(0).format(formatter));
        instance3.setAvailableSlots(experience.getMaxParticipants());
        instance3.setPrice(price);
        experienceInstanceRepository.save(instance3);

        // Add instances to experience
        List<ExperienceInstance> instances = new ArrayList<>();
        instances.add(instance1);
        instances.add(instance2);
        instances.add(instance3);
        experience.setInstances(instances);
        experienceRepository.save(experience);
    }

    private void createInitialUsers() {
        User user1 = new User();
        user1.setUsername("sora");
        user1.setPasswordHash(passwordEncoder.encode("sora123"));
        userRepository.save(user1);

        User user2 = new User();
        user2.setUsername("kairi");
        user2.setPasswordHash(passwordEncoder.encode("kairi456"));
        userRepository.save(user2);

        User user3 = new User();
        user3.setUsername("riku");
        user3.setPasswordHash(passwordEncoder.encode("riku789"));
        userRepository.save(user3);
    }
}