package com.amit.reddit.model;

import com.amit.reddit.exceptions.redditException;

import java.util.Arrays;

public enum VoteType {
    UPVOTE(1),NOVOTE(0), DOWNVOTE(-1),
    ;

    private int direction;

    VoteType(int direction) {
        this.direction=direction;
    }

    public static VoteType lookup(Integer direction) {
        return Arrays.stream(VoteType.values())
                .filter(value -> value.getDirection().equals(direction))
                .findAny()
                .orElseThrow(() -> new redditException("Vote not found"));
    }

    public Integer getDirection() {
        return direction;
    }
}
