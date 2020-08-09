package com.drmodi.learn.reactive.fluxmonotesting;

public class CustomException extends Throwable {

    @Override
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    private String message;

    public CustomException(Throwable e) {
        this.message = e.getMessage();
    }
}
