package com.amit.reddit.service;

import com.amit.reddit.dto.VoteDto;
import com.amit.reddit.exceptions.redditException;
import com.amit.reddit.model.Comment;
import com.amit.reddit.model.Post;
import com.amit.reddit.model.Vote;
import com.amit.reddit.model.VoteType;
import com.amit.reddit.repository.CommentRepository;
import com.amit.reddit.repository.PostRepository;
import com.amit.reddit.repository.VoteRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class VoteService {

    private final VoteRepository voteRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final AuthService authService;

    public void vote(VoteDto voteDto) {
        Post post = postRepository.findById(voteDto.getPostId())
                .orElseThrow(() -> new redditException("Sorry! The post no longer exists."));
        Vote vote=null;
        Integer voteCount=0;
        Comment comment = null;
        if(voteDto.getCommentId()!=null){
            comment = commentRepository.findById(voteDto.getCommentId())
                    .orElseThrow(()-> new redditException("Sorry! The comment no longer exists."));
            vote = voteRepository.findByPostAndCommentAndUser(post,comment,authService.getCurrentUser())
                    .orElse(Vote.builder().voteType(VoteType.NOVOTE).post(post).user(authService.getCurrentUser()).build());
            voteCount = comment.getVotes();
        } else {
            vote = voteRepository.findByPostAndCommentAndUser(post,null,authService.getCurrentUser())
                    .orElse(Vote.builder().voteType(VoteType.NOVOTE).post(post).user(authService.getCurrentUser()).build());
            voteCount = post.getVotes();
        }
        if(voteDto.getVoteType().equals(vote.getVoteType())) {
            if(voteDto.getVoteType().equals(VoteType.UPVOTE))
                voteCount -= 1;
            else if(voteDto.getVoteType().equals(VoteType.DOWNVOTE))
                voteCount += 1;
            vote.setVoteType(VoteType.NOVOTE);
        }
        else {
            if (voteDto.getVoteType().equals(VoteType.UPVOTE)) {
                if(vote.getVoteType()!=VoteType.NOVOTE) voteCount += 2;
                else voteCount += 1;
                vote.setVoteType(VoteType.UPVOTE);
            } else {
                if (vote.getVoteType()!=VoteType.NOVOTE) voteCount -= 2;
                else voteCount -= 1;
                vote.setVoteType(VoteType.DOWNVOTE);
            }
        }
        voteRepository.save(vote);
        if(voteDto.getCommentId()!=null){
            comment.setVotes(voteCount);
            commentRepository.save(comment);
        } else {
            post.setVotes(voteCount);
            postRepository.save(post);
        }
    }

    public VoteType getUserVote(Post post,Comment comment){
        Vote vote = Vote.builder().voteType(VoteType.NOVOTE).build();
        if(authService.isUserLoggedIn()) {
            vote = voteRepository.findByPostAndCommentAndUser(post,comment,authService.getCurrentUser())
                    .orElse(Vote.builder().voteType(VoteType.NOVOTE).build());
        }
        return vote.getVoteType();
    }

    public void saveDefaultVote(Post post) {
        Vote defaultVote=Vote.builder().post(post).user(authService.getCurrentUser()).voteType(VoteType.UPVOTE).build();
        voteRepository.save(defaultVote);
    }

    public void saveDefaultVote(Post post,Comment comment) {
        Vote defaultVote=Vote.builder().post(post).comment(comment).user(authService.getCurrentUser()).voteType(VoteType.UPVOTE).build();
        voteRepository.save(defaultVote);
    }
}
