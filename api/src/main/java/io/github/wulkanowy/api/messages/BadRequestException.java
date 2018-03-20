package io.github.wulkanowy.api.messages;

import io.github.wulkanowy.api.VulcanException;

class BadRequestException extends VulcanException {

    BadRequestException(String message) {
        super(message);
    }
}
