package com.amit.reddit.repository;

import com.amit.reddit.model.Community;
import com.amit.reddit.model.Post;
import com.amit.reddit.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post,Long> {
    List<Post> findAllByCommunity(Community community);

    List<Post> findAllByUser(User user);
}
