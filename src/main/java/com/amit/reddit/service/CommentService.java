package com.amit.reddit.service;

import com.amit.reddit.dto.CommentDto;
import com.amit.reddit.dto.PostResponseDto;
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
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final VoteService voteService;
    private final AuthService authService;
    private final CommentMapper commentMapper;
    private final MailService mailService;

    public CommentDto create(CommentDto commentDto) {
        Post post = getPostOfComment(commentDto.getPostId());
        User user=authService.getCurrentUser();
        if(!commentDto.getUsername().equals(user.getUsername())){
            throw new redditException("Sorry! something went wrong!");
        }
        Comment comment = commentMapper.mapDtoToComment(commentDto,post,user);
        comment.setRepliesCount(0);
        comment.setVotes(1);
        comment.setIsDeleted(false);
        commentRepository.save(comment);
        voteService.saveDefaultVote(post,comment);
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
        return commentToCommentResponse(post,comment);
    }

    public List<CommentDto> getAllPostComments(Long postId,Long commentId,Integer numberOfRepliesSection) {
        Post post = getPostOfComment(postId);
        Comment comment = null;
        Boolean regularFetchOfPopularComments = true;
        if(commentId!=null)
            comment=commentRepository.findById(commentId).orElseThrow(()-> {throw new redditException("Sorry! The Coment no longer exists");});
        else
            regularFetchOfPopularComments=false;
        final boolean finalRegularFetchOfPopularComments = regularFetchOfPopularComments;
        return commentRepository.findAllByPostAndParentComment(post,comment)
                .stream()
                .map((parentComment)-> getCommentDtoWithReplies(post,parentComment,numberOfRepliesSection,finalRegularFetchOfPopularComments))
                .collect(Collectors.toList());
    }

    private CommentDto getCommentDtoWithReplies(Post post,Comment comment,Integer numberOfRepliesSection,Boolean defaultFetch) {
        if(comment.getRepliesCount()==0) {
            return commentToCommentResponse(post,comment);
        }

        CommentDto commentWithLayersOfReplies=commentToCommentResponse(post,comment);
        List<Comment> replies = comment.getReplies();
        for(Comment reply: replies){
            if(numberOfRepliesSection>0 || (defaultFetch && (reply.getVotes() != null && reply.getVotes() > 3)))
                commentWithLayersOfReplies.addReply(getCommentDtoWithReplies(post,reply,numberOfRepliesSection-1,defaultFetch));
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
            comment.setComment("deleted");
            comment.setIsDeleted(true);
            commentRepository.save(comment);
            if(comment.getParentComment()!=null) {
                Comment parentComment = comment.getParentComment();
                List<Comment> replies = parentComment.getReplies();
                parentComment.removeReply(comment);
                commentRepository.save(parentComment);
            }
            post.setComments(post.getComments()-1);
            postRepository.save(post);
        } else {
            throw new redditException("No such comment exists for user: "+user.getUsername());
        }
        return;
    }

    public CommentDto getCommentBasedOnId(Long postId,Long commentId){
        Post post = getPostOfComment(postId);
        Comment comment = getComment(commentId);
        CommentDto commentDto = commentMapper.mapCommentToDto(comment);
        commentDto.setCurrentVote(voteService.getUserVote(post,comment));
        return commentDto;
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
                .map((comment)->commentToCommentResponse(post,comment))
                .collect(Collectors.toList());
    }

    public CommentDto edit(CommentDto commentDto) {
        Comment comment=getComment(commentDto.getCommentId());
        comment.setComment(commentDto.getComment());
        commentRepository.save(comment);
        Post post = getPostOfComment(comment.getPost().getPostId());
        User user=authService.getCurrentUser();
        CommentDto updatedComment = commentToCommentResponse(post,comment);
        NotificationEmail notificationEmail= new NotificationEmail("u/"+user.getUsername()+" updated a previous comment in r/"+post.getCommunity().getCommunityName(),authService.getCurrentUser().getEmail(),comment.getComment()+"/n"+VIEW_REPLY);
        sendCommentNotificationEmail(notificationEmail);
        return updatedComment;
    }

    public CommentDto commentToCommentResponse(Post post,Comment comment) {
        CommentDto commentDto = commentMapper.mapCommentToDto(comment);
        commentDto.setCurrentVote(voteService.getUserVote(post,comment));
        return commentDto;
    }
}
