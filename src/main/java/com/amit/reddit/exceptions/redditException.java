package com.amit.reddit.exceptions;

import java.util.function.Supplier;

public class redditException extends RuntimeException {
    public redditException(String errorMessage) {
        super(errorMessage);
    }
}
