package edu.java.scrapper.data.network.impl;

import edu.java.core.exception.ApiErrorException;
import edu.java.core.request.LinkUpdateRequest;
import edu.java.core.response.ApiErrorResponse;
import edu.java.core.util.ApiQualifier;
import edu.java.scrapper.data.network.NotificationConnector;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.reactive.function.client.WebClient;

@Slf4j
public class RestNotificationConnectorImpl implements NotificationConnector {
    private final WebClient webClient;

    public RestNotificationConnectorImpl(@ApiQualifier("bot") String baseUrl) {
        this.webClient = WebClient.create(baseUrl);
    }

    @Override
    public void update(LinkUpdateRequest request) {
        try {
            webClient.post()
                    .uri("/updates")
                    .bodyValue(request)
                    .retrieve()
                    .onStatus(
                            HttpStatusCode::is4xxClientError,
                            clientResponse -> clientResponse
                                    .bodyToMono(ApiErrorResponse.class)
                                    .map(ApiErrorException::new)
                    )
                    .toBodilessEntity()
                    .block();
        } catch (ApiErrorException apiErrorException) {
            log.warn(String.valueOf(apiErrorException));
        }
    }
}
