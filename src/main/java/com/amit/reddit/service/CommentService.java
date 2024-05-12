package com.amit.reddit.service;

import com.amit.reddit.dto.CommentDto;
import com.amit.reddit.exceptions.redditException;
import com.amit.reddit.exceptions.redditUserNotFoundException;
import com.amit.reddit.mapper.CommentMapper;
import com.amit.reddit.model.*;
import com.amit.reddit.repository.CommentRepository;
import com.amit.reddit.repository.PostRepository;
import com.amit.reddit.repository.UserRepository;
import com.amit.reddit.repository.VoteRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Transactional
@Slf4j
public class CommentService {

//    @Value("${popular.comments.threshold}")
//    private int POPULAR_COMMENTS_VOTE_THRESHOLD;

    private final String VIEW_REPLY="";
    private final CommentRepository commentRepository;
    private final VoteRepository voteRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final AuthService authService;
    private final CommentMapper commentMapper;
    private final MailService mailService;

    public void create(CommentDto commentDto) {
        Post post = getPostOfComment(commentDto.getPostId());
        User user=authService.getCurrentUser();
        if(!commentDto.getUsername().equals(user.getUsername())){
            throw new redditException("Sorry! something went wrong!");
        }
        Comment comment = commentMapper.mapDtoToComment(commentDto,post,user);
        comment.setRepliesCount(0);
        comment.setVotes(1);
        commentRepository.save(comment);
        Vote defaultVote=Vote.builder().post(post).comment(comment).user(user).voteType(VoteType.UPVOTE).build();
        voteRepository.save(defaultVote);
        post.setComments(post.getComments()+1);
        postRepository.save(post);
        NotificationEmail notificationEmail;
        if (commentDto.getParentId() != null) {
            Comment parentComment = commentRepository.findById(commentDto.getParentId())
                    .orElseThrow(() -> new redditException("The parent comment does not exist"));
            comment.setParentComment(parentComment);
            parentComment.addReply(comment);
            commentRepository.save(parentComment);
            notificationEmail= new NotificationEmail("u/"+user.getUsername()+" replied to your comment in r/"+post.getCommunity().getCommunityName(),parentComment.getUser().getEmail(),comment.getComment()+"/n"+VIEW_REPLY);
        } else {
            notificationEmail= new NotificationEmail("u/"+user.getUsername()+" commented in r/"+post.getCommunity().getCommunityName(),post.getUser().getEmail(),comment.getComment()+"/n"+VIEW_REPLY);
        }
        sendCommentNotificationEmail(notificationEmail);
    }

    public List<CommentDto> getAllPostComments(Long postId,Integer numberOfRepliesSection) {
        Post post = getPostOfComment(postId);
        return commentRepository.findAllByPostAndParentCommentIsNull(post)
                .stream()
                .map((comment)-> getCommentDtoWithReplies(comment,numberOfRepliesSection))
                .collect(Collectors.toList());
    }

    private CommentDto getCommentDtoWithReplies(Comment comment,Integer numberOfRepliesSection) {
        if(comment.getRepliesCount()==0) {
            return commentMapper.mapCommentToDto(comment);
        }

        CommentDto commentWithLayersOfReplies=commentMapper.mapCommentToDto(comment);
        List<Comment> replies = comment.getReplies();
        for(Comment reply: replies){
            if(numberOfRepliesSection>0 || (reply.getVotes() != null && reply.getVotes() > 3))
                commentWithLayersOfReplies.addReply(getCommentDtoWithReplies(reply,numberOfRepliesSection-1));
        }
        return commentWithLayersOfReplies;
    }

    private Post getPostOfComment(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new redditException("Sorry! The post no longer exists"));
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
        Post post = getPostOfComment(postId);
        Comment comment = getComment(commentId);
        //Check if the comment belongs to the User who requested
        User user= authService.getCurrentUser();
        if(user.equals(comment.getUser())){
            if(comment.getParentComment()!=null){
                Comment parentComment=comment.getParentComment();
                parentComment.setRepliesCount(parentComment.getRepliesCount()-1);
                commentRepository.save(parentComment);
            }
            comment.setComment("deleted");
            commentRepository.save(comment);
            post.setComments(post.getComments()-1);
            postRepository.save(post);
        } else {
            throw new redditException("No such comment exists for user: "+user.getUsername());
        }
        return;
    }

    private Comment getComment(Long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new redditException("Comment no longer exists!!"));
    }

    public List<CommentDto> getAllUserCommentsOnPost(Long postId) {
        User user = authService.getCurrentUser();
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> { throw new redditException("Post No Longer Exists!");} );
        return commentRepository.findAllByUserAndPost(user,post)
                .stream()
                .map((comment)->commentMapper.mapCommentToDto(comment))
                .collect(Collectors.toList());
    }

    public void edit(CommentDto commentDto) {
        Comment comment=getComment(commentDto.getCommentId());
        comment.setComment(commentDto.getComment());
        commentRepository.save(comment);
        Post post = getPostOfComment(comment.getPost().getPostId());
        User user=authService.getCurrentUser();
        NotificationEmail notificationEmail= new NotificationEmail("u/"+user.getUsername()+" updated a previous comment in r/"+post.getCommunity().getCommunityName(),authService.getCurrentUser().getEmail(),comment.getComment()+"/n"+VIEW_REPLY);
        sendCommentNotificationEmail(notificationEmail);
    }
}
