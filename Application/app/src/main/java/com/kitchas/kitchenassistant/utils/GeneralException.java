package com.kitchas.kitchenassistant.utils;

public class GeneralException extends Exception {
    private String error_code;

    public GeneralException(String message) {
        this(message, message);
    }

    public GeneralException(String message, String code) {
        super(message);
        this.error_code = code;
    }

    public void printError() {
        // Todo print error
    }
}
