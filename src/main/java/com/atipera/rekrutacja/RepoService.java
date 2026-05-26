package com.atipera.rekrutacja;

import org.springframework.stereotype.Service;
import java.util.List;

@Service
final class RepoService {

    private final GithubClient githubClient;

    RepoService(GithubClient githubClient) {
        this.githubClient = githubClient;
    }

    List<RepoDto> getUserRepositories(String username) {
        return githubClient.fetchRepositories(username).stream()
                .filter(repo -> !repo.fork())
                .map(repo -> {
                    List<BranchDto> branches = githubClient.fetchBranches(username, repo.name()).stream()
                            .map(branch -> new BranchDto(branch.name(), branch.commit().sha()))
                            .toList();
                    return new RepoDto(repo.name(), repo.owner().login(), branches);
                })
                .toList();
    }
}