package com.example.test.atipera.demo.controllers;

import com.example.test.atipera.demo.exceptions.RateLimitExceededException;
import com.example.test.atipera.demo.exceptions.UserNotFoundException;
import com.example.test.atipera.demo.info.BranchInfo;
import com.example.test.atipera.demo.info.CommitInfo;
import com.example.test.atipera.demo.info.OwnerInfo;
import com.example.test.atipera.demo.info.RepositoryInfo;
import com.example.test.atipera.demo.services.GitHubApiService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(GitHubController.class)
class GitHubControllerTest {

    @MockBean
    private GitHubApiService gitHubApiService;

    @Autowired
    private MockMvc mvc;

    @Test
    void importsInfoToDTOSuccessfully() throws Exception {
        String username = "exampleUser";
        String repoName = "Hello-World";
        String branchName = "master";
        String branchSha = "sha";
        List<RepositoryInfo> repositoryInfo = List.of(new RepositoryInfo(repoName, new OwnerInfo(username), false,
                List.of(new BranchInfo(branchName, new CommitInfo(branchSha)))
        ));

        when(gitHubApiService.getGitHubUserInfo(username)).thenReturn(repositoryInfo);

        mvc.perform(get("/github/user/" + username).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].repoName").value(repoName))
                .andExpect(jsonPath("$[0].ownerLogin").value(username))
                .andExpect(jsonPath("$[0].branches[0].name").value(branchName))
                .andExpect(jsonPath("$[0].branches[0].lastCommitSha").value(branchSha));
    }

    @Test
    void returnsEmptyListIfRepositoryIsEmpty() throws Exception {
        String username = "exampleuser";
        List<RepositoryInfo> repositoryInfo = List.of();

        when(gitHubApiService.getGitHubUserInfo(username)).thenReturn(repositoryInfo);

        mvc.perform(get("/github/user/" + username).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void returnsUserNotFoundIfUserHasNotBeenFound() throws Exception {
        String username = "exampleuser";

        when(gitHubApiService.getGitHubUserInfo(username)).thenThrow(new UserNotFoundException());

        mvc.perform(get("/github/user/" + username).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("User not found"));
    }

    @Test
    void returnsRateLimitExceededIfGitHubLimitHasBeenExceeded() throws Exception {
        String username = "exampleuser";

        when(gitHubApiService.getGitHubUserInfo(username)).thenThrow(new RateLimitExceededException());

        mvc.perform(get("/github/user/" + username).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isTooManyRequests())
                .andExpect(jsonPath("$.status").value(429))
                .andExpect(jsonPath("$.message").value("Rate limit has been exceeded"));
    }
}