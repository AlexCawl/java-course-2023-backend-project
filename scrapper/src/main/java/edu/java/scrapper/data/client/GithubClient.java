package edu.java.scrapper.data.client;

import edu.java.core.response.github.GithubRepositoryResponse;

public interface GithubClient {
    GithubRepositoryResponse fetchRepository(String user, String repository);
}
