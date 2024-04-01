package com.amit.reddit.exceptions;

public class communityNotFoundException extends redditException{

    public communityNotFoundException(String communityName) {
        super("Sorry no community found with name: "+communityName);
    }
}
