package com.atipera.rekrutacja;

import java.util.List;

record RepoDto(String repositoryName, String ownerLogin, List<BranchDto> branches) {}