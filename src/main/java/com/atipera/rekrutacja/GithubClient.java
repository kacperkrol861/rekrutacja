package com.atipera.rekrutacja;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import java.util.Arrays;
import java.util.List;

@Component
final class GithubClient {

    private final RestClient restClient;

    GithubClient(RestClient.Builder restClientBuilder, @Value("${github.api.url}") String baseUrl) {
        this.restClient = restClientBuilder.baseUrl(baseUrl).build();
    }

    List<GithubRepoResponse> fetchRepositories(String username) {
        try {
            GithubRepoResponse[] responses = restClient.get()
                    .uri("/users/{username}/repos", username)
                    .retrieve()
                    .body(GithubRepoResponse[].class);
            
            return responses != null ? Arrays.asList(responses) : List.of();
        } catch (HttpClientErrorException ex) {
            if (ex.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new UserNotFoundException(username);
            }
            throw ex;
        }
    }

    List<GithubBranchResponse> fetchBranches(String username, String repoName) {
        GithubBranchResponse[] responses = restClient.get()
                .uri("/repos/{username}/{repo}/branches", username, repoName)
                .retrieve()
                .body(GithubBranchResponse[].class);
        
        return responses != null ? Arrays.asList(responses) : List.of();
    }
}