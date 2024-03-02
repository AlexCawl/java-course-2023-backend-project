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
    Api api
) {
    public record Scheduler(boolean enable, @NotNull Duration interval, @NotNull Duration forceCheckDelay) {
    }

    public record Api(@NotNull String github, @NotNull String stackOverflow) {
    }
}