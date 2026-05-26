package com.atipera.rekrutacja;

final class UserNotFoundException extends RuntimeException {
    UserNotFoundException(String username) {
        super("GitHub user '" + username + "' not found");
    }
}