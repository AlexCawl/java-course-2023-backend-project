package edu.java.bot.di;

import edu.java.core.kafka.Kafka;
import edu.java.core.retry.Retry;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "app", ignoreUnknownFields = false)
public record ApplicationConfig(
        @NotEmpty
        String telegramToken,

        @NotEmpty
        String scrapper,

        @NotNull
        Retry retry,

        @NotNull
        Kafka kafka,

        @NotNull
        Boolean useQueue,

        @NotNull
        Micrometer micrometer
) {
    public record Micrometer(
        @NotNull
        ProcessedMessagesCounter counter
    ) {
        public record ProcessedMessagesCounter(
            @NotNull
            String name,

            @NotNull
            String description
        ) {
        }
    }
}
