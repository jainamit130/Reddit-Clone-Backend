package com.amit.reddit.repository;

import com.amit.reddit.model.Post;
import com.amit.reddit.model.User;
import com.amit.reddit.model.Vote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VoteRepository extends JpaRepository<Vote,Long> {

    Vote findByPostAndUser(Post post, User currentUser);
}
