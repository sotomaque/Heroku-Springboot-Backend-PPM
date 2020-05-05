package com.sotomaque.ppmtool.exceptions;

public class UsernameAlreadyRegisteredResponse {

    private String username;

    public UsernameAlreadyRegisteredResponse(String username) {
        this.username = username;
    }

    // GETTER and SETTER methods
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
}
