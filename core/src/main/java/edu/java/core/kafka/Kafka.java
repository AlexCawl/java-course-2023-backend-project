package edu.java.core.kafka;

public record Kafka(
        String servers,
        Updates updates
) {
    public record Updates(
            String name,
            Integer partitions,
            Integer replicas
    ) {
    }
}
