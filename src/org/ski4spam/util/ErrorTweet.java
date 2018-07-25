package org.ski4spam.util;

public class ErrorTweet {
    private Long tweetId;
    private String error;

    public ErrorTweet(Long tweetId, String error) {
        this.tweetId = tweetId;
        this.error = error;
    }

    public Long getTweetId() {
        return tweetId;
    }

    public String getError() {
        return error;
    }
}
