package edu.java.core.retry;

import lombok.SneakyThrows;
import org.springframework.retry.RetryContext;
import org.springframework.retry.backoff.BackOffContext;
import org.springframework.retry.backoff.BackOffInterruptedException;
import org.springframework.retry.backoff.BackOffPolicy;

public class LinearBackOffPolicy implements BackOffPolicy {
    private static final Long DEFAULT_INITIAL_INTERVAL = 1000L;
    private static final Long DEFAULT_MAX_INTERVAL = 30000L;

    private Long initialInterval = DEFAULT_INITIAL_INTERVAL;
    private Long maxInterval = DEFAULT_MAX_INTERVAL;

    @Override
    public BackOffContext start(RetryContext context) {
        return new LinearBackOffContext();
    }

    @SneakyThrows
    @Override
    public void backOff(BackOffContext backOffContext) throws BackOffInterruptedException {
        LinearBackOffContext linearBackOffContext = (LinearBackOffContext) backOffContext;
        Thread.sleep(Math.min(initialInterval * ++linearBackOffContext.attemptCount, maxInterval));
    }

    public void setInitialInterval(Long initialInterval) {
        if (initialInterval > 0) {
            this.initialInterval = initialInterval;
        }
    }

    public void setMaxInterval(Long maxInterval) {
        if (maxInterval > 0) {
            this.maxInterval = maxInterval;
        }
    }

    private static class LinearBackOffContext implements BackOffContext {
        private Integer attemptCount = 0;
    }
}