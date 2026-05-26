package com.atipera.rekrutacja;

import com.github.tomakehurst.wiremock.WireMockServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class RepoIntegrationTest {

    private static final WireMockServer wireMockServer = new WireMockServer(0);

    @LocalServerPort
    private int port;

    private RestClient restClient;

    @TestConfiguration
    static class TestConfig {
        @Bean
        public RestClient.Builder restClientBuilder() {
            return RestClient.builder();
        }
    }

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        wireMockServer.start();
        registry.add("github.api.url", wireMockServer::baseUrl);
    }

    @BeforeEach
    void setUp() {
        wireMockServer.resetAll();
        restClient = RestClient.builder().baseUrl("http://localhost:" + port).build();
    }

    @AfterEach
    void tearDown() {
        wireMockServer.resetRequests();
    }

    @Test
    void shouldReturnRepositoriesWithBranchesWhenUserExists() {
        String username = "existingUser";
        
        wireMockServer.stubFor(get(urlEqualTo("/users/" + username + "/repos"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("""
                                [
                                  {"name": "repo-1", "owner": {"login": "existingUser"}, "fork": false},
                                  {"name": "repo-fork", "owner": {"login": "existingUser"}, "fork": true}
                                ]
                                """)));

        wireMockServer.stubFor(get(urlEqualTo("/repos/" + username + "/repo-1/branches"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("""
                                [
                                  {"name": "main", "commit": {"sha": "sha123456"}}
                                ]
                                """)));

        ResponseEntity<RepoDto[]> response = restClient.get()
                .uri("/api/repositories/" + username)
                .retrieve()
                .toEntity(RepoDto[].class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).hasSize(1);
        
        RepoDto repo = response.getBody()[0];
        assertThat(repo.repositoryName()).isEqualTo("repo-1");
        assertThat(repo.ownerLogin()).isEqualTo("existingUser");
        assertThat(repo.branches()).hasSize(1);
        assertThat(repo.branches().get(0).name()).isEqualTo("main");
        assertThat(repo.branches().get(0).lastCommitSha()).isEqualTo("sha123456");
    }

    @Test
    void shouldReturn404WhenGithubUserDoesNotExist() {
        String username = "nonExistingUser";
        wireMockServer.stubFor(get(urlEqualTo("/users/" + username + "/repos"))
                .willReturn(aResponse()
                        .withStatus(404)));

        try {
            restClient.get()
                    .uri("/api/repositories/" + username)
                    .retrieve()
                    .toEntity(ErrorResponseDto.class);
            
            fail("Expected HttpClientErrorException.NotFound to be thrown");
        } catch (HttpClientErrorException.NotFound ex) {
            assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
            ErrorResponseDto errorResponse = ex.getResponseBodyAs(ErrorResponseDto.class);
            assertThat(errorResponse).isNotNull();
            assertThat(errorResponse.status()).isEqualTo(404);
            assertThat(errorResponse.message()).isEqualTo("GitHub user 'nonExistingUser' not found");
        }
    }
}