package com.example.test.atipera.demo.services;

import com.example.test.atipera.demo.exceptions.RateLimitExceededException;
import com.example.test.atipera.demo.exceptions.UserNotFoundException;
import com.example.test.atipera.demo.info.BranchInfo;
import com.example.test.atipera.demo.info.RepositoryInfo;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

@Service
public class GitHubApiService {

    private final String GITHUB_API_URL = "https://api.github.com";

    private final RestTemplate restTemplate;

    public GitHubApiService(RestTemplateBuilder restTemplate) {
        this.restTemplate = restTemplate.build();
    }

    public List<RepositoryInfo> getGitHubUserInfo(String username) {
        String url = "%s/users/%s/repos?type=all".formatted(GITHUB_API_URL, username);
        try {
            RepositoryInfo[] repositories = restTemplate.getForObject(url, RepositoryInfo[].class);
            if (repositories == null) {return List.of();}
            return Arrays.stream(repositories)
                    .filter(repositoryInfo -> !repositoryInfo.isFork())
                    .map(repositoryInfo -> {
                        String branchUrl = "%s/repos/%s/%s/branches".formatted(
                                GITHUB_API_URL, repositoryInfo.getOwner().getOwnerLogin(),
                                repositoryInfo.getRepoName()
                        );
                        BranchInfo[] branches = restTemplate.getForObject(branchUrl, BranchInfo[].class);
                        List<BranchInfo> branchList = branches == null ? List.of() : Arrays.stream(branches).toList();
                        return new RepositoryInfo(
                                repositoryInfo.getRepoName(), repositoryInfo.getOwner(), repositoryInfo.isFork(),
                                branchList
                        );
                    })
                    .toList();
        } catch (HttpClientErrorException ex) {
            switch (ex.getStatusCode()) {
                case HttpStatus.NOT_FOUND -> throw new UserNotFoundException(ex);
                case HttpStatus.FORBIDDEN -> throw new RateLimitExceededException(ex);
                default -> throw new IllegalStateException("Failed to retrieve user repositories", ex);
            }
        }
    }
}