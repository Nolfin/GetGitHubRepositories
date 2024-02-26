package com.example.test.atipera.demo.info;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class RepositoryInfo {

    public RepositoryInfo() {
    }

    public RepositoryInfo(String repoName, OwnerInfo owner, boolean fork, List<BranchInfo> branches) {
        this.repoName = repoName;
        this.owner = owner;
        this.fork = fork;
        this.branches = branches;
    }

    @JsonProperty("name")
    private String repoName;

    @JsonProperty("owner")
    private OwnerInfo owner;

    @JsonProperty("fork")
    private boolean fork;

    private List<BranchInfo> branches;

    public String getRepoName() {
        return repoName;
    }

    public OwnerInfo getOwner() {
        return owner;
    }

    public List<BranchInfo> getBranches() {
        return branches;
    }

    public boolean isFork() {
        return fork;
    }
}