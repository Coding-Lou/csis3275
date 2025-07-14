package com.example.csis3275.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "book_orders")
public class BookOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderId;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "experience_instance_id")
    private ExperienceInstance experienceInstance;
    private Long experienceId;
    private Long guideId;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime bookingDateTime;
    private LocalDateTime tourStartDateTime;
    private OrderStatus orderStatus;
    private double transactionValue;
    private PaymentType paymentType;
    private Long paymentId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
