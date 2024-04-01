package com.amit.reddit.repository;

import com.amit.reddit.model.Community;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CommunityRepository extends JpaRepository<Community,Long> {
    boolean existsByCommunityNameIgnoreCase(String name);
    Optional<Community> findByCommunityName(String communityName);
}
