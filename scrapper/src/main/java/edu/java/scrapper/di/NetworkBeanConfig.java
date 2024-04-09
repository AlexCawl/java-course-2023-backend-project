package edu.java.scrapper.di;

import edu.java.core.request.LinkUpdateRequest;
import edu.java.core.util.ApiQualifier;
import edu.java.scrapper.data.network.NotificationConnector;
import edu.java.scrapper.data.network.impl.KafkaNotificationConnectorImpl;
import edu.java.scrapper.data.network.impl.RestNotificationConnectorImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;

@EnableCaching
@Configuration
@Slf4j
public class NetworkBeanConfig {
    @Bean
    @ApiQualifier("github")
    public String provideGithubEndpoint(ApplicationConfig config) {
        return config.api().github();
    }

    @Bean
    @ApiQualifier("stack-overflow")
    public String provideStackOverflowEndpoint(ApplicationConfig config) {
        return config.api().stackOverflow();
    }

    @Bean
    @ApiQualifier("bot")
    public String provideBotEndpoint(ApplicationConfig config) {
        return config.api().bot();
    }

    @Bean
    @ConditionalOnProperty(prefix = "app", name = "use-queue", havingValue = "true")
    public KafkaNotificationConnectorImpl kafkaClient(
            ApplicationConfig config,
            KafkaTemplate<Long, LinkUpdateRequest> kafkaTemplate
    ) {
        log.info("Working with kafka-client");
        return new KafkaNotificationConnectorImpl(kafkaTemplate, config.kafka().updates().name());
    }

    @Bean
    @ConditionalOnProperty(prefix = "app", name = "use-queue", havingValue = "false")
    public NotificationConnector restClient(@ApiQualifier("bot") String baseUrl) {
        log.info("Working with rest-client");
        return new RestNotificationConnectorImpl(baseUrl);
    }
}
