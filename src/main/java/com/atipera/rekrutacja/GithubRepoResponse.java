package com.atipera.rekrutacja;

record GithubRepoResponse(String name, Owner owner, boolean fork) {
    record Owner(String login) {}
}