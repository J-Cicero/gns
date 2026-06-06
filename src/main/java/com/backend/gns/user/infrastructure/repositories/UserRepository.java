package com.backend.gns.user.infrastructure.repositories;

import com.backend.gns.user.domain.models.User;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<User, Long> {

  @Query("SELECT u FROM User u WHERE u.email = :email")
  Optional<User> findByEmail(@Param("email") String email);

  @Query("SELECT u FROM User u WHERE u.trackingId = :trackingId")
  Optional<User> findByTrackingId(@Param("trackingId") UUID trackingId);

  @Query("SELECT u FROM User u WHERE u.estActif = :isActive")
  List<User> findByIsActive(@Param("isActive") Boolean isActive);

  @Query("SELECT u FROM User u WHERE LOWER(u.prenom) LIKE LOWER(CONCAT('%', :firstName, '%'))")
  List<User> findByFirstNameContainingIgnoreCase(@Param("firstName") String firstName);

  @Query("SELECT u FROM User u WHERE LOWER(u.nom) LIKE LOWER(CONCAT('%', :lastName, '%'))")
  List<User> findByLastNameContainingIgnoreCase(@Param("lastName") String lastName);

  @Query("SELECT u FROM User u WHERE LOWER(u.email) LIKE LOWER(CONCAT('%', :email, '%'))")
  List<User> findByEmailContainingIgnoreCase(@Param("email") String email);

  @Query(
      "SELECT u FROM User u WHERE LOWER(u.nom) LIKE LOWER(CONCAT('%', :query, '%')) "
          + "OR LOWER(u.prenom) LIKE LOWER(CONCAT('%', :query, '%')) "
          + "OR LOWER(u.email) LIKE LOWER(CONCAT('%', :query, '%'))")
  List<User> searchUsers(@Param("query") String query);
}
