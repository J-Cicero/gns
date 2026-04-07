package com.backend.gns.Shared.user.infrastructure.repositories;

import com.backend.gns.Shared.user.domain.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, Long> {

    @Query("SELECT u FROM User u WHERE u.email = :email" )
    Optional<User> findByEmail(@Param("email") String email);

    @Query("SELECT u FROM User u WHERE u.trackingId = :trackingId")
    Optional<User> findByTrackingId(@Param("trackingId") UUID trackingId);

    @Query("SELECT u FROM User u WHERE u.active = :isActive")
    List<User> findByIsActive(@Param("isActive") Boolean isActive);

    @Query("SELECT u FROM User u WHERE LOWER(u.firstName) LIKE LOWER(CONCAT('%', :firstName, '%'))")
    List<User> findByFirstNameContainingIgnoreCase(@Param("firstName") String firstName);

    @Query("SELECT u FROM User u WHERE LOWER(u.lastName) LIKE LOWER(CONCAT('%', :lastName, '%'))")
    List<User> findByLastNameContainingIgnoreCase(@Param("lastName") String lastName);

    @Query("SELECT u FROM User u WHERE LOWER(u.email) LIKE LOWER(CONCAT('%', :email, '%'))")
    List<User> findByEmailContainingIgnoreCase(@Param("email") String email);
}
