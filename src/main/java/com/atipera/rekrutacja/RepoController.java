package com.atipera.rekrutacja;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/api/repositories")
final class RepoController {

    private final RepoService repoService;

    RepoController(RepoService repoService) {
        this.repoService = repoService;
    }

    @GetMapping("/{username}")
    ResponseEntity<List<RepoDto>> getRepositories(@PathVariable String username) {
        List<RepoDto> repos = repoService.getUserRepositories(username);
        return ResponseEntity.ok(repos);
    }
}