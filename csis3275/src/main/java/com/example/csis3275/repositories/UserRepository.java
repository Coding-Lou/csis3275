package com.example.csis3275.repositories;

import com.example.csis3275.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    void deleteByUsername(String username);

    @Query("SELECT u from User u where LOWER(u.username) like LOWER(concat('%', :username ,'%')) ")
    List<User> searchByUsername(@Param("username") String username);

    @Query("SELECT u FROM User u WHERE u.username = :username")
    User getUserByUsername(@Param("username") String username);
}
