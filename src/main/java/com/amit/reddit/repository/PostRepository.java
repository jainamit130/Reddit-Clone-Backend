package com.amit.reddit.repository;

import com.amit.reddit.dto.PostResponseDto;
import com.amit.reddit.model.Community;
import com.amit.reddit.model.Post;
import com.amit.reddit.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post,Long> {
    List<Post> findAllByCommunity(Community community);

    List<Post> findAllByUser(User user);

    List<Post> findAllByIsDeletedFalse();

    @Query(value = "SELECT * FROM Post p WHERE lower(strip_html_tags(p.description)) LIKE lower(concat('%', :searchQuery, '%')) OR lower(p.post_Name) LIKE lower(concat('%', :searchQuery, '%'))", nativeQuery = true)
    List<Post> findByDescriptionContainingOrPostNameContaining(@Param("searchQuery") String searchQuery);
}
