package com.amit.reddit.exceptions;

import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class redditUserNotFoundException extends UsernameNotFoundException {
    public redditUserNotFoundException(String username) {
        super("User name not found - " + username);
    }

    public redditUserNotFoundException() {
        super("User not found");
    }
}
