package com.amit.reddit.repository;

import com.amit.reddit.dto.CommentDto;
import com.amit.reddit.model.Comment;
import com.amit.reddit.model.Post;
import com.amit.reddit.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment,Long> {
    List<Comment> findAllByPostAndParentComment(Post post,Comment comment);

    List<Comment> findAllByUser(User user);

    List<Comment> findAllByUserAndPost(User user, Post post);

    List<Comment> findAllByCommentContainsIgnoreCase(String searchQuery);
}
