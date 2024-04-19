package edu.java.scrapper.data.network.impl;

import edu.java.core.request.LinkUpdateRequest;
import edu.java.scrapper.data.network.NotificationConnector;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;

@Slf4j
@AllArgsConstructor
public class KafkaNotificationConnectorImpl implements NotificationConnector {
    private KafkaTemplate<Long, LinkUpdateRequest> kafkaTemplate;
    private String topicName;

    @Override
    public void update(LinkUpdateRequest request) {
        try {
            kafkaTemplate.send(topicName, request.id(), request);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }
}
