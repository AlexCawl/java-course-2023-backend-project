package edu.java.scrapper.data.network.impl;

import edu.java.core.response.github.CommitResponse;
import edu.java.core.response.github.RepositoryResponse;
import edu.java.core.util.ApiQualifier;
import edu.java.scrapper.data.network.GithubConnector;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class GithubConnectorImpl implements GithubConnector {
    private final WebClient webClient;
    private RetryTemplate retryTemplate;

    public GithubConnectorImpl(@ApiQualifier("github") String baseUrl) {
        this.webClient = WebClient
                .builder()
                .baseUrl(baseUrl)
                .build();
    }

    @Autowired
    public void setRetryTemplate(RetryTemplate retryTemplate) {
        this.retryTemplate = retryTemplate;
    }

    @Override
    public RepositoryResponse fetchRepository(String username, String repository) {
        if (retryTemplate == null) {
            return internalFetchRepository(username, repository);
        } else {
            return retryTemplate.execute(context -> internalFetchRepository(username, repository));
        }
    }

    @Override
    public List<CommitResponse> fetchRepositoryCommits(String username, String repository) {
        if (retryTemplate == null) {
            return internalFetchRepositoryCommits(username, repository);
        } else {
            return retryTemplate.execute(context -> internalFetchRepositoryCommits(username, repository));
        }
    }

    private RepositoryResponse internalFetchRepository(String username, String repository) {
        return webClient
                .get()
                .uri("/repos/{username}/{repository}", username, repository)
                .retrieve()
                .bodyToMono(RepositoryResponse.class)
                .block();
    }

    private List<CommitResponse> internalFetchRepositoryCommits(String username, String repository) {
        return webClient
                .get()
                .uri("/repos/{username}/{repository}/commits", username, repository)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<CommitResponse>>() {})
                .block();
    }
}
