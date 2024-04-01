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
        Vote vote = voteRepository.findByPostAndUser(post,authService.getCurrentUser());
        if(voteDto.getVoteType().equals(vote.getVoteType()))
            throw new redditException("You have already "+vote.getVoteType()+"D!");
        Integer voteCount = post.getVote();
        if(voteDto.getVoteType().equals(VoteType.UPVOTE)) {
            voteCount += 2;
            vote.setVoteType(VoteType.UPVOTE);
        }
        if(voteDto.getVoteType().equals(VoteType.DOWNVOTE)) {
            voteCount -= 2;
            vote.setVoteType(VoteType.DOWNVOTE);
        }
        voteRepository.save(vote);
        post.setVote(voteCount);
        postRepository.save(post);
    }
}
