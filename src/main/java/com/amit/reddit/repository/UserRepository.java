package com.amit.reddit.repository;

import com.amit.reddit.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {
    Optional<User> findByUsername(String username);
    boolean existsByUsername(String username);
    List<User> findByUsernameContainsAndVerifiedTrue(String searchQuery);
    Optional<User> findByUserIdAndVerifiedTrue(Long id);
}
