package com.example.test.atipera.demo.services;

import com.example.test.atipera.demo.exceptions.RateLimitExceededException;
import com.example.test.atipera.demo.exceptions.UserNotFoundException;
import com.example.test.atipera.demo.info.BranchInfo;
import com.example.test.atipera.demo.info.CommitInfo;
import com.example.test.atipera.demo.info.OwnerInfo;
import com.example.test.atipera.demo.info.RepositoryInfo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;

@RestClientTest(GitHubApiService.class)
class GitHubApiServiceTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private GitHubApiService gitHubApiService;

    @Autowired
    private MockRestServiceServer server;

    @Test
    void retrievesBranchesAndRepositoriesSuccessfully() throws JsonProcessingException {
        String username = "exampleUser";
        String repoName = "Hello-World";
        String branchName = "master";
        String branchSha = "sha";
        String apiUrl = "https://api.github.com/users/" + username + "/repos?type=all";

        List<RepositoryInfo> repositoriesMock = List.of(new RepositoryInfo(repoName, new OwnerInfo(username), false, List.of()));
        String repositoryResponse = objectMapper.writeValueAsString(
                repositoriesMock
        );

        String branchUrl = "https://api.github.com/repos/%s/%s/branches".formatted(username, repoName);

        List<BranchInfo> branchesMock = List.of(new BranchInfo(branchName, new CommitInfo(branchSha)));
        String branchResponse = objectMapper.writeValueAsString(
                branchesMock);
        server.expect(requestTo(apiUrl))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(repositoryResponse, MediaType.APPLICATION_JSON));

        server.expect(requestTo(branchUrl))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(branchResponse, MediaType.APPLICATION_JSON));

        List<RepositoryInfo> repositories = gitHubApiService.getGitHubUserInfo(username);

        assertThat(repositories).hasSize(repositoriesMock.size());

        RepositoryInfo repositoryInfo = repositories.getFirst();
        assertThat(repositoryInfo.getRepoName()).isEqualTo(repoName);
        assertThat(repositoryInfo.getOwner()).isNotNull().extracting(OwnerInfo::getOwnerLogin).isEqualTo(username);

        List<BranchInfo> branchesInfo = repositoryInfo.getBranches();
        assertThat(branchesInfo).hasSize(branchesMock.size());
        assertThat(branchesInfo.getFirst().getBranchName()).isEqualTo(branchName);
        assertThat(branchesInfo.getFirst().getCommit()).isNotNull().extracting(CommitInfo::getBranchSha).isEqualTo(branchSha);
    }

    @Test
    void returnsEmptyListIfThereAreNoRepositories() throws JsonProcessingException {
        String username = "exampleUser";
        String apiUrl = "https://api.github.com/users/" + username + "/repos?type=all";

        List<RepositoryInfo> repositoriesMock = List.of();
        String repositoryResponse = objectMapper.writeValueAsString(
                repositoriesMock
        );

        server.expect(requestTo(apiUrl))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(repositoryResponse, MediaType.APPLICATION_JSON));

        List<RepositoryInfo> repositories = gitHubApiService.getGitHubUserInfo(username);

        assertThat(repositories).hasSize(0);
    }

    @Test
    void returnsEmptyListIfThereAreNoBranches() throws JsonProcessingException {
        String username = "exampleUser";
        String repoName = "Hello-World";
        String apiUrl = "https://api.github.com/users/" + username + "/repos?type=all";

        List<RepositoryInfo> repositoriesMock = List.of(new RepositoryInfo(repoName, new OwnerInfo(username), false, List.of()));
        String repositoryResponse = objectMapper.writeValueAsString(
                repositoriesMock
        );

        String branchUrl = "https://api.github.com/repos/%s/%s/branches".formatted(username, repoName);

        List<BranchInfo> branchesMock = List.of();
        String branchResponse = objectMapper.writeValueAsString(
                branchesMock);
        server.expect(requestTo(apiUrl))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(repositoryResponse, MediaType.APPLICATION_JSON));

        server.expect(requestTo(branchUrl))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(branchResponse, MediaType.APPLICATION_JSON));

        List<RepositoryInfo> repositories = gitHubApiService.getGitHubUserInfo(username);

        assertThat(repositories).hasSize(repositoriesMock.size());

        List<BranchInfo> branchesInfo = repositories.getFirst().getBranches();
        assertThat(branchesInfo).hasSize(0);
    }

    @Test
    void throwsNotFoundIfThereIsNoUserFound(){
        String username = "exampleUser";
        String apiUrl = "https://api.github.com/users/" + username + "/repos?type=all";

        server.expect(requestTo(apiUrl))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withResourceNotFound());

        assertThatExceptionOfType(UserNotFoundException.class).isThrownBy(()->gitHubApiService.getGitHubUserInfo(username));
    }

    @Test
    void throwsNotFoundIfRateLimitWasExceeded(){
        String username = "exampleUser";
        String apiUrl = "https://api.github.com/users/" + username + "/repos?type=all";

        server.expect(requestTo(apiUrl))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withForbiddenRequest());

        assertThatExceptionOfType(RateLimitExceededException.class).isThrownBy(()->gitHubApiService.getGitHubUserInfo(username));
    }

    @Test
    void returnsEmptyBranchListIfRetrievedBranchesAreNull() throws JsonProcessingException {
        String username = "exampleUser";
        String repoName = "Hello-World";
        String apiUrl = "https://api.github.com/users/" + username + "/repos?type=all";

        List<RepositoryInfo> repositoriesMock = List.of(new RepositoryInfo(repoName, new OwnerInfo(username), false, List.of()));
        String repositoryResponse = objectMapper.writeValueAsString(
                repositoriesMock
        );

        String branchUrl = "https://api.github.com/repos/%s/%s/branches".formatted(username, repoName);

        server.expect(requestTo(apiUrl))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(repositoryResponse, MediaType.APPLICATION_JSON));

        server.expect(requestTo(branchUrl))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess("", MediaType.APPLICATION_JSON));

        List<RepositoryInfo> repositories = gitHubApiService.getGitHubUserInfo(username);

        assertThat(repositories).hasSize(repositoriesMock.size());

        List<BranchInfo> branchesInfo = repositories.getFirst().getBranches();
        assertThat(branchesInfo).hasSize(0);
    }
}