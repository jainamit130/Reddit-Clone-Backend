package com.amit.reddit.service;

import com.amit.reddit.dto.CommentDto;
import com.amit.reddit.exceptions.redditException;
import com.amit.reddit.exceptions.redditUserNotFoundException;
import com.amit.reddit.mapper.CommentMapper;
import com.amit.reddit.model.*;
import com.amit.reddit.repository.CommentRepository;
import com.amit.reddit.repository.PostRepository;
import com.amit.reddit.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Transactional
@Slf4j
public class CommentService {
    private final String VIEW_REPLY="";
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final AuthService authService;
    private final CommentMapper commentMapper;
    private final MailService mailService;

    public void create(CommentDto commentDto) {
        Post post = postRepository.findById(commentDto.getPostId())
                .orElseThrow(() -> new redditException("Sorry! The post no longer exists"));
        User user=authService.getCurrentUser();
        Comment comment = commentMapper.mapDtoToComment(commentDto,post,user);
        commentRepository.save(comment);
        post.setComments(post.getComments()+1);
        postRepository.save(post);
        NotificationEmail notificationEmail= new NotificationEmail("u/"+user.getUsername()+" commented in r/"+post.getCommunity().getCommunityName(),authService.getCurrentUser().getEmail(),comment.getComment()+"/n"+VIEW_REPLY);
        sendCommentNotificationEmail(notificationEmail);
    }

    public List<CommentDto> getAllPostComments(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new redditException("Sorry! The post no longer exists"));
        return commentRepository.findAllByPost(post)
                .stream()
                .map((comment)->commentMapper.mapCommentToDto(comment))
                .collect(Collectors.toList());
    }

    public List<CommentDto> getAllUserComments(String userName) {
        User user = userRepository.findByUsername(userName)
                .orElseThrow(() -> new redditUserNotFoundException(userName));
        return commentRepository.findAllByUser(user)
                .stream()
                .map((comment)->commentMapper.mapCommentToDto(comment))
                .collect(Collectors.toList());
    }

    public void sendCommentNotificationEmail(NotificationEmail notificationEmail){
        mailService.sendMail(notificationEmail);
    }

    public void deleteComment(Long postId,Long commentId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new redditException("Sorry! The post no longer exists"));
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new redditException("Comment no longer exists!!"));
        //Check if the comment belongs to the User who requested
        User user= authService.getCurrentUser();
        if(user.equals(comment.getUser())){
            commentRepository.deleteById(commentId);
            post.setComments(post.getComments()-1);
            postRepository.save(post);
        } else {
            throw new redditException("No such comment exists for user: "+user.getUsername());
        }
        return;
    }
}
