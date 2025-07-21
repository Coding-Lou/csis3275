package com.example.csis3275;

import com.example.csis3275.entities.*;
import com.example.csis3275.repositories.BookOrderRepository;
import com.example.csis3275.repositories.ExperienceInstanceRepository;
import com.example.csis3275.repositories.UserRepository;
import com.example.csis3275.security.JwtAuthenticationFilter;
import com.example.csis3275.services.MyUserDetailsService;
import com.example.csis3275.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SpringBootApplication
public class Csis3275Application {
    @Autowired
    private BookOrderRepository bookOrderRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ExperienceInstanceRepository experienceInstanceRepository;


    @Autowired
    private MyUserDetailsService userDetailsService;

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;
    
    public static void main(String[] args) {
        SpringApplication.run(Csis3275Application.class, args);
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll())
                        //.requestMatchers("/auth/**", "/", "/web/**", "/user/register").permitAll()
                        //.requestMatchers("/user/", "/create-experience", "/book-experience").authenticated()
                        //.anyRequest().authenticated())
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());
        daoAuthenticationProvider.setUserDetailsService(userDetailsService);
        return daoAuthenticationProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception{
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }


    @Bean
    CommandLineRunner initData(BookOrderRepository bookOrderRepository) {
        return args -> {
            // Check if data already exists before inserting
            if(bookOrderRepository.count() == 0) { // Or check for a specific student

                // Sample User obj, ExperienceInstance obj, Guide obj
                User user1 = new User();
                user1.setUsername("Alice Smith");

                user1 = userRepository.save(user1);

                User user2 = new User();
                user2.setUsername("Bob Johnson");
                user2 = userRepository.save(user2);

                // Create sample experience instances
                ExperienceInstance exp1 = new ExperienceInstance();
                exp1.setTitle("City Walking Tour");
                exp1.setDescription("Explore the historic city center");
                exp1.setGuideId(1);
                exp1.setStartDateTime(LocalDateTime.now().plusDays(5));
                exp1 = experienceInstanceRepository.save(exp1);

                ExperienceInstance exp2 = new ExperienceInstance();
                exp2.setTitle("Mountain Hike Adventure");
                exp2.setDescription("A thrilling hike in the mountains");
                exp2.setGuideId(2);
                exp2.setStartDateTime(LocalDateTime.now().plusDays(10));
                exp2 = experienceInstanceRepository.save(exp2);

                // Sample 1: Booked order
                bookOrderRepository.save(new BookOrder(
                        null, // orderId is auto-generated by DB
                        user1, // Associated User object
                        exp1,  // Associated ExperienceInstance object
                        exp1.getId(), // Use the generated ID of exp1 as experienceId
                        3L, // Use the guideId from exp1
                        LocalDateTime.now(), // bookingDateTime
                        exp1.getStartDateTime(), // tourStartDateTime (usually matches ExperienceInstance start time)
                        OrderStatus.BOOKED,
                        100.0,
                        PaymentType.CREDIT_CARD, // Could be null if no payment yet
                        null, // Payment ID (null for booked, or a specific ID if processed later)
                        LocalDateTime.now(), // createdAt
                        LocalDateTime.now()  // updatedAt
                ));
                System.out.println("BookOrder 1 saved for ExperienceInstance ID: " + exp1.getId());

                // Sample 2: Paid order
                bookOrderRepository.save(new BookOrder(
                        null,
                        user2,
                        exp2,
                        exp2.getId(), // Use the generated ID of exp2
                        2L,
                        LocalDateTime.now(),
                        exp2.getStartDateTime(),
                        OrderStatus.PAID,
                        150.0,
                        PaymentType.CREDIT_CARD,
                        201L, // Example Payment ID
                        LocalDateTime.now(),
                        LocalDateTime.now()
                ));
                System.out.println("BookOrder 2 saved for ExperienceInstance ID: " + exp2.getId());

                // Sample 3: Confirmed order (for user1, using exp2)
                bookOrderRepository.save(new BookOrder(
                        null,
                        user1,
                        exp2,
                        exp2.getId(),
                        2L,
                        LocalDateTime.now(),
                        exp2.getStartDateTime().plusHours(1), // Example: booking after start time, or custom
                        OrderStatus.CONFIRMED,
                        120.0,
                        PaymentType.CASH,
                        202L, // Example Payment ID
                        LocalDateTime.now(),
                        LocalDateTime.now()
                ));
                System.out.println("BookOrder 3 saved for ExperienceInstance ID: " + exp2.getId());

                // Sample 4: Refunded order (for user2, using exp1)
                bookOrderRepository.save(new BookOrder(
                        null,
                        user2,
                        exp1,
                        exp1.getId(),
                        1L,
                        LocalDateTime.now().minusDays(1), // Booked yesterday
                        exp1.getStartDateTime(),
                        OrderStatus.REFUNDED,
                        80.0,
                        PaymentType.PAYPAL,
                        203L, // Example Payment ID
                        LocalDateTime.now().minusDays(1),
                        LocalDateTime.now()
                ));
                System.out.println("BookOrder 4 saved for ExperienceInstance ID: " + exp1.getId());

                System.out.println("Initial book order and related data inserted.");
            } else {
                System.out.println("Data already exists, skipping initial data insertion.");
            }

            // Retrieve and print all orders to console for verification
            bookOrderRepository.findAll().forEach(p -> {
                System.out.println("Order ID: " + p.getOrderId() +
                        ", User: " + (p.getUser() != null ? p.getUser().getUsername() : "N/A") +
                        ", Experience: " + (p.getExperienceInstance() != null ? p.getExperienceInstance().getTitle() : "N/A") +
                        ", Experience ID (flat): " + p.getExperienceId() +
                        ", Guide ID (flat): " + p.getGuideId() +
                        ", Status: " + p.getOrderStatus() +
                        ", Value: " + p.getTransactionValue());
            });
        };
    }
}
