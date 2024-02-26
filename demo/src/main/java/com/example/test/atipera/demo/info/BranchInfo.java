package com.example.test.atipera.demo.info;

import com.fasterxml.jackson.annotation.JsonProperty;

public class BranchInfo {

    @JsonProperty("name")
    private String branchName;

    @JsonProperty("commit")
    private CommitInfo commit;

    public BranchInfo() {
    }

    public BranchInfo(String branchName, CommitInfo commit) {
        this.branchName = branchName;
        this.commit = commit;
    }

    public String getBranchName() {
        return branchName;
    }

    public CommitInfo getCommit() {
        return commit;
    }
}