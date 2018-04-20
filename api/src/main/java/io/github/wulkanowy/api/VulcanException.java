package io.github.wulkanowy.api;

public class VulcanException extends Exception {

    protected VulcanException(String message) {
        super(message);
    }

    protected VulcanException(String message, Exception e) {
        super(message, e);
    }
}
