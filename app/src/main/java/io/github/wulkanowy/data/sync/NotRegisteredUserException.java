package io.github.wulkanowy.data.sync;

public class NotRegisteredUserException extends RuntimeException {

    public NotRegisteredUserException(String message) {
        super(message);
    }
}
