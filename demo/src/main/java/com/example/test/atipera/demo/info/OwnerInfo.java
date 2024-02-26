package com.example.test.atipera.demo.info;

import com.fasterxml.jackson.annotation.JsonProperty;

public class OwnerInfo {

    @JsonProperty("login")
    private String ownerLogin;

    public OwnerInfo() {
    }

    public OwnerInfo(String ownerLogin) {
        this.ownerLogin = ownerLogin;
    }
    public String getOwnerLogin() {
        return ownerLogin;
    }
}