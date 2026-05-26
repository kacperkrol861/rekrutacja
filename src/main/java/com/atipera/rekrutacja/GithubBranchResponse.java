package com.atipera.rekrutacja;

record GithubBranchResponse(String name, Commit commit) {
    record Commit(String sha) {}
}