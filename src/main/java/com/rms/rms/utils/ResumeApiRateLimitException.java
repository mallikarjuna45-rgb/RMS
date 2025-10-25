package com.rms.rms.utils;

public class ResumeApiRateLimitException extends RuntimeException {
    public ResumeApiRateLimitException() {
        super("Too many requests Try again later");
    }
}
