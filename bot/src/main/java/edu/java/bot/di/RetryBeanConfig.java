package edu.java.bot.di;

import edu.java.core.retry.HttpCodeRetryPolicy;
import edu.java.core.retry.LinearBackOffPolicy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.RetryPolicy;
import org.springframework.retry.backoff.BackOffPolicy;
import org.springframework.retry.backoff.ExponentialBackOffPolicy;
import org.springframework.retry.backoff.FixedBackOffPolicy;
import org.springframework.retry.support.RetryTemplate;

@Slf4j
@Configuration
public class RetryBeanConfig {
    private static final String FIXED_RETRY_LOG = "FIXED retry policy with {}ms and {} max attempts";
    private static final String LINEAR_RETRY_LOG = "LINEAR retry policy with [{}; {}]ms and {} max attempts";
    private static final String EXPONENTIAL_RETRY_LOG = "EXPONENTIAL retry policy with [{}; {}]ms and {} max attempts";

    @Bean
    public RetryPolicy provideRetryPolicy(ApplicationConfig config) {
        return new HttpCodeRetryPolicy(
                config.retry().maxAttempts(),
                config.retry().codes()
        );
    }

    @Bean
    @ConditionalOnProperty(prefix = "app", name = "retry.type", havingValue = "fixed")
    public BackOffPolicy provideConstantBackOffPolicy(ApplicationConfig config) {
        FixedBackOffPolicy fixedBackOffPolicy = new FixedBackOffPolicy();
        fixedBackOffPolicy.setBackOffPeriod(config.retry().delay().fixed().intervalDuration().toMillis());
        log.info(
                FIXED_RETRY_LOG,
                config.retry().delay().fixed().intervalDuration().toMillis(),
                config.retry().maxAttempts()
        );
        return fixedBackOffPolicy;
    }

    @Bean
    @ConditionalOnProperty(prefix = "app", name = "retry.type", havingValue = "linear")
    public BackOffPolicy provideLinearBackOffPolicy(ApplicationConfig config) {
        LinearBackOffPolicy linearBackOffPolicy = new LinearBackOffPolicy();
        linearBackOffPolicy.setInitialInterval(config.retry().delay().linear().initialIntervalDuration().toMillis());
        linearBackOffPolicy.setMaxInterval(config.retry().delay().linear().maxIntervalDuration().toMillis());
        log.info(
                LINEAR_RETRY_LOG,
                config.retry().delay().linear().initialIntervalDuration().toMillis(),
                config.retry().delay().linear().maxIntervalDuration().toMillis(),
                config.retry().maxAttempts()
        );
        return linearBackOffPolicy;
    }

    @Bean
    @ConditionalOnProperty(prefix = "app", name = "retry.type", havingValue = "exponential")
    public BackOffPolicy provideExponentialBackOffPolicy(ApplicationConfig config) {
        ExponentialBackOffPolicy exponentialBackOffPolicy = new ExponentialBackOffPolicy();
        exponentialBackOffPolicy
                .setInitialInterval(config.retry().delay().exponential().initialIntervalDuration().toMillis());
        exponentialBackOffPolicy
                .setMaxInterval(config.retry().delay().exponential().maxIntervalDuration().toMillis());
        log.info(
                EXPONENTIAL_RETRY_LOG,
                config.retry().delay().exponential().initialIntervalDuration().toMillis(),
                config.retry().delay().exponential().maxIntervalDuration().toMillis(),
                config.retry().maxAttempts()
        );
        return exponentialBackOffPolicy;
    }

    @Bean
    public RetryTemplate provideRetryTemplate(RetryPolicy retryPolicy, BackOffPolicy backOffPolicy) {
        RetryTemplate template = new RetryTemplate();
        template.setRetryPolicy(retryPolicy);
        template.setBackOffPolicy(backOffPolicy);
        return template;
    }
}
