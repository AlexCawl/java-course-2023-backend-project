package edu.java.scrapper.di;

import jakarta.validation.constraints.NotNull;
import java.time.Duration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "app", ignoreUnknownFields = false)
public record ApplicationConfig(
    @NotNull
    Scheduler scheduler,

    @NotNull
    Api api,

    @NotNull
    DatabaseAccessType databaseAccessType,

    @NotNull
    Retry retry
) {
    public record Scheduler(boolean enable, @NotNull Duration interval, @NotNull Duration linkExpiration) {
    }

    public record Api(@NotNull String github, @NotNull String stackOverflow, @NotNull String bot) {
    }

    public enum DatabaseAccessType {
        JDBC,
        JPA
    }

    public record Retry(
            @NotNull
            Integer maxAttempts,

            @NotNull
            RetryType type,

            @NotNull
            Delay delay
    ) {
        public enum RetryType {
            FIXED,
            LINEAR,
            EXPONENTIAL
        }

        public record Delay(
                Fixed fixed,
                Linear linear,
                Exponential exponential
        ) {
            public record Fixed(
                    Duration intervalDuration
            ) {
            }

            public record Linear(
                    Duration initialIntervalDuration,
                    Duration maxIntervalDuration
            ) {
            }

            public record Exponential(
                    Duration initialIntervalDuration,
                    Duration maxIntervalDuration
            ) {
            }
        }
    }
}
