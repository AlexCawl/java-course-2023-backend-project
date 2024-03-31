package edu.java.scrapper.di;

import edu.java.core.retry.LinearBackOffPolicy;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.backoff.ExponentialBackOffPolicy;
import org.springframework.retry.backoff.FixedBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;

@Configuration
public class RetryBeanConfig {
    @Bean
    @ConditionalOnProperty(prefix = "app", name = "retry.type", havingValue = "fixed")
    public RetryTemplate constantRetryTemplate(ApplicationConfig config) {
        RetryTemplate template = new RetryTemplate();
        SimpleRetryPolicy retryPolicy = new SimpleRetryPolicy(config.retry().maxAttempts());
        FixedBackOffPolicy backOffPolicy = new FixedBackOffPolicy();

        backOffPolicy.setBackOffPeriod(config.retry().delayConfig().fixed().backOffPeriodDuration().toMillis());
        template.setRetryPolicy(retryPolicy);
        template.setBackOffPolicy(backOffPolicy);
        return template;
    }

    @Bean
    @ConditionalOnProperty(prefix = "app", name = "retry.type", havingValue = "linear")
    public RetryTemplate linearRetryTemplate(ApplicationConfig config) {
        RetryTemplate template = new RetryTemplate();
        SimpleRetryPolicy retryPolicy = new SimpleRetryPolicy(config.retry().maxAttempts());
        LinearBackOffPolicy backOffPolicy = new LinearBackOffPolicy();

        backOffPolicy.setInitialInterval(config.retry().delayConfig().linear().initialIntervalDuration().toMillis());
        backOffPolicy.setMaxInterval(config.retry().delayConfig().linear().maxIntervalDuration().toMillis());
        template.setRetryPolicy(retryPolicy);
        template.setBackOffPolicy(backOffPolicy);
        return template;
    }

    @Bean
    @ConditionalOnProperty(prefix = "app", name = "retry.type", havingValue = "exponential")
    public RetryTemplate exponentialRetryTemplate(ApplicationConfig config) {
        RetryTemplate template = new RetryTemplate();
        SimpleRetryPolicy policy = new SimpleRetryPolicy(config.retry().maxAttempts());
        ExponentialBackOffPolicy backOffPolicy = new ExponentialBackOffPolicy();

        backOffPolicy.setInitialInterval(config.retry().delayConfig().exponential().initialIntervalDuration().toMillis());
        backOffPolicy.setMaxInterval(config.retry().delayConfig().exponential().maxIntervalDuration().toMillis());
        template.setRetryPolicy(policy);
        template.setBackOffPolicy(backOffPolicy);
        return template;
    }
}
