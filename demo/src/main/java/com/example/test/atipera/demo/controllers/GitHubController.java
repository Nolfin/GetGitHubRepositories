package com.example.test.atipera.demo.controllers;

import com.example.test.atipera.demo.dto.BranchDTO;
import com.example.test.atipera.demo.dto.RepositoryDTO;
import com.example.test.atipera.demo.services.GitHubApiService;
import jakarta.websocket.server.PathParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/github")
public class GitHubController {

    private final GitHubApiService gitHubApiService;

    public GitHubController(GitHubApiService gitHubApiService) {
        this.gitHubApiService = gitHubApiService;
    }

    @GetMapping("/user/{username}")
    @ResponseBody
    public List<RepositoryDTO> getGitHubUserInfo(@PathVariable String username) {
        return gitHubApiService.getGitHubUserInfo(username)
                .stream()
                .map(repositoryInfo -> new RepositoryDTO(repositoryInfo.getRepoName(),
                        repositoryInfo.getOwner().getOwnerLogin(),
                        repositoryInfo.getBranches()
                                .stream()
                                .map(branchInfo -> new BranchDTO(branchInfo.getBranchName(),
                                        branchInfo.getCommit().getBranchSha()))
                                .toList()))
                .toList();
    }
}