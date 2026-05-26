# GitHub Repositories Proxy API

A lightweight proxy service built with Spring Boot 4 and Java 25 that fetches and filters GitHub repositories for a given user.

## Features
- Lists all public GitHub repositories for a specified user.
- **Filters out fork repositories** (only original repositories are returned).
- Resolves branches and their latest commit SHA for each repository.
- Handles non-existing GitHub users by returning a standardized `404 Not Found` JSON response.
- Fully compliant with a strict synchronous architecture (no WebFlux used).
- Implemented within a single package adhering to the requested minimal Controller/Service/Client architecture.

## Tech Stack
- **Java 25**
- **Spring Boot 4.0.0-M1**
- **Gradle (Kotlin DSL)**
- **WireMock** (for integration testing without standard mocks)

## API Specification

### Get User Repositories
Returns a list of non-fork repositories with their branches.

- **URL:** `/api/repositories/{username}`
- **Method:** `GET`
- **Accept Header Requirement:** `application/json`
- **Success Response (200 OK):**
  ```json
  [
    {
      "repositoryName": "example-repo",
      "ownerLogin": "octocat",
      "branches": [
        {
          "name": "main",
          "lastCommitSha": "6dcb09b5b57875f334f61aebed695e2e4193db5e"
        }
      ]
    }
  ]