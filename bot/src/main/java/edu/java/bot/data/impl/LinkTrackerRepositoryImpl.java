package edu.java.bot.data.impl;

import edu.java.bot.data.LinkTrackerRepository;
import edu.java.core.exception.ApiErrorException;
import edu.java.core.request.AddLinkRequest;
import edu.java.core.request.RemoveLinkRequest;
import edu.java.core.response.ApiErrorResponse;
import edu.java.core.response.ListLinksResponse;
import edu.java.core.util.ApiQualifier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatusCode;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class LinkTrackerRepositoryImpl implements LinkTrackerRepository {
    private static final String ENDPOINT = "/links";
    private static final String CHAT_ID_HEADER = "Tg-Chat-Id";
    private final WebClient webClient;
    private RetryTemplate retryTemplate;

    public LinkTrackerRepositoryImpl(@ApiQualifier("scrapper") String baseUrl) {
        this.webClient = WebClient.create(baseUrl);
    }

    @Autowired
    public void setRetryTemplate(RetryTemplate retryTemplate) {
        this.retryTemplate = retryTemplate;
    }

    @Override
    public ListLinksResponse getUserTrackedLinks(Long userId) throws ApiErrorException {
        if (retryTemplate == null) {
            return internalGetUserTrackedLinks(userId);
        } else {
            return retryTemplate.execute(context -> internalGetUserTrackedLinks(userId));
        }
    }

    @Override
    public void setLinkTracked(Long userId, String link) throws ApiErrorException {
        if (retryTemplate == null) {
            internalSetLinkTracked(userId, link);
        } else {
            retryTemplate.execute(context -> {
                internalSetLinkTracked(userId, link);
                return null;
            });
        }
    }

    @Override
    public void setLinkUntracked(Long userId, String link) throws ApiErrorException {
        if (retryTemplate == null) {
            internalSetLinkUntracked(userId, link);
        } else {
            retryTemplate.execute(context -> {
                internalSetLinkUntracked(userId, link);
                return null;
            });
        }
    }

    private ListLinksResponse internalGetUserTrackedLinks(Long userId) throws ApiErrorException {
        return webClient.get()
                .uri(ENDPOINT)
                .header(CHAT_ID_HEADER, String.valueOf(userId))
                .retrieve()
                .onStatus(
                        HttpStatusCode::is4xxClientError,
                        clientResponse -> clientResponse.bodyToMono(ApiErrorResponse.class).map(ApiErrorException::new)
                )
                .bodyToMono(ListLinksResponse.class)
                .block();
    }

    private void internalSetLinkTracked(Long userId, String link) throws ApiErrorException {
        webClient.post()
                .uri(ENDPOINT)
                .header(CHAT_ID_HEADER, String.valueOf(userId))
                .body(BodyInserters.fromValue(new AddLinkRequest(link)))
                .retrieve()
                .onStatus(
                        HttpStatusCode::is4xxClientError,
                        clientResponse -> clientResponse.bodyToMono(ApiErrorResponse.class).map(ApiErrorException::new)
                )
                .toBodilessEntity()
                .block();
    }

    private void internalSetLinkUntracked(Long userId, String link) throws ApiErrorException {
        webClient.method(HttpMethod.DELETE)
                .uri(ENDPOINT)
                .header(CHAT_ID_HEADER, String.valueOf(userId))
                .bodyValue(new RemoveLinkRequest(link))
                .retrieve()
                .onStatus(
                        HttpStatusCode::is4xxClientError,
                        clientResponse -> clientResponse.bodyToMono(ApiErrorResponse.class).map(ApiErrorException::new)
                )
                .toBodilessEntity()
                .block();
    }
}
