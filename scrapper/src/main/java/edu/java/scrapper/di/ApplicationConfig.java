package edu.java.scrapper.di;

import edu.java.core.retry.Retry;
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
}
