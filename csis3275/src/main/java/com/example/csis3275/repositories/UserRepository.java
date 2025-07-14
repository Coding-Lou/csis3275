package com.example.csis3275.repositories;

import com.example.csis3275.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {
    @Query
    List<User> findUserById(long id);
}
