package io.github.wulkanowy.api;

public class NotLoggedInErrorException extends VulcanException {

    public NotLoggedInErrorException(String message) {
        super(message);
    }
}
