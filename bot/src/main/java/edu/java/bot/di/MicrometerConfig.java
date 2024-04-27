package edu.java.bot.di;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MicrometerConfig {
    @Bean
    public Counter buildProcessedMessagesCounter(MeterRegistry registry, ApplicationConfig config) {
        return Counter.builder(config.micrometer().counter().name())
            .description(config.micrometer().counter().description())
            .register(registry);
    }
}
