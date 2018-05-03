package io.github.wulkanowy.api;

public class VulcanException extends Exception {

    public VulcanException(String message) {
        super(message);
    }

    protected VulcanException(String message, Exception e) {
        super(message, e);
    }
}
