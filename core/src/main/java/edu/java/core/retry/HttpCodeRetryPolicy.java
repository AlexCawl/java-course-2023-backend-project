package edu.java.core.retry;

import java.util.Set;
import org.springframework.retry.RetryContext;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.web.client.HttpClientErrorException;

public class HttpCodeRetryPolicy extends SimpleRetryPolicy {
    private final Set<Integer> httpCodes;

    public HttpCodeRetryPolicy(Integer maxAttempts, Set<Integer> httpCodes) {
        super(maxAttempts);
        this.httpCodes = httpCodes;
    }

    @Override
    public boolean canRetry(RetryContext context) {
        if (context.getLastThrowable() instanceof HttpClientErrorException e) {
            return httpCodes.contains(e.getStatusCode().value());
        }
        return super.canRetry(context);
    }
}
