package ru.protei.dayPlanner.exception;

public class UserNotFoundException extends Exception {
    private long user_id;

    public UserNotFoundException(long user_id) {
        super(String.format("User is not found with id : '%s'", user_id));
    }
    public UserNotFoundException(String email) {
        super(String.format("User is not found with email : '%s'", email));
    }
}
