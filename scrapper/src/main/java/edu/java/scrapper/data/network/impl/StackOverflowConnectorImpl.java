package edu.java.scrapper.data.network.impl;

import edu.java.core.response.stackoverflow.AnswerResponse;
import edu.java.core.response.stackoverflow.CommentResponse;
import edu.java.core.util.ApiQualifier;
import edu.java.scrapper.data.network.StackOverflowConnector;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class StackOverflowConnectorImpl implements StackOverflowConnector {
    private final WebClient webClient;
    private RetryTemplate retryTemplate;

    public StackOverflowConnectorImpl(@ApiQualifier("stack-overflow") String baseUrl) {
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
    public AnswerResponse fetchAnswers(String questionId) {
        if (retryTemplate == null) {
            return internalFetchAnswers(questionId);
        } else {
            return retryTemplate.execute(context -> internalFetchAnswers(questionId));
        }
    }

    @Override
    public CommentResponse fetchComments(String questionId) {
        if (retryTemplate == null) {
            return internalFetchComments(questionId);
        } else {
            return retryTemplate.execute(context -> internalFetchComments(questionId));
        }
    }

    private AnswerResponse internalFetchAnswers(String questionId) {
        return webClient
                .get()
                .uri("/questions/{questionId}/answers?site=stackoverflow", questionId)
                .retrieve()
                .bodyToMono(AnswerResponse.class)
                .block();
    }

    private CommentResponse internalFetchComments(String questionId) {
        return webClient
                .get()
                .uri("/questions/{questionId}/comments?site=stackoverflow", questionId)
                .retrieve()
                .bodyToMono(CommentResponse.class)
                .block();
    }
}
