package com.example.capstone.githubtools.githubclient;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
public class GithubApiClient {

    private final RestTemplate restTemplate;

    public GithubApiClient() {
        this.restTemplate = new RestTemplate();
    }

    public List<Map<String, Object>> fetchCodeScanningAlerts(String owner, String repo, String pat) {
        String url = String.format("https://api.github.com/repos/%s/%s/code-scanning/alerts?state=all&per_page=100",
                owner, repo);
        HttpHeaders headers = createGithubHeaders(pat);
        ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                new HttpEntity<>(headers),
                new ParameterizedTypeReference<>() {}
        );
        return response.getBody() != null ? response.getBody() : Collections.emptyList();
    }

    public List<Map<String, Object>> fetchDependabotAlerts(String owner, String repo, String pat) {
        String url = String.format("https://api.github.com/repos/%s/%s/dependabot/alerts?per_page=100&state=open",
                owner, repo);
        HttpHeaders headers = createGithubHeaders(pat);
        ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                new HttpEntity<>(headers),
                new ParameterizedTypeReference<>() {}
        );
        return response.getBody() != null ? response.getBody() : Collections.emptyList();
    }

    public List<Map<String, Object>> fetchSecretScanningAlerts(String owner, String repo, String pat) {
        String url = String.format("https://api.github.com/repos/%s/%s/secret-scanning/alerts?per_page=100&state=open",
                owner, repo);
        HttpHeaders headers = createGithubHeaders(pat);
        ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                new HttpEntity<>(headers),
                new ParameterizedTypeReference<>() {}
        );
        return response.getBody() != null ? response.getBody() : Collections.emptyList();
    }

    private HttpHeaders createGithubHeaders(String pat) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + pat);
        headers.set("Accept", "application/vnd.github+json");
        return headers;
    }
}
