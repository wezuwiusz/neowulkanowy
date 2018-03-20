package io.github.wulkanowy.api.login;

import io.github.wulkanowy.api.VulcanException;

public class BadCredentialsException extends VulcanException {

    BadCredentialsException(String message) {
        super(message);
    }
}
