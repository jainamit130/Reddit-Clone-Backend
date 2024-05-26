package com.amit.reddit.repository;

import com.amit.reddit.model.Community;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommunityRepository extends JpaRepository<Community,Long> {
    boolean existsByCommunityNameIgnoreCase(String name);
    Optional<Community> findByCommunityName(String communityName);
    Optional<Community> findByPosts_PostId(Long postId);
    List<Community> findAllByCommunityNameStartsWithIgnoreCase(String prefix);

    @Query("SELECT c FROM Community c WHERE lower(cast(c.description as string)) LIKE %:searchQuery% OR lower(cast(c.communityName as string)) LIKE %:searchQuery%")
    List<Community> findAllByDescriptionOrCommunityNameContains(@Param("searchQuery") String search);
}
