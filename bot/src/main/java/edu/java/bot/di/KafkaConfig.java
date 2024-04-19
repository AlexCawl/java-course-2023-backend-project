package edu.java.bot.di;

import edu.java.bot.domain.NotifyUserForUpdatesUseCase;
import edu.java.bot.view.controller.KafkaUpdatesConsumer;
import edu.java.core.request.LinkUpdateRequest;
import java.util.Map;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.LongDeserializer;
import org.apache.kafka.common.serialization.LongSerializer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

@Configuration
@EnableKafka
public class KafkaConfig {
    @Bean
    public NewTopic scrapperUpdatesTopic(ApplicationConfig config) {
        return TopicBuilder
                .name(config.kafka().updates().name())
                .partitions(config.kafka().updates().partitions())
                .replicas(config.kafka().updates().replicas())
                .build();
    }

    @Bean
    public NewTopic scrapperUpdatesDeadLetterQueueTopic(ApplicationConfig config) {
        return TopicBuilder
                .name(config.kafka().updates().name() + "_dlq")
                .partitions(config.kafka().updates().partitions())
                .replicas(config.kafka().updates().replicas())
                .build();
    }

    @Bean
    public ConsumerFactory<Long, LinkUpdateRequest> linkUpdateRequestConsumerFactory(ApplicationConfig config) {
        return new DefaultKafkaConsumerFactory<>(Map.of(
                ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, config.kafka().servers(),
                ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, LongDeserializer.class,
                ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class,
                JsonDeserializer.TRUSTED_PACKAGES, "*",
                JsonDeserializer.USE_TYPE_INFO_HEADERS, false,
                JsonDeserializer.VALUE_DEFAULT_TYPE, LinkUpdateRequest.class
        ));
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<Long, LinkUpdateRequest>
    linkUpdateRequestConcurrentKafkaListenerContainerFactory(ConsumerFactory<Long, LinkUpdateRequest> consumerFactory) {
        ConcurrentKafkaListenerContainerFactory<Long, LinkUpdateRequest> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);
        return factory;
    }

    @Bean
    public ProducerFactory<Long, LinkUpdateRequest> linkUpdateRequestproducerFactory(ApplicationConfig config) {
        return new DefaultKafkaProducerFactory<>(Map.of(
                ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, config.kafka().servers(),
                ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, LongSerializer.class,
                ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class,
                JsonSerializer.ADD_TYPE_INFO_HEADERS, false
        ));
    }

    @Bean
    public KafkaTemplate<Long, LinkUpdateRequest> linkUpdateRequestKafkaTemplate(
            ProducerFactory<Long, LinkUpdateRequest> longLinkUpdateRequestProducerFactory
    ) {
        return new KafkaTemplate<>(longLinkUpdateRequestProducerFactory);
    }

    @Bean
    @ConditionalOnProperty(prefix = "app", name = "use-queue", havingValue = "true")
    public KafkaUpdatesConsumer kafkaUpdatesConsumer(
            ApplicationConfig applicationConfig,
            NotifyUserForUpdatesUseCase updatesUseCase,
            KafkaTemplate<Long, LinkUpdateRequest> kafkaTemplate
    ) {
        return new KafkaUpdatesConsumer(applicationConfig.kafka().updates().name(), updatesUseCase, kafkaTemplate);
    }
}
