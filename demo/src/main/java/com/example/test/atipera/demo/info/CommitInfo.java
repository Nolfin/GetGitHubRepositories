package com.example.test.atipera.demo.info;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CommitInfo {

    @JsonProperty("sha")
    private String branchSha;

    public CommitInfo() {
    }

    public CommitInfo(String branchSha) {
        this.branchSha = branchSha;
    }

    public String getBranchSha() {
        return branchSha;
    }
}