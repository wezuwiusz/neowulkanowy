package io.github.wulkanowy.api;

public abstract class VulcanException extends Exception {

    protected VulcanException(String message) {
        super(message);
    }
}
