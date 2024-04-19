package edu.java.bot.view.controller;

import edu.java.bot.domain.NotifyUserForUpdatesUseCase;
import edu.java.core.request.LinkUpdateRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.handler.annotation.Payload;

@Log4j2
@RequiredArgsConstructor
public class KafkaUpdatesConsumer {
    private final String topicName;
    private final NotifyUserForUpdatesUseCase notifyUser;
    private final KafkaTemplate<Long, LinkUpdateRequest> kafkaTemplate;

    @KafkaListener(
            groupId = "scrapper.updates.listeners",
            topics = "${app.kafka.updates.name}",
            containerFactory = "linkUpdateRequestConcurrentKafkaListenerContainerFactory")
    public void listen(@Payload LinkUpdateRequest body) {
        try {
            body.tgChatIds().forEach(userId -> notifyUser.notify(userId, body.description(), body.url()));
        } catch (Exception e) {
            log.error(e);
            kafkaTemplate.send(topicName + "_dlq", body.id(), body);
        }
    }
}
