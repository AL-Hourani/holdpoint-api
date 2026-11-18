package com.alhourani.holdpoint.common.exception;

public class SessionNotFoundException extends RuntimeException {

    public SessionNotFoundException() {
        super("Session not found");
    }
}
