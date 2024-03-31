package edu.java.bot.data.impl;

import edu.java.bot.data.UserAuthRepository;
import edu.java.core.exception.ApiErrorException;
import edu.java.core.response.ApiErrorResponse;
import edu.java.core.util.ApiQualifier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class UserAuthRepositoryImpl implements UserAuthRepository {
    private static final String ENDPOINT = "/tg-chat/{id}";
    private final WebClient webClient;
    private RetryTemplate retryTemplate;

    public UserAuthRepositoryImpl(@ApiQualifier("scrapper") String baseUrl) {
        this.webClient = WebClient.create(baseUrl);
    }

    @Autowired
    public void setRetryTemplate(RetryTemplate retryTemplate) {
        this.retryTemplate = retryTemplate;
    }

    @Override
    public void registerUser(Long userId) throws ApiErrorException {
        if (retryTemplate == null) {
            internalRegisterUser(userId);
        } else {
            retryTemplate.execute(context -> {
                internalRegisterUser(userId);
                return null;
            });
        }
    }

    @Override
    public void deleteUser(Long userId) throws ApiErrorException {
        if (retryTemplate == null) {
            internalDeleteUser(userId);
        } else {
            retryTemplate.execute(context -> {
                internalDeleteUser(userId);
                return null;
            });
        }
    }

    private void internalRegisterUser(Long userId) throws ApiErrorException {
        webClient.post()
                .uri(ENDPOINT, userId)
                .retrieve()
                .onStatus(
                        HttpStatusCode::is4xxClientError,
                        clientResponse -> clientResponse.bodyToMono(ApiErrorResponse.class).map(ApiErrorException::new)
                )
                .toBodilessEntity()
                .block();
    }

    private void internalDeleteUser(Long userId) throws ApiErrorException {
        webClient.delete()
                .uri(ENDPOINT, userId)
                .retrieve()
                .onStatus(
                        HttpStatusCode::is4xxClientError,
                        clientResponse -> clientResponse.bodyToMono(ApiErrorResponse.class).map(ApiErrorException::new)
                )
                .toBodilessEntity()
                .block();
    }
}
