package com.amit.reddit.service;

import com.amit.reddit.dto.VoteDto;
import com.amit.reddit.exceptions.redditException;
import com.amit.reddit.model.Post;
import com.amit.reddit.model.Vote;
import com.amit.reddit.model.VoteType;
import com.amit.reddit.repository.PostRepository;
import com.amit.reddit.repository.VoteRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class VoteService {

    private final VoteRepository voteRepository;
    private final PostRepository postRepository;
    private final AuthService authService;

    public void vote(VoteDto voteDto) {
        Post post = postRepository.findById(voteDto.getPostId())
                .orElseThrow(() -> new redditException("Sorry! The post no longer exists."));
        Vote vote = voteRepository.findByPostAndUser(post,authService.getCurrentUser())
                .orElse(Vote.builder().voteType(VoteType.NOVOTE).post(post).user(authService.getCurrentUser()).build());
        Integer voteCount = post.getVotes();
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
        post.setVotes(voteCount);
        postRepository.save(post);
    }
}
