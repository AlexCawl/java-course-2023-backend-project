package edu.java.scrapper.di;

import jakarta.validation.constraints.NotNull;
import java.time.Duration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "app", ignoreUnknownFields = false)
public record ApplicationConfig(
    @NotNull
    SchedulerConfiguration scheduler,

    @NotNull
    ApiConfiguration api,

    @NotNull
    DatabaseAccessType databaseAccessType,

    @NotNull
    NetworkRetryConfiguration retry
) {
    public record SchedulerConfiguration(boolean enable, @NotNull Duration interval, @NotNull Duration linkExpiration) {
    }

    public record ApiConfiguration(@NotNull String github, @NotNull String stackOverflow, @NotNull String bot) {
    }

    public enum DatabaseAccessType {
        JDBC,
        JPA
    }

    public record NetworkRetryConfiguration(
            Integer maxAttempts,
            RetryType type,
            NetworkDelayConfiguration delayConfig
    ) {
        public enum RetryType {
            CONSTANT,
            LINEAR,
            EXPONENTIAL
        }

        public record NetworkDelayConfiguration(
                ConstantDelayConfiguration constant,
                LinearDelayConfiguration linear,
                ExponentialDelayConfiguration exponential
        ) {
            public record ConstantDelayConfiguration(
                    Duration backOffPeriodDuration
            ) {
            }

            public record LinearDelayConfiguration(
                    Duration initialIntervalDuration,
                    Duration maxIntervalDuration
            ) {
            }

            public record ExponentialDelayConfiguration(
                    Duration initialIntervalDuration,
                    Duration maxIntervalDuration
            ) {
            }
        }
    }
}
