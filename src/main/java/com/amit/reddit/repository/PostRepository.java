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

    @Query("SELECT p FROM Post p WHERE lower(cast(p.description as string)) LIKE %:searchQuery% OR lower(cast(p.postName as string)) LIKE %:searchQuery%")
    List<Post> findAllByDescriptionOrPostNameContains(@Param("searchQuery") String prefixSearch);
}
