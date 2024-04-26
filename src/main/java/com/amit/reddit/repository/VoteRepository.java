package com.amit.reddit.repository;

import com.amit.reddit.model.Post;
import com.amit.reddit.model.User;
import com.amit.reddit.model.Vote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VoteRepository extends JpaRepository<Vote,Long> {

    Optional<Vote> findByPostAndUser(Post post, User currentUser);
}
